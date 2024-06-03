package roomescape.paymenthistory.error;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TossPaymentServerErrorCodeTest {

    @ParameterizedTest
    @ValueSource(strings = {"INCORRECT_BASIC_AUTH_FORMAT", "INVALID_PARAMETER", "UNAUTHORIZED_KEY"})
    void existInAdminErrorCode(String errorCode) {
        assertTrue(TossPaymentServerErrorCode.existInAdminErrorCode(errorCode));
    }
}
