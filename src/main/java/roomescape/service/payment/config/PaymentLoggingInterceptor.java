package roomescape.service.payment.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class PaymentLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(PaymentLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String host = request.getURI().getHost();
        String path = request.getURI().getPath();
        String httpMethod = request.getMethod().toString();

        logger.info("[Payment Request] {}\t: {} {} \n \t{}", path, httpMethod, host, new String(body));
        return execution.execute(request, body);
    }
}
