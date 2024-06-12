package roomescape.service.config;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class TossPaymentInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
        ClientHttpRequestExecution execution) throws IOException {

        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response, request.getURI());
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("""
            [REQUEST]
            Timestamp: {}
            URI: {}
            Method: {}
            Request Body: {}
            """, LocalDateTime.now(), request.getURI(), request.getMethod(), new String(body, UTF_8));
    }

    private void logResponse(ClientHttpResponse response, URI uri) throws IOException {
        log.info("""
            [RESPONSE]
            Timestamp: {}
            URI: {}
            Status Code: {}
            """, LocalDateTime.now(), uri, response.getStatusCode()
        );
    }
}
