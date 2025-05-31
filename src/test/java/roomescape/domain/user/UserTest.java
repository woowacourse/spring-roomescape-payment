package roomescape.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import roomescape.exception.BusinessRuleViolationException;
import roomescape.exception.InvalidInputException;

class UserTest {

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름이 공백이거나 null일 경우 예외를 던진다.")
    void validateNameLength_WhenNullOrBlank(String name) {
        // given
        var email = "user3@email.com";
        var password = "password3";

        // when & then
        assertThatThrownBy(() -> User.register(name, email, password))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("이름은 공백일 수 없습니다.");
    }

    @Test
    @DisplayName("이름이 5자를 초과하는 경우 예외를 던진다.")
    void validateNameLength_WhenOverLimit() {
        // given
        var tooLongName = "긴사용자이름";
        var email = "user3@email.com";
        var password = "password3";

        // when & then
        assertThatThrownBy(() -> User.register(tooLongName, email, password))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("이름은 5자를 넘길 수 없습니다.");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이메일이 공백이거나 null일 경우 예외를 던진다.")
    void validateEmail_WhenNullOrBlank(String email) {
        // given
        var name = "사용자3";
        var password = "password3";

        // when & then
        assertThatThrownBy(() -> User.register(name, email, password))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("비밀번호는 공백일 수 없습니다.");

    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("비밀번호가 공백이거나 null일 경우 예외를 던진다.")
    void validatePasswordLength_WhenNullOrBlank(String password) {
        // given
        var name = "사용자3";
        var email = "user3@email.com";

        // when & then
        assertThatThrownBy(() -> User.register(name, email, password))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("비밀번호는 공백일 수 없습니다.");
    }

    @Test
    @DisplayName("비밀번호가 30글자 이상이면 예외를 던진다.")
    void validatePasswordLength_WhenOverLimit() {
        // given
        var name = "사용자3";
        var email = "user3@email.com";
        var tooLongPassword = "p".repeat(31);

        // when & then
        assertThatThrownBy(() -> User.register(name, email, tooLongPassword))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessage("비밀번호는 30자를 넘길 수 없습니다.");
    }

    @Test
    @DisplayName("비밀번호 일치 여부를 알 수 있다.")
    void matchesPassword() {
        var user = User.ofExisting(1L, "어드민", UserRole.ADMIN, "admin@email.com", "password");
        assertAll(
                () -> assertThat(user.matchesPassword("password")).isTrue(),
                () -> assertThat(user.matchesPassword("PASSWORD")).isFalse()
        );
    }
}
