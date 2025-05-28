package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.request.PaymentConfirmRequest;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.error.ClientErrorHandler;
import roomescape.payment.error.ServerErrorHandler;
import roomescape.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RestClient restClient;

    public Long confirmPayment(String paymentKey, String orderId, Long amount) {
        PaymentConfirmResponse response = restClient.post()
                .body(new PaymentConfirmRequest(paymentKey, orderId, amount))
                .retrieve()
                .onStatus(new ClientErrorHandler())
                .onStatus(new ServerErrorHandler())
                .body(PaymentConfirmResponse.class);

        Payment payment = new Payment(
                response.paymentKey(),
                response.orderId(),
                response.totalAmount(),
                response.type()
        );
        paymentRepository.save(payment);
        return payment.getId();
    }
}
