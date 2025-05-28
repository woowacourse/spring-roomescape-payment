package roomescape.order;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import roomescape.order.dto.PaymentConfirmRequest;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Component
@Slf4j
@AllArgsConstructor
public class PaymentClient {

    private static final String TEST_WIDGET_SECRET_KEY = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6:";
    private static final String ENCODED_SECRET_KEY = Base64.getEncoder().encodeToString(TEST_WIDGET_SECRET_KEY.getBytes());
    private static final String URL_PREFIX = "https://api.tosspayments.com/v1/payments";

    private final RestClient restClient;

    public void confirm(final PaymentConfirmRequest request) {
        try {
            ResponseEntity<Void> response = restClient.post()
                    .uri(URL_PREFIX + "/confirm")
                    .header("Authorization", "Basic awdawdaw" + ENCODED_SECRET_KEY)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new IllegalStateException("Payment confirmation failed");
            }
        } catch (HttpClientErrorException e) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                TossErrorResponse errorResponse = objectMapper.readValue(e.getResponseBodyAsString(), TossErrorResponse.class);
                TossErrorCode.fromCode(errorResponse.code)
                        .ifPresentOrElse(errorCode -> {
                            if (errorCode.isUserVisible()) {
                                throw new PaymentException("결제 승인 실패: " + errorResponse.message, e);
                            }
                            throw new PaymentException("결제 승인에 실패하였습니다.", e);
                        }, () -> {
                            throw new PaymentException("예상치 못한 오류로 인해 결제 승인에 실패하였습니다.", e);
                        });
            } catch (IOException ioException) {
                throw new PaymentException("결제 승인에 실패하였습니다.", ioException);
            }
        }
    }

    public enum TossErrorCode {

        ALREADY_PROCESSED_PAYMENT(true),
        PROVIDER_ERROR(true),
        EXCEED_MAX_CARD_INSTALLMENT_PLAN(true),
        INVALID_REQUEST(false),
        NOT_ALLOWED_POINT_USE(true),
        INVALID_API_KEY(false),
        INVALID_REJECT_CARD(true),
        BELOW_MINIMUM_AMOUNT(true),
        INVALID_CARD_EXPIRATION(true),
        INVALID_STOPPED_CARD(true),
        EXCEED_MAX_DAILY_PAYMENT_COUNT(true),
        NOT_SUPPORTED_INSTALLMENT_PLAN_CARD_OR_MERCHANT(true),
        INVALID_CARD_INSTALLMENT_PLAN(true),
        NOT_SUPPORTED_MONTHLY_INSTALLMENT_PLAN(true),
        EXCEED_MAX_PAYMENT_AMOUNT(true),
        NOT_FOUND_TERMINAL_ID(true),
        INVALID_AUTHORIZE_AUTH(false),
        INVALID_CARD_LOST_OR_STOLEN(true),
        RESTRICTED_TRANSFER_ACCOUNT(true),
        INVALID_CARD_NUMBER(true),
        INVALID_UNREGISTERED_SUBMALL(false),
        NOT_REGISTERED_BUSINESS(false),
        EXCEED_MAX_ONE_DAY_WITHDRAW_AMOUNT(true),
        EXCEED_MAX_ONE_TIME_WITHDRAW_AMOUNT(true),
        CARD_PROCESSING_ERROR(true),
        EXCEED_MAX_AMOUNT(true),
        INVALID_ACCOUNT_INFO_RE_REGISTER(true),
        NOT_AVAILABLE_PAYMENT(true),
        UNAPPROVED_ORDER_ID(false),
        EXCEED_MAX_MONTHLY_PAYMENT_AMOUNT(true),
        UNAUTHORIZED_KEY(false),
        REJECT_ACCOUNT_PAYMENT(true),
        REJECT_CARD_PAYMENT(true),
        REJECT_CARD_COMPANY(true),
        FORBIDDEN_REQUEST(true),
        REJECT_TOSSPAY_INVALID_ACCOUNT(true),
        EXCEED_MAX_AUTH_COUNT(true),
        EXCEED_MAX_ONE_DAY_AMOUNT(true),
        NOT_AVAILABLE_BANK(true),
        INVALID_PASSWORD(true),
        INCORRECT_BASIC_AUTH_FORMAT(false),
        FDS_ERROR(true),
        NOT_FOUND_PAYMENT(false),
        NOT_FOUND_PAYMENT_SESSION(true),
        FAILED_PAYMENT_INTERNAL_SYSTEM_PROCESSING(true),
        FAILED_INTERNAL_SYSTEM_PROCESSING(true),
        UNKNOWN_PAYMENT_ERROR(true);

        private final boolean userVisible;

        TossErrorCode(boolean userVisible) {
            this.userVisible = userVisible;
        }

        public static Optional<TossErrorCode> fromCode(String code) {
            try {
                return Optional.of(TossErrorCode.valueOf(code));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        }

        public boolean isUserVisible() {
            return userVisible;
        }
    }


    static public class PaymentException extends RuntimeException {

        public PaymentException(final String message, final Throwable cause) {
            super(message, cause);
        }

    }

    record TossErrorResponse(
            String code,
            String message,
            Object data
    ) {
    }
}
