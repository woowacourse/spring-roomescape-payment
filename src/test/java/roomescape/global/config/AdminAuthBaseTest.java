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
public class AdminAuthBaseTest {

    @Autowired
    protected TokenProvider tokenProvider;

    @Autowired
    protected CookieManager cookieManager;

    protected String adminToken;
    protected LoginMember adminLoginMember;

    @BeforeEach
    void setUpAdminAuth() {
        Member adminMember = Member.builder()
                .id(1L)
                .email("admin@example.com")
                .password("password")
                .name("관리자")
                .role(MemberRole.ADMIN)
                .build();

        adminToken = tokenProvider.createToken(adminMember);
        adminLoginMember = new LoginMember(
                adminMember.getId(),
                adminMember.getName(),
                adminMember.getRole()
        );
    }

    protected MockHttpServletRequestBuilder addAuthCookie(MockHttpServletRequestBuilder request) {
        ResponseCookie responseCookie = cookieManager.generateLoginCookie(adminToken);

        Cookie cookie = new Cookie(
                responseCookie.getName(),
                responseCookie.getValue()
        );

        cookie.setPath("/");
        cookie.setHttpOnly(true);
        return request.cookie(cookie);
    }
}
