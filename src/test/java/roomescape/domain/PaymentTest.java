package roomescape.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.exception.ExceptionType;
import roomescape.exception.RoomescapeException;

class PaymentTest {

    private static final Long DEFAULT_ID = 1L;
    private static final String VALID_PAYMENT_KEY = "validPaymentKey";
    private static final String VALID_ORDER_ID = "WTEST12345";
    private static final BigDecimal VALID_AMOUNT = BigDecimal.valueOf(1000L);
    private static final BigDecimal INVALID_AMOUNT = BigDecimal.valueOf(-1000L);

    @Test
    @DisplayName("결제 키가 비어있는 경우 생성할 수 없는지 확인")
    void createFailWhenEmptyPaymentKey() {
        assertAll(
                () -> assertThatThrownBy(() -> new Payment(DEFAULT_ID, null, VALID_ORDER_ID, VALID_AMOUNT))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage(ExceptionType.EMPTY_PAYMENT_KEY.getMessage()),
                () -> assertThatThrownBy(() -> new Payment(DEFAULT_ID, "", VALID_ORDER_ID, VALID_AMOUNT))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage(ExceptionType.EMPTY_PAYMENT_KEY.getMessage())
        );
    }

    @Test
    @DisplayName("주문 아이디가 비어있는 경우 생성할 수 없는지 확인")
    void createFailWhenEmptyOrderId() {
        assertAll(
                () -> assertThatThrownBy(() -> new Payment(DEFAULT_ID, VALID_PAYMENT_KEY, null, VALID_AMOUNT))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage(ExceptionType.EMPTY_ORDER_ID.getMessage()),
                () -> assertThatThrownBy(() -> new Payment(DEFAULT_ID, VALID_PAYMENT_KEY, "", VALID_AMOUNT))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage(ExceptionType.EMPTY_ORDER_ID.getMessage())
        );
    }

    @Test
    @DisplayName("주문 아이디가 유효하지 않은 경우 생성할 수 없는지 확인")
    void createFailWhenInvalidOrderId() {
        assertThatThrownBy(() -> new Payment(DEFAULT_ID, VALID_PAYMENT_KEY, "INVALID123", VALID_AMOUNT))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage(ExceptionType.INVALID_ORDER_ID.getMessage());
    }

    @Test
    @DisplayName("금액이 비어있는 경우 생성할 수 없는지 확인")
    void createFailWhenEmptyAmount() {
        assertAll(
                () -> assertThatThrownBy(() -> new Payment(DEFAULT_ID, VALID_PAYMENT_KEY, VALID_ORDER_ID, null))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage(ExceptionType.EMPTY_AMOUNT.getMessage())
        );
    }

    @Test
    @DisplayName("금액이 음수인 경우 생성할 수 없는지 확인")
    void createFailWhenInvalidAmount() {
        assertAll(
                () -> assertThatThrownBy(() -> new Payment(DEFAULT_ID, VALID_PAYMENT_KEY, VALID_ORDER_ID, INVALID_AMOUNT))
                        .isInstanceOf(RoomescapeException.class)
                        .hasMessage(ExceptionType.INVALID_AMOUNT.getMessage())
        );
    }

    @Test
    @DisplayName("유효한 정보로 생성할 수 있는지 확인")
    void createSuccess() {
        assertDoesNotThrow(() -> new Payment(DEFAULT_ID, VALID_PAYMENT_KEY, VALID_ORDER_ID, VALID_AMOUNT));
    }
}
