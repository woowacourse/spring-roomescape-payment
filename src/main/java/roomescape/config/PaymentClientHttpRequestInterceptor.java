package roomescape.config;

import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class PaymentClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        logger.info("Payment API request {} {} ", request.getMethod(), request.getURI());

        ClientHttpResponse response = execution.execute(request, body);

        logger.info("Payment API response {} {}", response.getStatusCode(), response.getHeaders());
        return response;
    }
}
