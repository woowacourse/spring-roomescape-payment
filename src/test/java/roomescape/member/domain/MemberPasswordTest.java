package roomescape.member.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.advice.exception.RoomEscapeException;

class MemberPasswordTest {
    @DisplayName("예약자 비밀번호가 null일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsNull() {
        assertThatThrownBy(() -> new MemberPassword(null))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("사용자 비밀번호는 null 일 수 없습니다.");
    }

    @DisplayName("예약자 비밀번호가 1자 미만일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsEmpty() {
        assertThatThrownBy(() -> new MemberPassword(""))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("사용자 비밀번호는 1글자 이상 100글자 이하이어야 합니다.");
    }

    @DisplayName("예약자 비밀번호가 100자 초과일 경우 예외를 던진다.")
    @Test
    void validateTest_whenValueIsLong() {
        assertThatThrownBy(() -> new MemberPassword("a".repeat(101)))
                .isInstanceOf(RoomEscapeException.class)
                .hasMessage("사용자 비밀번호는 1글자 이상 100글자 이하이어야 합니다.");
    }
}
