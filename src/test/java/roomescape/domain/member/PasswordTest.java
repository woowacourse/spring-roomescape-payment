package roomescape.domain.member;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.BaseTest;
import roomescape.exception.RoomEscapeException;

class PasswordTest extends BaseTest {

    @Test
    void 비밀번호가_4자_미만일_경우_예외_발생() {
        // given
        String password = "123";

        // when, then
        assertThatThrownBy(() -> new Password(password))
                .isInstanceOf(RoomEscapeException.class);
    }
}
