package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.global.error.exception.ServerException;
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

    public Payment confirmPayment(String paymentKey, String orderId, Long amount) {
        try {
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
            return paymentRepository.save(payment);
        } catch (Exception e) {
            throw new ServerException("결제 승인 중 문제가 발생했습니다.");
        }
    }
}
