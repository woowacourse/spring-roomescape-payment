package roomescape.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
public class PaymentService {

    // TODO: 배포시 환경 변수로 변경하기
    private final static String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final String confirmSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
    private final String authorizationHeader;

    private final PaymentRepository paymentRepository;
    private final RestClient restClient;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        restClient = RestClient.builder()
                .baseUrl(CONFIRM_URL)
                .build();

        String base64EncodedKey = Base64.getEncoder()
                .encodeToString((confirmSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        authorizationHeader = "Basic " + base64EncodedKey;
    }

    public Long confirmPayment(String paymentKey, String orderId, Long amount) {
        // 다음 코드로 결제 승인을 요청한다.
        PaymentConfirmResponse response = restClient.post()
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new PaymentConfirmRequest(paymentKey, orderId, amount))
                .retrieve()
                .body(PaymentConfirmResponse.class);

        // 위 작업이 문제가 없다면 DB에 값을 저장한다.
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
