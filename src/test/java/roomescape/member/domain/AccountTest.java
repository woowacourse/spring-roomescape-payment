package roomescape.member.domain;

import org.junit.jupiter.api.Test;
import roomescape.common.exception.InvalidInputException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    void 멤버가_null이면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> Account.withoutId(null, Password.from("1234")))
                .isInstanceOf(InvalidInputException.class)
                .hasMessage("Account.member 은(는) null일 수 없습니다.");

    }

    @Test
    void 비밀번호가_null이면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> Account.withoutId(
                Member.withoutId(
                        MemberName.from("siso"),
                        MemberEmail.from("siso@gmail.com"),
                        Role.ADMIN
                ), null)
        ).isInstanceOf(InvalidInputException.class)
                .hasMessage("Account.password 은(는) null일 수 없습니다.");


    }
}
