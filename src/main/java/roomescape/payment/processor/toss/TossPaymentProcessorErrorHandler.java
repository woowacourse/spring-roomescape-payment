package roomescape.payment.processor.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.TossPaymentException;

public class TossPaymentProcessorErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    public TossPaymentProcessorErrorHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) {
        try {
            final HttpStatusCode statusCode = response.getStatusCode();
            return statusCode.is4xxClientError() || statusCode.is5xxServerError();
        } catch (final IOException e) {
            // 로깅 적용 전 임시 sout
            System.out.println("토스 결제 처리 중 오류가 발생했습니다. " + e.getMessage());
            return true;
        }
    }

    @Override
    public void handleError(final URI url, final HttpMethod method, final ClientHttpResponse response) {
        try {
            final TossPaymentConfirmError error =
                objectMapper.readValue(response.getBody(), TossPaymentConfirmError.class);
            throw new TossPaymentException(response.getStatusCode(), error.message());
        } catch (final IOException e) {

            // 로깅 적용 전 임시 sout
            System.out.println("토스 결제 처리 중 오류가 발생했습니다. " + e.getMessage());

            throw new TossPaymentException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "토스 결제 처리 중 오류가 발생했습니다. "
            );
        }
    }
}
