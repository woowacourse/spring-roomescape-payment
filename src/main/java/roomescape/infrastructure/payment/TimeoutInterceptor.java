package roomescape.infrastructure.payment;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResourceAccessException;
import roomescape.exception.PaymentServerException;

import java.io.IOException;

public class TimeoutInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        try {
            return execution.execute(request, body);
        } catch (ResourceAccessException e) {
            throw new PaymentServerException(e.getMessage(), e);
        }
    }
}
