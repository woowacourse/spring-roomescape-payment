package roomescape.exception;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class TossPayErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public TossPayErrorHandler() {
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return !response.getStatusCode().is2xxSuccessful();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        InputStream responseBody = response.getBody();
        PaymentErrorResponse paymentErrorResponse = objectMapper.readValue(responseBody, PaymentErrorResponse.class);
        if (paymentErrorResponse.isInvalid()) {
            String data = new String(responseBody.readAllBytes());
            throw new ParsingFailException("서버로부터 올바르지 않은 에러 데이터가 전달되었습니다.", data);
        }
        throw new PaymentFailException(paymentErrorResponse.code(), paymentErrorResponse.message());
    }
}
