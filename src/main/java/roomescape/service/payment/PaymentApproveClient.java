package roomescape.service.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import roomescape.dto.response.TossPaymentResponse;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Service
public class PaymentApproveClient {

    private static final int MAX_RETRY = 3;

    private final RestClient restClient;
    private final String widgetSecretKey;
    private final String paymentApproveUrl;
    private final String paymentCheckUrl;
    private final String paymentCancelUrl;

    public PaymentApproveClient(
            @Value("${toss.payments.base-url}") String baseUrl,
            @Value("${toss.payments.widget-secret-key}") String widgetSecretKey,
            @Value("${toss.payments.payment-approve-url}") String paymentApproveUrl,
            @Value("${toss.payments.payment-check-url}") String paymentCheckUrl,
            @Value("${toss.payments.payment-cancel-url}") String paymentCancelUrl,
            MyClientHttpRequestFactory requestFactory
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .defaultStatusHandler(new PaymentApproveErrorHandler())
                .build();
        this.widgetSecretKey = widgetSecretKey;
        this.paymentApproveUrl = paymentApproveUrl;
        this.paymentCheckUrl = paymentCheckUrl;
        this.paymentCancelUrl = paymentCancelUrl;
    }

    public TossPaymentResponse approvePayment(String paymentKey, String orderId, int amount) {
        final Map<String, Object> requestBody = Map.of(
                "amount", amount,
                "orderId", orderId,
                "paymentKey", paymentKey
        );

        try {
            return approvePayment(requestBody);
        } catch (RestClientException e) {
            return getPaymentOrElseCancel(paymentKey);
        } catch (Exception e) {
            throw new IllegalStateException("[ERROR] 결제 중 예기치 못한 문제가 발생하였습니다. 관리자에게 문의하세요.");
        }
    }

    private TossPaymentResponse approvePayment(Map<String, Object> requestBody) {
        return restClient.post().uri(paymentApproveUrl)
                .header("Authorization", getAuthorizations())
                .accept(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(TossPaymentResponse.class);
    }

    private TossPaymentResponse getPaymentOrElseCancel(String paymentKey) {
        // 서버 에러 및 연결 에러 -> 재시도
        int attempts = 0;
        while (attempts < MAX_RETRY) {
            try {
                attempts++;
                return checkPayment(paymentKey);
            } catch (HttpServerErrorException | ResourceAccessException ignored) {
            }
        }

        // 재시도 해도 실패 -> 안전하게 결제 취소
        boolean cancelSuccess = false;
        attempts = 0;
        while (attempts < MAX_RETRY) {
            try {
                attempts++;
                cancelPayment(paymentKey);
                cancelSuccess = true;
            } catch (HttpServerErrorException | ResourceAccessException ignored) {
            }
        }
        if (cancelSuccess) {
            throw new IllegalStateException("[ERROR] 결제 중 문제가 발생하여 결제를 취소했습니다.");
        } else {
            throw new IllegalStateException("[ERROR] 결제 중 문제가 발생하여 결제를 취소하려했으나 실패했습니다. 관리자에게 문의하세요");
        }
    }

    private TossPaymentResponse checkPayment(String paymentKey) {
        return restClient.get().uri(paymentCheckUrl, paymentKey)
                .header("Authorization", getAuthorizations())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(TossPaymentResponse.class);
    }

    private void cancelPayment(String paymentKey) {
        restClient.post().uri(paymentCancelUrl, paymentKey)
                .header("Authorization", getAuthorizations())
                .accept(MediaType.APPLICATION_JSON)
                .body(Map.of(
                        "cancelReason", "클라이언트 로직 문제"
                ))
                .retrieve()
                .body(TossPaymentResponse.class);
    }

    private String getAuthorizations() {
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        return "Basic " + new String(encodedBytes);
    }
}
