package roomescape.application.config;

import java.io.IOException;
import java.net.SocketTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import roomescape.exception.payment.PaymentException;

public class PaymentTimeoutHandlerInterceptor implements ClientHttpRequestInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(PaymentTimeoutHandlerInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) {
        String requestBody = new String(body);
        logger.info("Payment request: {}", requestBody);
        try {
            return execution.execute(request, body);
        } catch (SocketTimeoutException e) {
            logger.error("Payment request timeout", e);
            throw new PaymentException("결제 서버 요청 시간이 초과되었습니다.");
        } catch (IOException e) {
            logger.error("Payment request failed", e);
            throw new PaymentException("결제 서버 요청에 실패했습니다.");
        }
    }
}
