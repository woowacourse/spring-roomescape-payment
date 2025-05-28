package roomescape.payment;

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

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        TossPaymentErrorResponse tossPaymentErrorResponse = objectMapper.readValue(response.getBody(),
                TossPaymentErrorResponse.class);
        HttpStatusCode statusCode = response.getStatusCode();
        TossErrorType tossErrorType = TossErrorType.findByCode(tossPaymentErrorResponse.code);
        String message = tossPaymentErrorResponse.message;
        if (tossErrorType != TossErrorType.NONE) {
            statusCode = tossErrorType.getStatus();
            message = tossErrorType.getMessage();
        }

        if (statusCode == HttpStatus.BAD_REQUEST) {
            throw new BadRequestException(tossPaymentErrorResponse.code, message);
        }

        if (statusCode == HttpStatus.UNAUTHORIZED) {
            throw new UnauthorizedException(tossPaymentErrorResponse.code, message);
        }

        if (statusCode == HttpStatus.NOT_FOUND) {
            throw new NotFoundException(tossPaymentErrorResponse.code, message);
        }

        if (statusCode == HttpStatus.FORBIDDEN) {
            throw new ForbiddenException(tossPaymentErrorResponse.code, message);
        }

        if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR) {
            throw new InternalServerException(tossPaymentErrorResponse.code, message);
        }
    }

    record TossPaymentErrorResponse(
            String code,
            String message
    ) {
    }
}
