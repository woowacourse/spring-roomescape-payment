package roomescape.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.member.exception.InvalidMemberException;

class EmailTest {

    @Test
    @DisplayName("유효한 이메일로 Email을 생성한다.")
    void createEmail_whenValidRequest() {
        // given
        String validEmail = "test@example.com";

        // when
        Email email = Email.from(validEmail);

        // then
        assertThat(email.value()).isEqualTo(validEmail);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이메일이 null이거나 비어있으면 예외가 발생한다.")
    void createEmail_whenInvalidInput_throwsException(String invalidEmail) {
        // when & then
        assertThatThrownBy(() -> Email.from(invalidEmail))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessage("이메일은 비어있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "invalid.email",
            "test@",
            "@example.com",
            "test@example",
            "test.example.com",
            "test@.com",
            "test@example.",
            "test@example..com"
    })
    @DisplayName("이메일 형식이 올바르지 않으면 예외가 발생한다.")
    void createEmail_whenInvalidFormat_throwsException(String invalidEmail) {
        // when & then
        assertThatThrownBy(() -> Email.from(invalidEmail))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessage("올바른 이메일 형식이 아닙니다.");
    }

    @Test
    @DisplayName("이메일이 100자를 초과하면 예외가 발생한다.")
    void createEmail_whenTooLongEmail_throwsException() {
        // given
        String tooLongEmail = "a".repeat(91) + "@example.com";

        // when & then
        assertThatThrownBy(() -> Email.from(tooLongEmail))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessage("이메일은 100글자 이하이어야합니다.");
    }
}
