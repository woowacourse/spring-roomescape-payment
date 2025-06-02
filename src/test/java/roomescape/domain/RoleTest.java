package roomescape.domain;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import roomescape.application.exception.AuthException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RoleTest {

    @Test
    void 문자열에서_권한을_반환한다() {
        assertAll(
                () -> assertThat(Role.from("ADMIN")).isEqualTo(Role.ADMIN),
                () -> assertThat(Role.from("USER")).isEqualTo(Role.USER)
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {"NOTROLE", "GUEST", "NULL"})
    void 권한에_없는_문자열이라면_예외를_반환한다(String name) {
        assertThatThrownBy(() -> Role.from(name))
                .isInstanceOf(AuthException.class);
    }
}
