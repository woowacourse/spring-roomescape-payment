package roomescape.payment.toss;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.exception.BadRequestException;
import roomescape.exception.ForbiddenException;
import roomescape.exception.InternalServerException;
import roomescape.exception.NotFoundException;
import roomescape.exception.UnauthorizedException;

@RequiredArgsConstructor
public class PaymentErrorHandler implements ResponseErrorHandler {

    private final ObjectMapper objectMapper;

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse httpResponse) throws IOException {
        TossPaymentErrorResponse response = objectMapper.readValue(httpResponse.getBody(), TossPaymentErrorResponse.class);
        HttpStatusCode statusCode = httpResponse.getStatusCode();
        TossErrorType tossErrorType = TossErrorType.findByCode(response.code);
        String message = response.message;
        if (tossErrorType != TossErrorType.NONE) {
            statusCode = tossErrorType.getStatus();
            message = tossErrorType.getMessage();
        }

        if (statusCode == HttpStatus.BAD_REQUEST) {
            throw new BadRequestException(response.code, message);
        }

        if (statusCode == HttpStatus.UNAUTHORIZED) {
            throw new UnauthorizedException(response.code, message);
        }

        if (statusCode == HttpStatus.NOT_FOUND) {
            throw new NotFoundException(response.code, message);
        }

        if (statusCode == HttpStatus.FORBIDDEN) {
            throw new ForbiddenException(response.code, message);
        }

        if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
            throw new InternalServerException(response.code, message);
        }
    }

    record TossPaymentErrorResponse(
            String code,
            String message
    ) {
    }
}
