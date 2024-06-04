package roomescape.config;

import java.io.IOException;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.payment.PaymentClientTimeoutException;

public class PaymentClientTimeoutInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            return execution.execute(request, body);
        } catch (IOException exception) {
            throw new PaymentClientTimeoutException();
        }
    }
}
