package roomescape.payment.infrastructure;

import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.payment.PaymentException;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentInfo;

@Slf4j
public class TossPaymentClient implements PaymentClient {

    @Value("${toss.confirm-url}")
    private String confrimUrl;
    @Value("${toss.secret-key}")
    private String secretKey;

    private final RestClient restClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public TossPaymentClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public void approvePayment(final PaymentInfo paymentInfo) {

        final ApproveTossPaymentRequest request = ApproveTossPaymentRequest.from(paymentInfo);
        final String encodedSecretKey = getEncodedSecretKey();

        try {
            restClient.post()
                    .uri(confrimUrl)
                    .header("Authorization", encodedSecretKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .onStatus((status) -> status.value() != HttpStatus.OK.value(), (req, res) -> {
                        InputStream body = res.getBody();
                        TossPaymentErrorResponse errorResponse
                                = objectMapper.readValue(body, TossPaymentErrorResponse.class);
                        throw new PaymentException(
                                res.getStatusCode(),
                                errorResponse.code(),
                                errorResponse.message()
                        );
                    })
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            log.warn("결제 API 연결 실패: {}", e.getMessage(), e);

            Throwable rootCause = e.getCause();
            if (rootCause instanceof SocketTimeoutException) {
                throw new PaymentException(GATEWAY_TIMEOUT, "결제 처리 중 지연이 발생했습니다. 잠시 후 다시 시도해주세요.");
            }

            throw new PaymentException(HttpStatus.SERVICE_UNAVAILABLE, "결제 서비스에 일시적인 문제가 발생했습니다.");
        }
        catch (Exception e) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 과정중 서버에 문제가 생겼습니다. 고객센터에게 문의하세요");
        }
    }

    private String getEncodedSecretKey() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
    }
}
