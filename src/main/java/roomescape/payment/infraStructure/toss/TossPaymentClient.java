package roomescape.payment.infraStructure.toss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.common.exception.PaymentClientException;
import roomescape.common.exception.ServerConnectException;
import roomescape.payment.infraStructure.PaymentGatewayClient;
import roomescape.payment.infraStructure.dto.request.ConfirmPaymentRequest;
import roomescape.payment.infraStructure.dto.response.ConfirmPaymentResponse;
import roomescape.payment.infraStructure.dto.response.PaymentFailure;

import java.util.Base64;
import java.util.List;

public class TossPaymentClient implements PaymentGatewayClient {

    private static final Logger log = LoggerFactory.getLogger(TossPaymentClient.class);

    @Value("${payment.toss.secret-key}")
    private String secretKey;

    private static final List<String> IGNORE_CODES = List.of(
            "INCORRECT_BASIC_AUTH_FORMAT",
            "INVALID_API_KEY"
    );

    private final RestClient restClient;

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public ConfirmPaymentResponse postConfirmPayment(ConfirmPaymentRequest paymentRequest) {
        ConfirmPaymentResponse paymentResponse;
        try {
            paymentResponse = restClient.post()
                    .uri("/confirm")
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((secretKey + ":").getBytes()))
                    .body(paymentRequest)
                    .retrieve()
                    .body(ConfirmPaymentResponse.class);

            log.info("[결제 승인 성공] paymentKey={}, orderId={}",
                    paymentRequest.paymentKey(), paymentRequest.orderId());

        } catch (HttpClientErrorException hce) {
            log.warn("[잘못된 요청] statusCode={}, message={}, paymentKey={}",
                    hce.getStatusCode(), hce.getMessage(), paymentRequest.paymentKey());
            throw new ServerConnectException("서버에 잘못된 요청을 보냈습니다.");
        } catch (HttpServerErrorException hse) {
            log.error("[서버 오류] statusCode={}, message={}, paymentKey={}",
                    hse.getStatusCode(), hse.getMessage(), paymentRequest.paymentKey());
            throw new ServerConnectException("서버와 통신 중 에러가 발생했습니다.");
        } catch (ResourceAccessException rae) {
            log.error("[응답 시간 초과] message={}, paymentKey={}",
                    rae.getMessage(), paymentRequest.paymentKey());
            throw new ServerConnectException("서버측 응답 시간이 초과되었습니다.");
        }
        handlePaymentResponse(paymentResponse,paymentRequest);
        return paymentResponse;
    }

    private void handlePaymentResponse(ConfirmPaymentResponse response, ConfirmPaymentRequest request) {
        PaymentFailure failure = response.failure();
        if (failure == null) {
            return;
        }

        if (IGNORE_CODES.contains(failure.code())) {
            log.error("[승인 API 요청 값 오류] code={}, message={}, paymentKey={}, orderId={}",
                    failure.code(), failure.message(), request.paymentKey(), request.orderId());
            throw new RuntimeException("토스 결제 승인 API 요청 값이 올바르지 않습니다.");
        }

        log.error("[결제 승인 실패] code={}, message={}, paymentKey={}, orderId={}",
                failure.code(), failure.message(), request.paymentKey(), request.orderId());
        throw new PaymentClientException(failure.message());
    }
}
