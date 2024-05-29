package roomescape.application.payment;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import roomescape.application.payment.dto.Payment;
import roomescape.application.payment.dto.request.PaymentRequest;
import roomescape.application.payment.dto.response.PaymentResponse;
import roomescape.domain.payment.ReservationPayment;
import roomescape.domain.payment.ReservationPaymentRepository;
import roomescape.domain.reservation.Reservation;

@Component
public class PaymentService {

    private final String clientSecret;
    private final PaymentClient paymentClient;
    private final ReservationPaymentRepository reservationPaymentRepository;

    public PaymentService(@Value("${payment.secret}") String secret,
                          PaymentClient paymentClient,
                          ReservationPaymentRepository reservationPaymentRepository) {
        this.clientSecret = new String(Base64.getEncoder().encode((secret + ":").getBytes(StandardCharsets.UTF_8)));
        this.paymentClient = paymentClient;
        this.reservationPaymentRepository = reservationPaymentRepository;
    }

    @Transactional
    public PaymentResponse purchase(Reservation reservation, PaymentRequest request) {
        Payment payment = paymentClient.requestPurchase(clientSecret, request);
        ReservationPayment reservationPayment = new ReservationPayment(
                payment.orderId(), reservation, request.paymentKey(), payment.totalAmount()
        );
        reservationPaymentRepository.save(reservationPayment);
        return new PaymentResponse(payment.orderId(), payment.totalAmount());
    }
}
