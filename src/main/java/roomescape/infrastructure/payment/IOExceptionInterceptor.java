package roomescape.infrastructure.payment;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.PaymentServerException;

import java.io.IOException;

public class IOExceptionInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            return execution.execute(request, body);
        } catch (IOException e) {
            throw new PaymentServerException(e.getMessage(), e);
        }
    }
}
