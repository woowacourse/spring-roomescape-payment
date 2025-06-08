package roomescape.global.config;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import roomescape.auth.application.LoginMember;
import roomescape.auth.infrastructure.TokenProvider;
import roomescape.auth.presentation.CookieManager;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;

@SpringBootTest
public class UserAuthBaseTest {

    @Autowired
    protected TokenProvider tokenProvider;

    @Autowired
    protected CookieManager cookieManager;

    protected String userToken;
    protected LoginMember userLoginMember;

    @BeforeEach
    void setUpAdminAuth() {
        Member adminMember = Member.builder()
                .id(1L)
                .email("admin@example.com")
                .password("password")
                .name("유저")
                .role(MemberRole.MEMBER)
                .build();

        userToken = tokenProvider.createToken(adminMember);
        userLoginMember = new LoginMember(
                adminMember.getId(),
                adminMember.getName(),
                adminMember.getRole()
        );
    }

    protected MockHttpServletRequestBuilder addAuthCookie(MockHttpServletRequestBuilder request) {
        ResponseCookie responseCookie = cookieManager.generateLoginCookie(userToken);

        Cookie cookie = new Cookie(
                responseCookie.getName(),
                responseCookie.getValue()
        );

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return request.cookie(cookie);
    }
}
