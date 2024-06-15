package roomescape.service.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class TossPaymentInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TossPaymentInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {

        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response, request.getURI());
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        LOGGER.info("""
            [REQUEST]
            Timestamp: {}
            URI: {}
            Method: {}
            Request Body: {}
            """, LocalDateTime.now(), request.getURI(), request.getMethod(), new String(body, UTF_8));
    }

    private void logResponse(ClientHttpResponse response, URI uri) throws IOException {
        LOGGER.info("""
                [RESPONSE]
                Timestamp: {}
                URI: {}
                Status Code: {}
                Response Body: {}
                """, LocalDateTime.now(), uri, response.getStatusCode(),
            new String(response.getBody().readAllBytes(), UTF_8)
        );
    }
}
