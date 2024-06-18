package roomescape.member.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.system.exception.RoomEscapeException;

class MemberTest {

    @Test
    @DisplayName("Member 객체를 생성할 때 Role은 반드시 입력되어야 한다.")
    void createMemberWithoutRole() {
        // given
        String name = "name";
        String email = "email";
        String password = "password";

        // when
        Role role = null;

        // then
        Assertions.assertThatThrownBy(() -> new Member(name, email, password, null))
                .isInstanceOf(RoomEscapeException.class);
    }
}
