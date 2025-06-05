package roomescape.payment.toss.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.InternalServerException;

@RequiredArgsConstructor
public class PaymentErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;
    private final TossErrorMapper tossErrorMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse httpResponse) throws IOException {
        TossPaymentErrorResponse errorResponse = getTossErrorResponse(httpResponse);
        HttpStatus httpStatus = HttpStatus.resolve(httpResponse.getStatusCode().value());
        throw tossErrorMapper.createException(errorResponse, httpStatus);
    }

    private TossPaymentErrorResponse getTossErrorResponse(ClientHttpResponse httpResponse) {
        try {
            return objectMapper.readValue(httpResponse.getBody(), TossPaymentErrorResponse.class);
        } catch (IOException ex) {
            throw new InternalServerException("PARSING_ERROR", "예외 응답 파싱에 실패했습니다.");
        }
    }
}
