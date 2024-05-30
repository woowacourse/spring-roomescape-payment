package roomescape.paymenthistory.domain;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.RestClient;
import roomescape.paymenthistory.dto.PaymentCreateRequest;
import roomescape.paymenthistory.exception.PaymentException;

public class PaymentRestClient {

    public static final String BASIC = "Basic ";
    public static final String INVALID_ORDER_ID_CODE = "INVALID_ORDER_ID";
    public static final String INVALID_API_KEY_CODE = "INVALID_API_KEY";
    public static final String UNAUTHORIZED_KEY_CODE = "UNAUTHORIZED_KEY";
    public static final String INCORRECT_BASIC_AUTH_FORMAT_CODE = "INCORRECT_BASIC_AUTH_FORMAT";

    private final RestClient restClient;
    private final String secretKey;

    public PaymentRestClient(RestClient restClient, String secretKey) {
        this.restClient = restClient;
        this.secretKey = new String(Base64.getEncoder().encode((secretKey + ":").getBytes(StandardCharsets.UTF_8)));
    }

    public void approvePayment(PaymentCreateRequest paymentCreateRequest) {
        String authorizations = BASIC + secretKey;

        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentCreateRequest.createRestClientPaymentApproveRequest())
                .retrieve()
                .onStatus(HttpStatusCode::isError, ((request, response) ->
                        handleErrorMessage(response)
                ))
                .toBodilessEntity();
    }

    private void handleErrorMessage(ClientHttpResponse httpResponse) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode rootNode = objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .readTree(httpResponse.getBody());
        String code = rootNode.path("code").asText();

        if (code.equals(INVALID_ORDER_ID_CODE) || code.equals(INVALID_API_KEY_CODE) ||
                code.equals(UNAUTHORIZED_KEY_CODE) || code.equals(INCORRECT_BASIC_AUTH_FORMAT_CODE)) {
            throw new PaymentException.PaymentServerError("내부 서버 에러가 발생했습니다. 관리자에게 문의해주세요.");
        }
        throw new PaymentException(rootNode.path("message").asText(), httpResponse.getStatusCode());
    }
}
