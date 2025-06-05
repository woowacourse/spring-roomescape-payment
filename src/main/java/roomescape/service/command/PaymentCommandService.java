package roomescape.service.command;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.client.dto.TossPaymentConfirmResponse;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.payment.ReservationPayment;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.exception.NotFoundException;
import roomescape.repository.JpaPaymentRepository;
import roomescape.repository.JpaReservationPaymentRepository;
import roomescape.repository.JpaReservationRepository;

@Service
@Transactional
public class PaymentCommandService {

    private final JpaPaymentRepository paymentRepository;
    private final JpaReservationPaymentRepository reservationPaymentRepository;
    private final JpaReservationRepository reservationRepository;

    public PaymentCommandService(JpaPaymentRepository paymentRepository,
                                 JpaReservationPaymentRepository reservationPaymentRepository,
                                 JpaReservationRepository reservationRepository) {
        this.paymentRepository = paymentRepository;
        this.reservationPaymentRepository = reservationPaymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public void createPayment(TossPaymentConfirmResponse tossPaymentConfirmResponse, ReservationResponseDto reservationResponse) {
        Reservation reservation = reservationRepository.findById(reservationResponse.id())
                .orElseThrow(NotFoundException::new);
        Payment payment = tossPaymentConfirmResponse.toPayment();

        paymentRepository.save(payment);
        reservationPaymentRepository.save(new ReservationPayment(reservation, payment)); //TODO payment save와 전파될 수 없을까? -> 안될듯 ㅋ
    }
}
