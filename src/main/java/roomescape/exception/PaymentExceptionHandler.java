package roomescape.exception;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class PaymentExceptionHandler implements ResponseErrorHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    /**
     * 토스 예외 문서
     * https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
     */
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        Map<String, Object> properties = mapper.readValue(body, new TypeReference<>() {});
        throw new PaymentException(response.getStatusCode(), properties.get("message").toString());
    }
}
