package roomescape.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.util.time.DateTime;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservation.Waiting;
import roomescape.domain.reservation.WaitingRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.dto.auth.info.LoginMemberInfo;
import roomescape.dto.member.response.MemberResponse;
import roomescape.dto.reservation.ReservationSearchCondition;
import roomescape.dto.reservation.ReservationWithPayment;
import roomescape.dto.reservation.WaitingWithRank;
import roomescape.dto.reservation.request.ReservationRequest;
import roomescape.dto.reservation.request.ReservationWithPaymentRequest;
import roomescape.dto.reservation.response.ReservationMineResponse;
import roomescape.dto.reservation.response.ReservationResponse;
import roomescape.dto.reservation.response.WaitingResponse;
import roomescape.dto.theme.response.ThemeResponse;
import roomescape.dto.timeslot.response.TimeSlotResponse;
import roomescape.exception.member.MemberException;
import roomescape.exception.reservation.ReservationException;
import roomescape.exception.theme.ThemeException;
import roomescape.exception.timeslot.TimeSlotException;
import roomescape.infrastructure.payment.toss.dto.request.TossPaymentRequest;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final DateTime dateTime;
    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentService paymentService;
    
    @Transactional
    public ReservationResponse createReservationWithPayment(final ReservationWithPaymentRequest request,
                                                            final Long memberId) {
        ReservationRequest reservationRequest = new ReservationRequest(request.date(), request.timeId(), request.themeId());
        Reservation reservation = createReservation(reservationRequest, memberId);

        TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(request.paymentKey(), request.orderId(), request.amount());
        paymentService.confirmPayment(tossPaymentRequest, reservation);

        return ReservationResponse.from(reservation);
    }

    @Transactional
    public ReservationResponse createReservationWithoutPayment(final ReservationRequest request, final Long memberId) {
        Reservation reservation = createReservation(request, memberId);
        return ReservationResponse.from(reservation);
    }

    private Reservation createReservation(final ReservationRequest request, final Long memberId) {
        TimeSlot time = timeSlotRepository.findById(request.timeId())
                .orElseThrow(() -> new TimeSlotException("예약 시간을 찾을 수 없습니다."));
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new ThemeException("테마를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("멤버를 찾을 수 없습니다."));

        List<Reservation> reservations = reservationRepository.findAllByDateAndThemeId(request.date(),
                request.themeId());
        validateExistDuplicateReservation(reservations, time);

        Reservation reservation = Reservation.createWithoutId(request.date(), time, theme, member, Status.RESERVED);
        validateCanReserveDateTime(reservation, dateTime.now());

        reservation = reservationRepository.save(reservation);

        return reservation;
    }

    @Transactional
    public WaitingResponse createWaiting(final ReservationRequest request, final Long memberId) {
        Reservation reservation = reservationRepository.findBy(request.date(), request.timeId(), request.themeId())
                .orElseThrow(() -> new ReservationException("예약 정보가 없습니다."));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException("멤버를 찾을 수 없습니다."));

        validateNotReservationOwner(reservation, member);
        validateCanReserveDateTime(reservation, dateTime.now());
        validateDuplicateWaiting(reservation, member);

        Waiting waiting = waitingRepository.save(Waiting.createWithoutId(reservation, member));
        return WaitingResponse.from(waiting);
    }

    private void validateNotReservationOwner(final Reservation reservation, final Member member) {
        if (reservation.isSameMember(member)) {
            throw new ReservationException("예약자는 예약대기를 할 수 없습니다.");
        }
    }

    private void validateDuplicateWaiting(final Reservation reservation, final Member member) {
        if (waitingRepository.existsByReservationIdAndMemberId(reservation.id(), member.id())) {
            throw new ReservationException("이미 예약대기 중입니다.");
        }
    }

    private void validateExistDuplicateReservation(final List<Reservation> reservations, final TimeSlot time) {
        boolean isBooked = reservations.stream()
                .anyMatch(reservation -> reservation.isSameTime(time));

        if (isBooked) {
            throw new ReservationException("이미 예약이 존재합니다.");
        }
    }

    private void validateCanReserveDateTime(final Reservation reservation, final LocalDateTime now) {
        if (reservation.isCannotReserveDateTime(now)) {
            throw new ReservationException("예약할 수 없는 날짜와 시간입니다.");
        }
    }

    public List<ReservationResponse> getReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteReservationById(final Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ReservationException("예약을 찾을 수 없습니다."));

        List<Waiting> waitings = waitingRepository.findAllByReservationId(id);
        if (waitings.isEmpty()) {
            reservationRepository.deleteById(id);
            return;
        }

        Waiting firstWaiting = waitings.getFirst();
        reservation.updateMember(firstWaiting.member());

        waitingRepository.deleteById(firstWaiting.id());
    }

    @Transactional
    public void deleteWaiting(final Long waitingId) {
        waitingRepository.findById(waitingId)
                .orElseThrow(() -> new ReservationException("예약 대기를 찾을 수 없습니다."));

        waitingRepository.deleteById(waitingId);
    }

    public List<ReservationResponse> searchReservationWithCondition(final ReservationSearchCondition condition) {
        List<Reservation> reservations = reservationRepository.findAllByMemberIdAndThemeIdAndDateBetween(
                condition.memberId(), condition.themeId(),
                condition.dateFrom(), condition.dateTo()
        );

        return reservations.stream()
                .map(reservation -> new ReservationResponse(
                        reservation.id(),
                        reservation.date(),
                        new TimeSlotResponse(reservation.time().id(), reservation.time().startAt()),
                        new ThemeResponse(reservation.theme().id(), reservation.theme().name(),
                                reservation.theme().description(), reservation.theme().thumbnail()),
                        new MemberResponse(reservation.member().id(), reservation.member().getName())
                ))
                .toList();
    }

    public List<ReservationMineResponse> getMemberReservations(final LoginMemberInfo loginMemberInfo) {
        Member member = memberRepository.findById(loginMemberInfo.id())
                .orElseThrow(() -> new MemberException("멤버를 찾을 수 없습니다."));

        List<ReservationWithPayment> reservationsWithPayment = reservationRepository.findAllWithPaymentByMemberId(member.id());
        List<Reservation> reservationsWithoutPayment = reservationRepository.findAllWithoutPaymentByMemberId(member.id());
        List<WaitingWithRank> waitingsWithRank = waitingRepository.findAllWithRankByMemberId(member.id());

        return Stream.of(
                        reservationsWithPayment.stream().map(ReservationMineResponse::from),
                        reservationsWithoutPayment.stream().map(ReservationMineResponse::from),
                        waitingsWithRank.stream().map(ReservationMineResponse::from)
                )
                .flatMap(Function.identity())
                .sorted(Comparator.comparing(ReservationMineResponse::date))
                .toList();
    }

    public List<ReservationResponse> findAllWaitings() {
        List<Waiting> waitings = waitingRepository.findAll();

        return waitings.stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
