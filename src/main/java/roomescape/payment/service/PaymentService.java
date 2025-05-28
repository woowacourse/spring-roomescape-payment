package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    // TODO: 배포시 환경 변수로 변경하기
    private final static String paymentWidgetClientKey = "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm";
    private final static String CONFIRM_URL = "https://api.tosspayments.com/v1/payments/confirm";
    private final static String authorizationHeader = "Basic " + "dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg==";

    private final PaymentRepository paymentRepository;
    private final RestClient restClient;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        restClient = RestClient.builder()
                .baseUrl(CONFIRM_URL).build();
    }

    /* 다음과 같은 GET 요청을 보내야 결제 승인이 난다.
    curl --request POST \
        --url https://api.tosspayments.com/v1/payments/confirm \
        --header 'Authorization: Basic dGVzdF9za196WExrS0V5cE5BcldtbzUwblgzbG1lYXhZRzVSOg=='

        Request Body
        amount 필수 · number
        orderId 필수 · string
        paymentKey 필수 · string
     */

    public void confirmPayment(String paymentKey, String orderId, Long amount, String paymentType) {
        // 다음 코드로 결제 승인을 요청한다.
        restClient.post()
                .header("Authorization", authorizationHeader)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                            "paymentKey": "%s",
                            "orderId": "%s",
                            "amount": %d
                        }
                        """.formatted(paymentKey, orderId, amount))
                .retrieve();

        // 위 작업이 문제가 없다면 DB에 값을 저장한다.
        Payment payment = new Payment(paymentKey, orderId, amount, paymentType);
        paymentRepository.save(payment);
    }
}
