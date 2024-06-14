package roomescape.reservation.service;


import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.reservation.domain.Payment;
import roomescape.reservation.domain.repository.PaymentRepository;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.system.payment.PaymentClient;

@Service
@Transactional
public class PaymentService {
    @Value("${payment.widget.confirm.secret-key}")
    private String widgetSecretKey;

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentClient paymentClient, PaymentRepository paymentRepository) {
        this.paymentClient = paymentClient;
        this.paymentRepository = paymentRepository;
    }

    public void confirm(ReservationRequest reservationRequest) {
        String authorizations = getAuthorizations();

        paymentClient.confirm(authorizations, reservationRequest);
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }

    public Payment save(final ReservationRequest request) {
        Payment payment = new Payment(request.paymentKey(), request.amount());
        return paymentRepository.save(payment);
    }
}
