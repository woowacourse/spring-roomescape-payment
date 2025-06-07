package roomescape.api.auth;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static roomescape.api.fixture.DocumentationFixture.createDocumentWithDefaultPath;
import static roomescape.api.member.MemberDocumentationFixture.MEMBER_EMAIL_FIELD_DESCRIPTOR;
import static roomescape.api.member.MemberDocumentationFixture.MEMBER_NAME_FIELD_DESCRIPTOR;
import static roomescape.api.member.MemberDocumentationFixture.MEMBER_PASSWORD_FIELD_DESCRIPTOR;

import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

public class AuthDocumentationFixture {

    // descriptors
    public static final HeaderDescriptor SET_COOKIE_HEADER_DESCRIPTOR = headerWithName("Set-Cookie")
            .description("로그인 토큰 쿠키 설정");
    public static final CookieDescriptor MEMBER_TOKEN_COOKIE_DESCRIPTOR = cookieWithName("token")
            .description("로그인한 회원의 토큰");
    public static final HeaderDescriptor UNSET_COOKIE_HEADER_DESCRIPTOR = headerWithName("Set-Cookie")
            .description("쿠키 삭제 설정");

    // documents
    public static final RestDocumentationFilter LOGIN_DOCUMENT = createDocumentWithDefaultPath(
            requestFields(
                    MEMBER_EMAIL_FIELD_DESCRIPTOR,
                    MEMBER_PASSWORD_FIELD_DESCRIPTOR
            ),
            responseHeaders(SET_COOKIE_HEADER_DESCRIPTOR)
    );
    public static final RestDocumentationFilter LOGOUT_DOCUMENTATION = createDocumentWithDefaultPath(
            requestCookies(MEMBER_TOKEN_COOKIE_DESCRIPTOR),
            responseHeaders(UNSET_COOKIE_HEADER_DESCRIPTOR)
    );
    public static final RestDocumentationFilter LOGIN_CHECK_DOCUMENT = createDocumentWithDefaultPath(
            requestCookies(MEMBER_TOKEN_COOKIE_DESCRIPTOR),
            responseFields(MEMBER_NAME_FIELD_DESCRIPTOR)
    );
}
