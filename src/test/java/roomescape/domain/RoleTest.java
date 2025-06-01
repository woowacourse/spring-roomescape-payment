package roomescape.domain;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RoleTest {

    @ParameterizedTest
    @ValueSource(strings = {"ADMIN", "USER"})
    void 등록된_권한이라면_true를_반환한다(Role role) {
        assertThat(Role.hasRole(role)).isTrue();
    }
}
