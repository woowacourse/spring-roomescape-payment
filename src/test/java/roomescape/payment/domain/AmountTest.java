package roomescape.payment.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.global.exception.IllegalRequestException;
import roomescape.reservation.domain.ReservationDate;

class AmountTest {
    @DisplayName("금액이 null인 경우 생성 시 예외가 발생한다")
    @Test
    void should_throw_exception_when_value_is_null() {
        assertThatThrownBy(() -> new Amount(null))
                .isInstanceOf(IllegalRequestException.class);
    }

    @DisplayName("금액이 소수인 경우 생성 시 예외가 발생한다")
    @Test
    void should_throw_exception_when_value_is_decimal() {
        assertThatThrownBy(() -> new Amount(new BigDecimal("33.123")))
                .isInstanceOf(IllegalRequestException.class);
    }

    @DisplayName("금액이 음수인 경우 생성 시 예외가 발생한다")
    @Test
    void should_throw_exception_when_value_is_negative() {
        assertThatThrownBy(() -> new Amount(new BigDecimal("-1")))
                .isInstanceOf(IllegalRequestException.class);
    }
}
