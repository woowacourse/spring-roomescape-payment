package roomescape.reservation.service;


import java.util.NoSuchElementException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.PaymentInfo;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.external.toss.PaymentConfirmRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.schedule.domain.ReservationSchedule;

@Service
public class ReservationCommandService {
    private final ReservationRepository reservationRepository;
    private final PaymentService paymentService;

    public ReservationCommandService(
            final ReservationRepository reservationRepository,
            final PaymentService paymentService
    ) {
        this.reservationRepository = reservationRepository;
        this.paymentService = paymentService;
    }

    public void deleteReservationById(final Long id) {
        reservationRepository.deleteById(id);
    }

    @Transactional
    public Reservation createReservation(final ReservationSchedule schedule, final Member member) {
        if (reservationRepository.findByScheduleId(schedule.getId()).isPresent()) {
            throw new NoSuchElementException("이미 해당 일정에 예약이 존재합니다.");
        }
        return reservationRepository.save(new Reservation(null, member, schedule));
    }

    @Transactional
    public Reservation createReservationWithPayment(
            final ReservationSchedule schedule,
            final Member member,
            final PaymentInfo paymentInfo
    ) {
        if (reservationRepository.findByScheduleId(schedule.getId()).isPresent()) {
            throw new NoSuchElementException("이미 해당 일정에 예약이 존재합니다.");
        }
        PaymentInfo confirmPaymentInfo = paymentService.paymentReservation(PaymentConfirmRequest.from(paymentInfo));
        return reservationRepository.save(new Reservation(
                null,
                member,
                schedule,
                confirmPaymentInfo.orderId(),
                confirmPaymentInfo.totalAmount(),
                confirmPaymentInfo.paymentKey()
        ));
    }

}
