package roomescape.reservation.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.InvalidReservationException;
import roomescape.common.util.DateTime;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationRepository;
import roomescape.reservation.dto.request.ReservationConditionRequest;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.MyReservationWithPaymentResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationWithPaymentResponse;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.reservationTime.domain.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.domain.ThemeRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.domain.WaitingRepository;

@Service
public class ReservationService {

    private final DateTime dateTime;
    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;

    public ReservationService(DateTime dateTime, ReservationRepository reservationRepository, ReservationTimeRepository reservationTimeRepository, ThemeRepository themeRepository, MemberRepository memberRepository, WaitingRepository waitingRepository, PaymentRepository paymentRepository) {
        this.dateTime = dateTime;
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public ReservationWithPaymentResponse createReservationWithPendingPayment(final ReservationWithPaymentRequest request, final Long memberId) {
        Payment payment = paymentRepository.save(request.toPendingPayment());

        Reservation reservation = getReservation(request, memberId, payment);
        Reservation savedReservation = reservationRepository.save(reservation);

        return ReservationWithPaymentResponse.from(savedReservation, payment);
    }

    private Reservation getReservation(ReservationWithPaymentRequest request, Long memberId, Payment payment) {
        ReservationTime time = reservationTimeRepository.findById(request.timeId())
                .orElseThrow(() -> new InvalidReservationException("존재하지 않는 시간입니다."));
        Theme theme = themeRepository.findById(request.themeId())
                .orElseThrow(() -> new InvalidReservationException("존재하지 않는 테마입니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new InvalidReservationException("존재 하지 않는 유저입니다."));

        Reservation reservation = Reservation.createWithoutId(dateTime.now(), member, request.date(), time, theme, payment);

        if (reservationRepository.existsByDateAndTimeStartAtAndThemeId(
                reservation.getDate(),
                reservation.getReservationTime(),
                reservation.getThemeId()
        )) {
            throw new InvalidReservationException("이미 예약이 존재합니다.");
        }
        return reservation;
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservations(ReservationConditionRequest request) {
        if (request.isEmpty()) {
            return reservationRepository.findAll().stream()
                    .map(ReservationResponse::from)
                    .toList();
        }
        return reservationRepository.findByMemberIdAndThemeIdAndDate(
                        request.memberId(),
                        request.themeId(),
                        request.dateFrom(),
                        request.dateTo())
                .stream()
                .map(ReservationResponse::from)
                .toList();
    }

    @Transactional
    public void deleteReservationById(final Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new InvalidReservationException("존재하지 않는 예약입니다."));
        reservationRepository.deleteById(id);

        List<Waiting> waitings = waitingRepository.findByDateAndThemeIdAndTimeIdOrderByCreatedAtAsc(
                reservation.getDate(),
                reservation.getThemeId(),
                reservation.getTimeId()
        );

        if (!waitings.isEmpty()) {
            approveWaiting(waitings);
        }

    }

    private void approveWaiting(List<Waiting> waitings) {
        Waiting firstWaiting = waitings.get(0);

        Reservation newReservation = Reservation.createWithoutId(
                dateTime.now(),
                firstWaiting.getMember(),
                firstWaiting.getDate(),
                firstWaiting.getTime(),
                firstWaiting.getTheme(),
                null // TODO:대기 예약은 결제 시스템이 없기 때문에 null로 둠
        );
        reservationRepository.save(newReservation);

        waitingRepository.delete(firstWaiting);
    }

    @Transactional(readOnly = true)
    public List<MyReservationWithPaymentResponse> getMyReservations(final Long id) {
        List<Reservation> confirmedReservations = reservationRepository.findByMemberId(id);
        List<MyReservationWithPaymentResponse> confirmedResponses = getReservationsWithPayment(confirmedReservations);

        List<Waiting> waitingReservations = waitingRepository.findByMemberId(id);
        List<MyReservationWithPaymentResponse> waitingResponses = waitingReservations.stream()
                .map(waiting -> {
                    long rank = calculateWaitingRank(waiting);
                    return MyReservationWithPaymentResponse.fromWaiting(waiting, rank);
                })
                .toList();

        return Stream.concat(confirmedResponses.stream(), waitingResponses.stream())
                .sorted(Comparator.comparing(MyReservationWithPaymentResponse::date))
                .toList();
    }

    private List<MyReservationWithPaymentResponse> getReservationsWithPayment(List<Reservation> confirmedReservations) {
        List<MyReservationWithPaymentResponse> responses = new ArrayList<>();
        for (Reservation reservation : confirmedReservations) {
            responses.add(MyReservationWithPaymentResponse.from(reservation));
        }
        return responses;
    }

    private long calculateWaitingRank(Waiting waiting) {
        return waitingRepository.countByDateAndThemeIdAndTimeIdAndCreatedAtBefore(
                waiting.getDate(),
                waiting.getTheme().getId(),
                waiting.getTime().getId(),
                waiting.getCreatedAt()
        ) + 1;
    }
}
