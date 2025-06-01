package roomescape.payment.infrastructure;

import static org.springframework.http.HttpStatus.GATEWAY_TIMEOUT;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.exception.payment.PaymentException;
import roomescape.payment.domain.PaymentClient;
import roomescape.payment.domain.PaymentInfo;

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
            throw new PaymentException(GATEWAY_TIMEOUT, "결제 API가 응답하지 않습니다.");
        } catch (Exception e) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 과정중 서버에 문제가 생겼습니다. 고객센터에게 문의하세요");
        }
    }

    private String getEncodedSecretKey() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((secretKey + ":").getBytes());
    }
}


