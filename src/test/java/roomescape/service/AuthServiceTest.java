package roomescape.service;

import jakarta.servlet.http.Cookie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.BaseTest;
import roomescape.exception.BadRequestException;
import roomescape.model.Member;
import roomescape.model.Role;
import roomescape.service.fake.FakeJwtTokenProvider;
import roomescape.service.fixture.MemberBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static roomescape.model.Role.ADMIN;
import static roomescape.model.Role.MEMBER;

class AuthServiceTest extends BaseTest {

    private FakeJwtTokenProvider jwtTokenProvider = new FakeJwtTokenProvider();
    private AuthService authService = new AuthService(jwtTokenProvider);

    @DisplayName("사용자를 제공하면 쿠키를 만들어 준다.")
    @Test
    void should_create_cookie_when_given_member() {
        Member member = MemberBuilder.builder().build();

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
        Member member = MemberBuilder.builder().build();
        String token = jwtTokenProvider.createToken(member);
        Cookie[] cookies = new Cookie[]{new Cookie("noToken", token)};

        assertThatThrownBy(() -> authService.findMemberIdByCookie(cookies))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("[ERROR] 아이디가 token인 쿠키가 없습니다.");
    }

    @DisplayName("쿠키를 제공하면 쿠키 아이디가 있는지 확인하고 있다면 사용자 아이디를 반환한다.")
    @Test
    void should_return_member_id_when_give_cookie() {
        Member member = MemberBuilder.builder().id(1L).build();
        String token = jwtTokenProvider.createToken(member);
        Cookie[] cookies = new Cookie[]{new Cookie("token", token)};

        Long memberId = authService.findMemberIdByCookie(cookies);

        assertThat(memberId).isEqualTo(1L);
    }

    @DisplayName("쿠키를 주면 권한을 반환한다.")
    @Test
    void should_return_role_when_give_cookie() {
        Member adminMember = MemberBuilder.builder().role(ADMIN).build();
        Member memberMember = MemberBuilder.builder().role(MEMBER).build();
        String adminToken = jwtTokenProvider.createToken(adminMember);
        String memberToken = jwtTokenProvider.createToken(memberMember);

        Role admin = authService.findRoleByCookie(new Cookie[]{new Cookie("token", adminToken)});
        Role member = authService.findRoleByCookie(new Cookie[]{new Cookie("token", memberToken)});

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(admin).isEqualTo(ADMIN);
            softAssertions.assertThat(member).isEqualTo(MEMBER);
        });
    }
}
