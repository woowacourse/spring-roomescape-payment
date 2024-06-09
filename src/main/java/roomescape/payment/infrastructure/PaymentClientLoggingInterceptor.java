package roomescape.payment.infrastructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class PaymentClientLoggingInterceptor implements ClientHttpRequestInterceptor {
    private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI uri = request.getURI();
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response, uri);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) {
        String reqLog = "[REQUEST] " +
                "Uri : " + request.getURI() +
                ", Method : " + request.getMethod() +
                ", Request Body : " + new String(body, StandardCharsets.UTF_8);
        log.info(reqLog);
    }

    private void traceResponse(ClientHttpResponse response, URI uri) throws IOException {
        String resLog = "[RESPONSE] " +
                "Uri : " + uri +
                ", Status code : " + response.getStatusCode();
        log.info(resLog);
    }
}
