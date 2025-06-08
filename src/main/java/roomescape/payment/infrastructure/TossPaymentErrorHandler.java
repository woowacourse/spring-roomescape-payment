package roomescape.payment.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.BadRequestException;
import roomescape.common.exception.InternalServerErrorException;
import roomescape.payment.infrastructure.dto.PaymentFailure;
import roomescape.payment.infrastructure.dto.response.ConfirmPaymentResponse;
import roomescape.payment.infrastructure.vo.TossPaymentInternalErrorCode;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class TossPaymentErrorHandler implements ResponseErrorHandler {
    private static final List<String> INTERNAL_ERROR_CODES = Arrays.stream(TossPaymentInternalErrorCode.values())
            .map(TossPaymentInternalErrorCode::getCode)
            .toList();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError()
                || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        if (response.getStatusCode().isError()) {
            ObjectMapper objectMapper = new ObjectMapper();
            ConfirmPaymentResponse paymentResponse = objectMapper.readValue(response.getBody(), ConfirmPaymentResponse.class);
            handleErrorResponse(paymentResponse);
        }
    }

    private void handleErrorResponse(ConfirmPaymentResponse response) {
        PaymentFailure failure = response.failure();
        logBasedOnFailure(failure);

        if (INTERNAL_ERROR_CODES.contains(failure.code())) {
            throw new InternalServerErrorException(failure.message());
        }
        throw new BadRequestException(failure.message());
    }

    private void logBasedOnFailure(PaymentFailure failure) {
        if (INTERNAL_ERROR_CODES.contains(failure.code())) {
            log.error("toss payments API 500 error - code: {}, message: {}", failure.code(), failure.message());
        }
        log.warn("toss payments API 400 error - code: {}, message: {}", failure.code(), failure.message());
    }
}
