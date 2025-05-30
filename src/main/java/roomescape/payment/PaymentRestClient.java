package roomescape.payment;

import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.InternalServerException;
import roomescape.payment.dto.TossPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;

@Component
@Profile("!test")
public class PaymentRestClient implements PaymentClient {

    private final RestClient restClient;
    private final String TEST_KEY;
    private final String BASE_URL;

    public PaymentRestClient(
            @Value("${toss.secret-key}") String testKey,
            @Value("${toss.base-url}") String baseUrl
    ) {
        this.TEST_KEY = testKey;
        this.BASE_URL = baseUrl;
        this.restClient = initRestClient();
    }

    private RestClient initRestClient() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(1));
        requestFactory.setReadTimeout(Duration.ofSeconds(2));
        return RestClient.builder()
                .baseUrl(BASE_URL)
                .defaultHeader("Authorization", getEncodedKey())
                .defaultHeader("Content-Type", "application/json")
                .defaultStatusHandler(new PaymentErrorHandler())
                .requestFactory(requestFactory)
                .build();
    }

    @Override
    public TossPaymentResponse requestPaymentApprove(final TossPaymentRequest request) {
        try {
            return restClient.post()
                    .uri("/v1/payments/confirm")
                    .body(request)
                    .retrieve()
                    .body(TossPaymentResponse.class);
        } catch (ResourceAccessException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                throw new InternalServerException("TIME_OUT_EXCEPTION", "요청 시간이 초과되었습니다.");
            }
            throw new InternalServerException("CONNECTION_EXCEPTION", "연결에 실패하였습니다.");
        }
    }

    private String getEncodedKey() {
        StringBuilder sb = new StringBuilder();
        sb.append("Basic ");
        sb.append(Base64.getEncoder().encodeToString(TEST_KEY.getBytes()));
        return sb.toString();
    }
}
