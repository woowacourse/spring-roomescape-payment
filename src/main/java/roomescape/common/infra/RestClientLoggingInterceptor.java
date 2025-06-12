package roomescape.common.infra;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class RestClientLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RestClientLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(
        final HttpRequest request,
        final byte[] body,
        final ClientHttpRequestExecution execution
    ) throws IOException {
        logRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponse(response);
        return response;
    }

    private void logRequest(final HttpRequest request, final byte[] body) {
        logger.info("Request URI: {} Body: {}", request.getURI(), new String(body, StandardCharsets.UTF_8));
    }

    private void logResponse(final ClientHttpResponse response) throws IOException {
        String responseBody = readBody(response.getBody());
        logger.info("Response Status: {} Body: {}", response.getStatusCode(), responseBody);
    }

    private String readBody(final InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        inputStream.transferTo(buffer);
        return buffer.toString(StandardCharsets.UTF_8);
    }
}
