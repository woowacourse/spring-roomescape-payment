package roomescape.member.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import roomescape.member.exception.InvalidMemberException;

class MemberNameTest {

    @Test
    @DisplayName("유효한 이름으로 MemberName을 생성한다.")
    void createMemberName_whenValidRequest_returnName() {
        // given
        String validName = "홍길동";

        // when
        MemberName memberName = MemberName.from(validName);

        // then
        assertThat(memberName.value()).isEqualTo(validName);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("이름이 null이거나 비어있으면 예외가 발생한다.")
    void createMemberName_whenInvalidInput_throwsException(String invalidName) {
        // when & then
        assertThatThrownBy(() -> MemberName.from(invalidName))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessage("이름은 비어있을 수 없습니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {"aaaaaaaaaaaaaaa", "가가가가가가가가가가가가가가"})
    @DisplayName("이름이 10자를 초과하면 예외가 발생한다.")
    void createMemberName_whenTooLongName_throwsException(String tooLongName) {
        // when & then
        assertThatThrownBy(() -> MemberName.from(tooLongName))
                .isInstanceOf(InvalidMemberException.class)
                .hasMessage("이름은 10글자 이하이어야합니다.");
    }
}
