package roomescape.service.command;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import roomescape.client.PaymentClient;
import roomescape.client.dto.TossPaymentConfirmResponse;
import roomescape.domain.payment.OrderItem;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.payment.ReservationPayment;
import roomescape.dto.reservation.TossPaymentConfirmRequestDto;
import roomescape.dto.reservation.TossPaymentRequestDto;
import roomescape.exception.common.NotFoundException;
import roomescape.repository.JpaPaymentRepository;
import roomescape.repository.JpaReservationPaymentRepository;
import roomescape.repository.JpaReservationRepository;

@Service
@Transactional
public class PaymentCommandService {

    public static final long FIXED_ORDER_QUANTITY = 1L;

    private final PaymentClient paymentClient;
    private final JpaPaymentRepository paymentRepository;
    private final JpaReservationPaymentRepository reservationPaymentRepository;
    private final JpaReservationRepository reservationRepository;

    public PaymentCommandService(PaymentClient paymentClient,
                                 JpaPaymentRepository paymentRepository,
                                 JpaReservationPaymentRepository reservationPaymentRepository,
                                 JpaReservationRepository reservationRepository
    ) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
        this.reservationPaymentRepository = reservationPaymentRepository;
        this.reservationRepository = reservationRepository;
    }

    public void createPayment(Long paymentTargetId, TossPaymentRequestDto paymentRequest) {
        Long paymentAmount = OrderItem.calculatePaymentAmount(paymentRequest.orderId(), FIXED_ORDER_QUANTITY);
        TossPaymentConfirmResponse tossPaymentConfirmResponse = paymentClient.confirmPayment(
                new TossPaymentConfirmRequestDto(
                        paymentRequest.orderId(),
                        paymentRequest.paymentKey(),
                        paymentAmount
                ));
        Reservation reservation = reservationRepository.findById(paymentTargetId)
                .orElseThrow(() -> new NotFoundException("예약", paymentTargetId));
        Payment payment = tossPaymentConfirmResponse.toPayment();

        paymentRepository.save(payment);
        reservationPaymentRepository.save(new ReservationPayment(reservation, payment));
    }
}
