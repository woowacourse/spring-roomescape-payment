package roomescape.reservation.application;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.auth.dto.info.LoginMemberInfo;
import roomescape.common.util.time.DateTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.exception.MemberException;
import roomescape.payment.application.PaymentService;
import roomescape.payment.infrastructure.dto.request.TossPaymentRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.domain.WaitingRepository;
import roomescape.reservation.dto.ReservationSearchCondition;
import roomescape.reservation.dto.ReservationWithPayment;
import roomescape.reservation.dto.WaitingWithRank;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationMineResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingResponse;
import roomescape.reservation.exception.ReservationException;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.exception.ThemeException;
import roomescape.timeslot.domain.TimeSlot;
import roomescape.timeslot.domain.TimeSlotRepository;
import roomescape.timeslot.dto.response.TimeSlotResponse;
import roomescape.timeslot.exception.TimeSlotException;

@Service
@Transactional(readOnly = true)
public class ReservationService {

    private final DateTime dateTime;
    private final ReservationRepository reservationRepository;
    private final TimeSlotRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentService paymentService;

    public ReservationService(
            final DateTime dateTime,
            final ReservationRepository reservationRepository,
            final TimeSlotRepository reservationTimeRepository,
            final ThemeRepository themeRepository,
            final MemberRepository memberRepository,
            final WaitingRepository waitingRepository,
            final PaymentService paymentService
    ) {
        this.dateTime = dateTime;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentService = paymentService;
    }

    @Transactional
    public ReservationResponse createReservationWithPayment(final ReservationWithPaymentRequest request,
                                                            final Long memberId) {
        ReservationRequest reservationRequest = new ReservationRequest(request.date(), request.timeId(),
                request.themeId());
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
        TimeSlot time = reservationTimeRepository.findById(request.timeId())
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
        if (waitingRepository.existsByReservationIdAndMemberId(reservation.getId(), member.getId())) {
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

        List<Waiting> waitings = waitingRepository.findByReservationId(id);
        if (waitings.isEmpty()) {
            reservationRepository.deleteById(id);
            return;
        }

        Waiting firstWaiting = waitings.getFirst();
        reservation.setMember(firstWaiting.getMember());

        waitingRepository.deleteById(firstWaiting.getId());
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
                        reservation.getId(),
                        reservation.getDate(),
                        new TimeSlotResponse(reservation.getTime().getId(), reservation.getTime().getStartAt()),
                        new ThemeResponse(reservation.getTheme().getId(), reservation.getTheme().getName(),
                                reservation.getTheme().getDescription(), reservation.getTheme().getThumbnail()),
                        new MemberResponse(reservation.getMember().getId(), reservation.getMember().getName())
                ))
                .toList();
    }

    public List<ReservationMineResponse> getMemberReservations(final LoginMemberInfo loginMemberInfo) {
        Member member = memberRepository.findById(loginMemberInfo.id())
                .orElseThrow(() -> new MemberException("멤버를 찾을 수 없습니다."));

        List<ReservationWithPayment> reservationsWithPayment = reservationRepository.findAllWithPaymentByMemberId(
                member.getId());
        List<Reservation> reservationsWithoutPayment = reservationRepository.findAllWithoutPaymentByMemberId(
                member.getId());
        List<WaitingWithRank> waitingsWithRank = waitingRepository.findByMemberId(member.getId());

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
