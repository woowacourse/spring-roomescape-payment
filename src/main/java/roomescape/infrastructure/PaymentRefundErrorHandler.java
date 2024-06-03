package roomescape.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.core.exception.PaymentException;

public class PaymentRefundErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentApproveErrorHandler.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().is4xxClientError() || response.getStatusCode().is5xxServerError();
    }

    @Override
    public void handleError(final ClientHttpResponse response) throws IOException {
        logger.error("환불 과정에서 문제가 발생했습니다. 상태 코드: {}, 상태 텍스트: {}",
                response.getStatusCode(), response.getStatusText());
        TossError tossError = objectMapper.readValue(response.getBody(), TossError.class);
        if (isNotBadRequestOfClient(response, tossError)) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "환불 과정에서 문제가 발생했습니다.");
        }
        throw new PaymentException(HttpStatus.BAD_REQUEST, tossError.message());
    }

    private boolean isNotBadRequestOfClient(ClientHttpResponse response, TossError tossError) throws IOException {
        return response.getStatusCode().is5xxServerError() || response.getStatusCode() == HttpStatus.NOT_FOUND
                || Objects.equals(tossError.code(), "INVALID_AUTHORIZE_AUTH")
                || Objects.equals(tossError.code(), "INCORRECT_BASIC_AUTH_FORMAT");
    }
}
