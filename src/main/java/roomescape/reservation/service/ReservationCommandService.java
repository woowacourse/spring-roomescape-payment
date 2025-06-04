package roomescape.reservation.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentKey;
import roomescape.reservation.external.toss.TossPaymentRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.external.toss.TossPaymentResponse;
import roomescape.reservation.external.toss.TossApiClient;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.schedule.domain.ReservationSchedule;

@Service
public class ReservationCommandService {
    private final ReservationRepository reservationRepository;
    private final TossApiClient tossApiClient;

    public ReservationCommandService(
            final ReservationRepository reservationRepository,
            final TossApiClient tossApiClient
    ) {
        this.reservationRepository = reservationRepository;
        this.tossApiClient = tossApiClient;
    }

    public void deleteReservationById(final Long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public Reservation createReservation(final ReservationSchedule schedule, final Member member) {
        if (reservationRepository.findByScheduleId(schedule.getId()).isPresent()) {
            throw new BadRequestException("이미 해당 일정에 예약이 존재합니다.");
        }
        return reservationRepository.save(new Reservation(null, member, schedule));
    }

    @Transactional
    public Reservation createReservationWithPayment(
            final ReservationSchedule schedule,
            final Member member,
            final TossPaymentRequest tossPaymentRequest
    ) {
        if (reservationRepository.findByScheduleId(schedule.getId()).isPresent()) {
            throw new BadRequestException("이미 해당 일정에 예약이 존재합니다.");
        }
        TossPaymentResponse tossPaymentResponse = tossApiClient.requestPayment(tossPaymentRequest);
        return reservationRepository.save(new Reservation(
                member,
                schedule,
                new OrderId(tossPaymentResponse.orderId()),
                new Amount(tossPaymentResponse.totalAmount()),
                new PaymentKey(tossPaymentResponse.paymentKey())
        ));
    }
}
