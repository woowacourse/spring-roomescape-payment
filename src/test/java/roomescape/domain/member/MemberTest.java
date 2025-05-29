package roomescape.domain.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.infrastructure.error.exception.MemberException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class MemberTest {

    @Test
    @DisplayName("정상적인 회원 정보로 Member를 생성할 수 있다.")
    void 정상_회원_생성() {
        assertThatCode(() -> new Member("벨로", new Email("test@email.com"), "password", MemberRole.NORMAL))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("이름이 공백이면 예외가 발생한다.")
    void 이름이_공백이면_예외() {
        assertThatCode(() -> new Member(" ", new Email("test@email.com"), "password", MemberRole.NORMAL))
                .isInstanceOf(MemberException.class)
                .hasMessage("사용자명은 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("이름이 너무 길면 예외가 발생한다.")
    void 이름이_너무_길면_예외() {
        final String longName = "a".repeat(11);
        assertThatCode(() -> new Member(longName, new Email("test@email.com"), "password", MemberRole.NORMAL))
                .isInstanceOf(MemberException.class)
                .hasMessage("사용자명은 10자 이하여야 합니다.");
    }

    @Test
    @DisplayName("비밀번호가 공백이면 예외가 발생한다.")
    void 비밀번호가_공백이면_예외() {
        assertThatCode(() -> new Member("벨로", new Email("test@email.com"), " ", MemberRole.NORMAL))
                .isInstanceOf(MemberException.class)
                .hasMessage("비밀번호는 비어있을 수 없습니다.");
    }

    @Test
    @DisplayName("비밀번호가 일치하지 않으면 true를 반환한다.")
    void 비밀번호가_일치하지_않으면_true() {
        // given
        final Member member = new Member("벨로", new Email("test@email.com"), "password", MemberRole.NORMAL);

        // when
        final boolean result = member.isNotPassword("wrongPassword");

        // then
        assertThat(result)
                .isTrue();
    }

    @Test
    @DisplayName("비밀번호가 일치하면 false를 반환한다.")
    void 비밀번호가_일치하면_false() {
        // given
        final Member member = new Member("벨로", new Email("test@email.com"), "password", MemberRole.NORMAL);

        // when
        final boolean result = member.isNotPassword("password");

        // then
        assertThat(result)
                .isFalse();
    }

    @Test
    @DisplayName("ADMIN 권한이면 isAdmin은 true를 반환한다.")
    void 관리자이면_true() {
        // given
        final Member admin = new Member("어드민", new Email("admin@email.com"), "pw", MemberRole.ADMIN);

        // when
        final boolean isAdmin = admin.isAdmin();

        // then
        assertThat(isAdmin)
                .isTrue();
    }

    @Test
    @DisplayName("NORMAL 권한이면 isAdmin은 false를 반환한다.")
    void 일반회원이면_false() {
        // given
        final Member user = new Member("유저", new Email("user@email.com"), "pw", MemberRole.NORMAL);

        // when
        final boolean isAdmin = user.isAdmin();

        // then
        assertThat(isAdmin)
                .isFalse();
    }
}
