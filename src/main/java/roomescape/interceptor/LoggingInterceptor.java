package roomescape.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import roomescape.interceptor.wrapper.BufferingClientHttpResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class LoggingInterceptor implements ClientHttpRequestInterceptor {
    private final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(final HttpRequest request, final byte[] body, final ClientHttpRequestExecution execution) throws IOException {
        logger.info("[REQUEST BODY] msg : {}", new String(body, StandardCharsets.UTF_8));
        final ClientHttpResponse response = new BufferingClientHttpResponseWrapper(execution.execute(request, body));
        logger.info("[RESPONSE BODY] msg : {}", new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
        return response;
    }
}
