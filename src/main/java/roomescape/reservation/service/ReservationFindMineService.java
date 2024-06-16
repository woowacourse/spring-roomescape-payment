package roomescape.reservation.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.waiting.domain.Waiting;
import roomescape.waiting.repository.WaitingRepository;

@Service
public class ReservationFindMineService {
    private static final Comparator<MyReservationResponse> RESERVATION_SORTING_COMPARATOR = Comparator
            .comparing(MyReservationResponse::date).thenComparing(MyReservationResponse::startAt);

    private final ReservationRepository reservationRepository;
    private final WaitingRepository waitingRepository;
    private final PaymentRepository paymentRepository;

    public ReservationFindMineService(ReservationRepository reservationRepository,
                                      WaitingRepository waitingRepository,
                                      PaymentRepository paymentRepository) {
        this.reservationRepository = reservationRepository;
        this.waitingRepository = waitingRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public List<MyReservationResponse> findMyReservations(Long memberId) {
        List<MyReservationResponse> reservations = findReservations(memberId);
        List<MyReservationResponse> waitings = findWaitings(memberId);

        return makeMyReservations(reservations, waitings);
    }

    private List<MyReservationResponse> findReservations(Long memberId) {
        return reservationRepository.findByMemberId(memberId)
                .stream()
                .map(this::createResponse)
                .toList();
    }

    private MyReservationResponse createResponse(Reservation reservation) {
        return paymentRepository.findByScheduleAndMemberAndStatus(
                        reservation.getSchedule(), reservation.getMember(), PaymentStatus.PAID)
                .map(payment -> MyReservationResponse.from(reservation, payment))
                .orElse(MyReservationResponse.from(reservation));
    }

    private List<MyReservationResponse> findWaitings(Long memberId) {
        return waitingRepository.findByMemberId(memberId)
                .stream()
                .map(waiting -> MyReservationResponse.from(waiting, countOrderOfWaiting(waiting)))
                .toList();
    }

    private Long countOrderOfWaiting(Waiting waiting) {
        return waitingRepository.countByScheduleAndCreatedAtLessThanEqual(
                waiting.getSchedule(), waiting.getCreatedAt());
    }

    private List<MyReservationResponse> makeMyReservations(List<MyReservationResponse> reservations,
                                                           List<MyReservationResponse> waitings) {
        List<MyReservationResponse> response = new ArrayList<>();
        response.addAll(reservations);
        response.addAll(waitings);
        response.sort(RESERVATION_SORTING_COMPARATOR);
        return response;
    }
}
