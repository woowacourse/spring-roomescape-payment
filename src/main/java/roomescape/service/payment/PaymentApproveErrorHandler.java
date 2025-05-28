package roomescape.service.payment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class PaymentApproveErrorHandler implements ResponseErrorHandler {

    private final List<String> sensitiveErrorCodes = List.of(
            "INVALID_API_KEY",
            "UNAPPROVED_ORDER_ID",
            "UNAUTHORIZED_KEY",
            "INCORRECT_BASIC_AUTH_FORMAT");

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        final String code = extractMessage(response, "code");
        if (isSensitiveError(code)) {
            throw new IllegalStateException("[ERROR] 결제 승인 중 예외가 발생하였습니다.");
        }
        final String errMessage = extractMessage(response, "message");
        throw new IllegalStateException("[ERROR] " + errMessage);
    }

    private boolean isSensitiveError(String code) {
        return sensitiveErrorCodes.contains(code);
    }

    private String extractMessage(ClientHttpResponse response, String key) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        InputStream inputStream = response.getBody();
        JsonNode jsonNode = objectMapper.readTree(inputStream);
        return jsonNode.get(key).asText();
    }
}
