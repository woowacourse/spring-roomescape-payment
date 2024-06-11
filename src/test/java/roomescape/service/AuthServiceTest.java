package roomescape.service;

import jakarta.servlet.http.Cookie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.exception.BadRequestException;
import roomescape.model.Member;
import roomescape.model.Role;
import roomescape.service.fake.FakeJwtTokenProvider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static roomescape.model.Role.ADMIN;
import static roomescape.model.Role.MEMBER;

class AuthServiceTest {

    private FakeJwtTokenProvider jwtTokenProvider = new FakeJwtTokenProvider();
    private AuthService authService = new AuthService(jwtTokenProvider);

    @DisplayName("사용자를 제공하면 쿠키를 만들어 준다.")
    @Test
    void should_create_cookie_when_given_member() {
        Member member = new Member(1L, "수달", MEMBER, "otter@email.com", "1234");

        Cookie cookie = authService.createCookieByMember(member);

        assertSoftly(softAssertions -> {
            softAssertions.assertThat(cookie.getName()).isEqualTo("token");
            softAssertions.assertThat(cookie.getPath()).isEqualTo("/");
            softAssertions.assertThat(cookie.isHttpOnly()).isTrue();
        });
    }

    @DisplayName("쿠키를 제공하면 쿠키 아이디가 있는지 확인하고 없으면 예외를 발생시킨다.")
    @Test
    void should_throw_exception_when_not_exist_cookie_id() {
        Member member = new Member(1L, "수달", MEMBER, "otter@email.com", "1234");
        String token = jwtTokenProvider.createToken(member);
        Cookie[] cookies = new Cookie[]{new Cookie("id", token)};

        assertThatThrownBy(() -> authService.getMemberIdByCookie(cookies))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("[ERROR] 아이디가 token인 쿠키가 없습니다.");
    }

    @DisplayName("쿠키를 제공하면 쿠키 아이디가 있는지 확인하고 있다면 사용자 아이디를 반환한다.")
    @Test
    void should_return_member_id_when_give_cookie() {
        Member member = new Member(1L, "수달", MEMBER, "otter@email.com", "1234");
        String token = jwtTokenProvider.createToken(member);
        Cookie[] cookies = new Cookie[]{new Cookie("token", token)};

        Long memberId = authService.getMemberIdByCookie(cookies);

        assertThat(memberId).isEqualTo(1L);
    }

    @DisplayName("쿠키를 주면 권한을 반환한다.")
    @Test
    void should_return_role_when_give_cookie() {
        Member adminMember = new Member(1L, "수달", ADMIN, "otter@email.com", "1234");
        Member memberMember = new Member(2L, "배키", MEMBER, "dmsgml@email.com", "1111");
        String adminToken = jwtTokenProvider.createToken(adminMember);
        String memberToken = jwtTokenProvider.createToken(memberMember);

        Role admin = authService.getRoleByCookie(new Cookie[]{new Cookie("token", adminToken)});
        Role member = authService.getRoleByCookie(new Cookie[]{new Cookie("token", memberToken)});

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(admin).isEqualTo(ADMIN);
            softAssertions.assertThat(member).isEqualTo(MEMBER);
        });
    }
}
