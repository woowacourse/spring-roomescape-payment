package roomescape.client.payment.service;

import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.client.payment.Payment;
import roomescape.client.payment.PaymentRepository;
import roomescape.client.payment.dto.TossPaymentConfirmRequest;
import roomescape.client.payment.dto.TossPaymentConfirmResponse;
import roomescape.exception.PaymentConfirmException;
import roomescape.exception.model.PaymentConfirmExceptionCode;
import roomescape.reservation.domain.Reservation;

@Tag(name = "결제 서비스", description = "결제 승인, 예약 id에 해당하는 결제를 반환하는 로직 수행")
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentClient paymentClient;

    public PaymentService(PaymentRepository paymentRepository, PaymentClient paymentClient) {
        this.paymentRepository = paymentRepository;
        this.paymentClient = paymentClient;
    }

    public Payment confirmPayment(TossPaymentConfirmRequest tossPaymentConfirmRequest, Reservation reservation) {
        TossPaymentConfirmResponse tossResponse = paymentClient.sendPaymentConfirmToToss(tossPaymentConfirmRequest);
        Payment payment = new Payment(tossResponse, reservation);

        return paymentRepository.save(payment);
    }

    public Payment findPaymentByReservationId(long reservationId) {
        return paymentRepository.findPaymentByReservationId(reservationId)
                .orElseThrow(() -> new PaymentConfirmException(PaymentConfirmExceptionCode.FOUND_PAYMENT_IS_NULL_EXCEPTION));
    }
}
