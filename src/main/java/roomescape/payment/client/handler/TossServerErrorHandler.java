package roomescape.payment.client.handler;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.global.exception.RoomescapeException;

@Slf4j
@Component
public class TossServerErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        byte[] responseBody = response.getBody().readAllBytes();
        String content = new String(responseBody, StandardCharsets.UTF_8);
        log.error("Payment API 처리 중 상태 코드 5xx 에러 발생 - URL: {}, Method: {}, Status: {}, Content: {}",
                url, method, response.getStatusCode(), content);

        throw new RoomescapeException();
    }
}
