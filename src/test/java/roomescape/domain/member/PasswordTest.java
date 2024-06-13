package roomescape.domain.member;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.exception.RoomescapeException;

class PasswordTest {
    @DisplayName("비밀번호가 8~20자 범위가 아니거나, 대소문자, 숫자, 특수문자가 포함되지 않거나 공백이 포함되면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"1234567", "1234567890abcdefghijk", "wootecoCrew!", "wootecoCrew6 ", "wootecoCrew6! "})
    void createInvalidPasswordTest(String password) {
        assertThatCode(() -> new Password(password))
                .isInstanceOf(RoomescapeException.class)
                .hasMessage("비밀번호는 8~20자 범위의 최소 하나의 대소문자, 숫자, 특수문자가 포함되어야 합니다.");
    }
}
