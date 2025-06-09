package roomescape.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import roomescape.member.exception.InvalidMemberException;

class PasswordTest {

    @Test
    @DisplayName("유효한 비밀번호로 Password를 생성한다.")
    void createPassword() {
        // given
        String validPassword = "password123";

        // when
        Password password = Password.from(validPassword);

        // then
        assertThat(password.value()).isEqualTo(validPassword);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("비밀번호가 null이거나 비어있으면 예외가 발생한다.")
    void createPasswordWithInvalidInput(String invalidPassword) {
        // when & then
        assertThatThrownBy(() -> Password.from(invalidPassword))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessage("비밀번호는 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("비밀번호 길이가 100글자를 초과하면 예외가 발생한다.")
    void createPasswordWithInvalidLength() {
        String invalidPassword = "a".repeat(101);
        // when & then
        assertThatThrownBy(() -> Password.from(invalidPassword))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessage("비밀번호는 100글자 이하여야 합니다.");
    }
}
