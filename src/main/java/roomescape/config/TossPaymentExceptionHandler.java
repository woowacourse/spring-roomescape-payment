package roomescape.config;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.TossPaymentException;

public class TossPaymentExceptionHandler implements ResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        HttpStatusCode status = response.getStatusCode();
        String body = StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8);

        if (status.is4xxClientError()) {
            throw new TossPaymentException("결제 요청이 잘못되었습니다 (클라이언트 오류)." + body);
        } else if (status.is5xxServerError()) {
            throw new TossPaymentException("결제 서버 오류가 발생했습니다 (서버 오류)." + body);
        }
        throw new TossPaymentException("예상치 못한 결제 오류가 발생했습니다." + body);
    }
}
