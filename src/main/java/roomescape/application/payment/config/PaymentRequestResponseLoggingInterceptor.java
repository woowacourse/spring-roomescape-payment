package roomescape.application.payment.config;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import roomescape.exception.payment.PaymentException;

public class PaymentRequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(PaymentRequestResponseLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        logRequest(request, body);
        try {
            ClientHttpResponse response = execution.execute(request, body);
            logResponse(request.getURI(), response);
            return response;
        } catch (Exception e) {
            logger.error("Failed to log response: ", e);
            throw new PaymentException();
        }
    }

    private void logRequest(HttpRequest request, byte[] body) {
        HttpHeaders headers = request.getHeaders();
        String requestBody = new String(body);
        logger.info("Payment request (Header Auth: {}, URI: {}, Method: {}, Body: {})",
                headers.get(HttpHeaders.AUTHORIZATION), request.getURI(), request.getMethod(), requestBody);
    }

    private void logResponse(URI uri, ClientHttpResponse response) {
        try {
            String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
            logger.info("Payment response (URI : {}, Status: {}) : {}",
                    uri, response.getStatusCode(), responseBody);
        } catch (IOException e) {
            logger.error("Failed to log response: ", e);
        }
    }
}