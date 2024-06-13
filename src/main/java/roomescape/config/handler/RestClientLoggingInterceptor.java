package roomescape.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.payment.domain.ServerErrorCode;
import roomescape.payment.dto.PaymentErrorResponse;

public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = new BufferingClientHttpResponseWrapper(execution.execute(request, body));

        if (response.getStatusCode().isError()) {
            logError(request, body, response);
            return response;
        }

        logger.info("Request {} {} {}", request.getMethod(), request.getURI(), new String(body));
        logger.info("Response {} {}", response.getStatusCode(), new String(response.getBody().readAllBytes()));
        return response;
    }

    private void logError(HttpRequest request, byte[] requestBody, ClientHttpResponse response) throws IOException {
        byte[] responseBody = response.getBody().readAllBytes();
        PaymentErrorResponse errorResponse = objectMapper.readValue(responseBody, PaymentErrorResponse.class);

        if (ServerErrorCode.isServerErrorCode(errorResponse.code()) || response.getStatusCode().is5xxServerError()) {
            logger.error("Request {} {} {}", request.getMethod(), request.getURI(), new String(requestBody));
            logger.error("Response {} {}", response.getStatusCode(), new String(responseBody));
            return;
        }

        logger.warn("Request {} {} {}", request.getMethod(), request.getURI(), new String(requestBody));
        logger.warn("Response {} {}", response.getStatusCode(), new String(responseBody));
    }

    private static class BufferingClientHttpResponseWrapper implements ClientHttpResponse {
        private final ClientHttpResponse response;
        private final byte[] body;

        public BufferingClientHttpResponseWrapper(ClientHttpResponse response) throws IOException {
            this.response = response;
            this.body = response.getBody().readAllBytes();
        }

        @Override
        public HttpStatusCode getStatusCode() throws IOException {
            return response.getStatusCode();
        }

        @Override
        public String getStatusText() throws IOException {
            return response.getStatusText();
        }

        @Override
        public void close() {
            response.close();
        }

        @Override
        public InputStream getBody() {
            return new ByteArrayInputStream(body);
        }

        @Override
        public HttpHeaders getHeaders() {
            return response.getHeaders();
        }
    }
}
