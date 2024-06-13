package roomescape.config.payment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class PaymentClientLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentClientLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = new ClientHttpResponseWrapper(execution.execute(request, body));
        logResponse(response);
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        LOGGER.info("Request: {} {}", request.getMethod(), request.getURI());
        LOGGER.info("Request headers: {}", request.getHeaders());
        LOGGER.info("Request body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(ClientHttpResponse response) throws IOException {
        LOGGER.info("Response: {}", response.getStatusCode());
        LOGGER.info("Response headers: {}", response.getHeaders());
        LOGGER.info("Response body: {}", new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
    }
}
