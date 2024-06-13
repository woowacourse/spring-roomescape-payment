package roomescape.paymenthistory.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TimeOutTest {

    @DisplayName("TimeOut 시간이 0혹은 음수인 경우 예외를 던진다.")
    @ParameterizedTest
    @ValueSource(strings = {"0", "-1", "-202121"})
    void getTimeOutTime_whenTimeIsIllegalNumber() {
        assertThatThrownBy(() -> new TimeOut("0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TimeOut 시간은 0이거나 음수가 될 수 없습니다.");
    }

    @DisplayName("TimeOut 시간이 null인 경우 예외를 던진다.")
    @Test
    void getTimeOutTime_whenTimeIsNull() {
        assertThatThrownBy(() -> new TimeOut(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TimeOut 시간이 null 입니다.");
    }
}
