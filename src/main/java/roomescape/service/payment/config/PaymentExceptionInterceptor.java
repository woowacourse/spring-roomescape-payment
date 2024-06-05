package roomescape.service.payment.config;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.payment.PaymentConfirmException;
import roomescape.exception.payment.PaymentTimeoutException;

import java.io.IOException;

public class PaymentExceptionInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        try {
            return execution.execute(request, body);
        } catch (IOException e) {
            throw new PaymentTimeoutException(e);
        } catch (Exception e) {
            throw new PaymentConfirmException(e);
        }
    }
}
