package roomescape.domain.member;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    @DisplayName("문자열을 정상적으로 role로 변환한다.")
    void role_of_success_test() {
        String normalValue = "normal";
        String adminValue = "admin";

        Role normal = Role.of(normalValue);
        Role admin = Role.of(adminValue);

        assertAll(
                () -> assertThat(normal).isEqualTo(Role.NORMAL),
                () -> assertThat(admin).isEqualTo(Role.ADMIN)
        );
    }

    @Test
    @DisplayName("유효하지 않은 문자열 입력시 예외를 발생시킨다.")
    void throw_exception_invalid_input() {
        String invalidValue = "invalid";

        assertThatThrownBy(() -> Role.of(invalidValue))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
