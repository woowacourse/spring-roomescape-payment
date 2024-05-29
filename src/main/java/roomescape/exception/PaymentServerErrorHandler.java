package roomescape.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

public class PaymentServerErrorHandler implements ResponseErrorHandler {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // TODO: 상태 코드에 따라 true / false 반환
        // https://docs.tosspayments.com/reference/error-codes#%EA%B2%B0%EC%A0%9C-%EC%8A%B9%EC%9D%B8
        return response.getStatusCode().is4xxClientError();
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        PaymentErrorResponse paymentErrorResponse = OBJECT_MAPPER.readValue(response.getBody(), PaymentErrorResponse.class);

        throw new PaymentException(paymentErrorResponse.code(), paymentErrorResponse.message());
    }
}
