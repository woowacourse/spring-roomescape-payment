package roomescape.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import roomescape.domain.member.Role;
import roomescape.exception.UnAuthorizedException;

class RoomescapeControllerAdviceTest extends ControllerTest {

    @Test
    @DisplayName("권한이 없는 사용자가 admin 페이지에 접근한다.")
    void unAuthorizedMemberTest() {
        BDDMockito.willThrow(new UnAuthorizedException())
                .given(credentialContext)
                .validatePermission(Role.ADMIN);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        RestDocumentationResultHandler handler = document(
                "unauthorized-member",
                requestCookies(cookieDescriptors)
        );

        givenWithSpec().log().all()
                .accept(APPLICATION_JSON_VALUE)
                .cookie("token", "member-token")
                .when().get("/admin")
                .then().log().all()
                .apply(handler)
                .statusCode(403);
    }
}
