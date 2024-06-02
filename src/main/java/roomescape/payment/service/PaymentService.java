package roomescape.payment.service;

import org.springframework.stereotype.Service;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.entity.MemberReservation;

import java.util.Map;

@Service
public class PaymentService {

    private final TossPaymentRestClient restClient;
    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository, TossPaymentRestClient restClient) {
        this.paymentRepository = paymentRepository;
        this.restClient = restClient;
    }

    public void confirmPayment(PaymentRequest request, MemberReservation memberReservation) {
        PaymentResponse response = restClient.post("/confirm", request);

        Payment payment = new Payment(
                response.paymentKey(),
                response.orderId(),
                response.totalAmount(),
                memberReservation);
        paymentRepository.save(payment);
    }

    public void cancelPayment(MemberReservation memberReservation) {
        paymentRepository.findByMemberReservation(memberReservation)
                .ifPresent(payment -> {
                    String uri = "/" + payment.getPaymentKey() + "/cancel";
                    Map<String, String> body = Map.of("cancelReason", "고객 변심");
                    restClient.post(uri, body);
                    paymentRepository.delete(payment);
                });
    }
}
