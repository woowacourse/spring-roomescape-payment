package roomescape.payment.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import roomescape.common.exception.impl.DeserializationException;
import roomescape.common.exception.impl.ExternalApiException;
import roomescape.payment.application.dto.TossErrorResponse;

public class TossPaymentErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(TossPaymentErrorHandler.class);

    private final ObjectMapper objectMapper;

    public TossPaymentErrorHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean hasError(final ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(
        final URI url,
        final HttpMethod method,
        final ClientHttpResponse response
    ) throws IOException {
        HttpStatus status = (HttpStatus) response.getStatusCode();
        TossErrorResponse error = parseErrorResponse(response.getBody());

        if (TossErrorCodesTreatedAsClientError.contains(error.code())) {
            throw new ExternalApiException(status, error.message());
        }
        logger.error("Toss API Unexpected error occurred: Code: {}, Message: {}", error.code(),
            error.message());
        throw new ExternalApiException(HttpStatus.INTERNAL_SERVER_ERROR,
            "결제가 실패했습니다. 관리자 문의가 필요합니다.");
    }

    private TossErrorResponse parseErrorResponse(final InputStream bodyStream) {
        try (InputStream errorBody = bodyStream) {
            return objectMapper.readValue(errorBody, TossErrorResponse.class);
        } catch (Exception e) {
            throw new DeserializationException("Error parsing Toss error", e);
        }
    }

    public enum TossErrorCodesTreatedAsClientError {
        ALREADY_PROCESSED_PAYMENT,
        PROVIDER_ERROR,
        EXCEED_MAX_CARD_INSTALLMENT_PLAN,
        INVALID_REQUEST,
        NOT_ALLOWED_POINT_USE,
        INVALID_API_KEY,
        INVALID_REJECT_CARD,
        INVALID_CARD_EXPIRATION,
        INVALID_STOPPED_CARD,
        EXCEED_MAX_DAILY_PAYMENT_COUNT,
        NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT,
        INVALID_CARD_INSTALLMENT_PLAN,
        NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN,
        EXCEED_MAX_PAYMENT_AMOUNT,
        INVALID_CARD_LOST_OR_STOLEN,
        RESTRICTED_TRANSFER_ACCOUNT,
        INVALID_CARD_NUMBER,
        EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT,
        EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT,
        CARD_PROCESSING_ERROR,
        EXCEED_MAX_AMOUNT,
        INVALID_ACCOUNT_INFO_RE_REGISTER,
        NOT_AVAILABLE_PAYMENT,
        UNAPPROVED_ORDER_ID,
        EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT,
        REJECT_ACCOUNT_PAYMENT,
        REJECT_CARD_PAYMENT,
        REJECT_CARD_COMPANY,
        FORBIDDEN_REQUEST,
        REJECT_TOSSPAY_INVALID_ACCOUNT,
        EXCEED_MAX_AUTH_COUNT,
        EXCEED_MAX_ONE_DAY_AMOUNT,
        NOT_AVAILABLE_BANK,
        INVALID_PASSWORD,
        FDS_ERROR,
        NOT_FOUND_PAYMENT,
        NOT_FOUND_PAYMENT_SESSION,
        FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING,
        FAILED_INTERNAL_SYSTEM_PROCESSING,
        UNKNOWN_PAYMENT_ERROR;

        private static final Set<String> CODE_SET = Stream.of(values())
            .map(Enum::name)
            .collect(Collectors.toSet());

        public static boolean contains(final String code) {
            return CODE_SET.contains(code);
        }
    }
}
