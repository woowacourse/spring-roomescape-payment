package roomescape.application.payment.config;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
            throw new PaymentException("결제 서버 요청에 실패했습니다.");
        }
    }

    private void logRequest(HttpRequest request, byte[] body){
        String requestBody = new String(body);
        logger.info("Payment request (URI : {}, Method: {}, Body: {})",
                request.getURI(), request.getMethod(), requestBody);
    }

    private void logResponse(URI uri, ClientHttpResponse response) throws IOException {
        String responseBody = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);
        logger.info("Payment response (URI : {}, Status: {}) : {}",
                uri, response.getStatusCode(), responseBody);
    }
}
