package roomescape.payment.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import roomescape.global.exception.TossPaymentConfirmClientErrorCode;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossErrorResponse(String message, String code) {

    public boolean isClientError() {
        try {
            TossPaymentConfirmClientErrorCode.valueOf(code);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
