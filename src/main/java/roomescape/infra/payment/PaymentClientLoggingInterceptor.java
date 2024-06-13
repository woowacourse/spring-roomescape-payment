package roomescape.infra.payment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class PaymentClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("[Payment Request] URI: {}, Method: {}, Headers: {}, Body: {}",
                request.getURI(),
                request.getMethod(),
                request.getHeaders(),
                new String(body, StandardCharsets.UTF_8)
        );
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        try {
            log.info("[Payment Response] Status code: {}, Status text: {}, Headers: {}, Body: {}",
                    response.getStatusCode(),
                    response.getStatusText(),
                    response.getHeaders(),
                    new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8)
            );
        } catch (IOException e) {
            log.error("Failed to read response body", e);
        }
    }
}
