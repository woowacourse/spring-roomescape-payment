package roomescape.member.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.advice.exception.RoomEscapeException;

class MemberTest {
    @DisplayName("이름이 비어있을 때 예외를 던진다.")
    @Test
    void validateThemeTest_whenNameIsNull() {
        assertThatThrownBy(() -> new Member(1L, null, "abc@abc.com"))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("사용자 이름은 null 일 수 없습니다.");
    }

    @DisplayName("이메일이 비어있을 때 예외를 던진다.")
    @Test
    void validateThemeTest_whenEmailIsNull() {
        assertThatThrownBy(() -> new Member(1L, "커찬", null))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("예약 이메일은 null 일 수 없습니다.");
    }
}
