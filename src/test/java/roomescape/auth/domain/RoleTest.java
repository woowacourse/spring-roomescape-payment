package roomescape.auth.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoleTest {

    @DisplayName("USER 권한 인지 확인한다.")
    @Test
    void isAdmin() {
        Role admin = Role.ADMIN;
        Role member = Role.USER;

        assertAll(
                () -> assertThat(admin.isUser()).isFalse(),
                () -> assertThat(member.isUser()).isTrue()
        );
    }
}
