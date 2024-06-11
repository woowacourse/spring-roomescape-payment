package roomescape.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.infrastructure.BufferingClientHttpResponseWrapper;

public class PaymentClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        logger.info("Payment API request {} {} ", request.getMethod(), request.getURI());

        BufferingClientHttpResponseWrapper response = new BufferingClientHttpResponseWrapper(
                execution.execute(request, body));
        byte[] bytes = response.getBody().readAllBytes();

        logger.info("Payment API response {} {}", response.getStatusCode(), new String(bytes, StandardCharsets.UTF_8));
        return response;
    }
}
