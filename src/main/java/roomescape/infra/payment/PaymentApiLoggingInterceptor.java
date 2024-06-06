package roomescape.infra.payment;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class PaymentApiLoggingInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        log.info(getLoggingData(request, body, response));
        return response;
    }

    private String getLoggingData(HttpRequest request, byte[] body, ClientHttpResponse response) throws IOException {
        return String.format("[Request] %s %s Header %s Body %s [Response] %s Header %s ",
                request.getMethod(),
                request.getURI(),
                request.getHeaders(),
                new String(body),
                response.getStatusCode(),
                response.getHeaders());
    }
}
