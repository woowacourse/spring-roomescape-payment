package roomescape.reservation.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.global.exception.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Amount;
import roomescape.reservation.domain.OrderId;
import roomescape.reservation.domain.PaymentKey;
import roomescape.reservation.external.toss.TossPaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.external.toss.TossPaymentRequest;
import roomescape.reservation.external.toss.TossPaymentService;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.external.toss.TossPaymentServiceImpl;
import roomescape.schedule.domain.ReservationSchedule;

@Service
public class ReservationCommandService {
    private final ReservationRepository reservationRepository;
    private final TossPaymentService tossPaymentService;

    public ReservationCommandService(
            final ReservationRepository reservationRepository,
            final TossPaymentService tossPaymentService
    ) {
        this.reservationRepository = reservationRepository;
        this.tossPaymentService = tossPaymentService;
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
            final TossPaymentResponse tossPaymentResponse
    ) {
        if (reservationRepository.findByScheduleId(schedule.getId()).isPresent()) {
            throw new BadRequestException("이미 해당 일정에 예약이 존재합니다.");
        }
        TossPaymentResponse confirmTossPaymentResponse = tossPaymentService.paymentReservation(TossPaymentRequest.from(
                tossPaymentResponse));
        return reservationRepository.save(new Reservation(
                null,
                member,
                schedule,
                new OrderId(confirmTossPaymentResponse.orderId()),
                new Amount(confirmTossPaymentResponse.totalAmount()),
                new PaymentKey(confirmTossPaymentResponse.paymentKey())
        ));
    }

}
