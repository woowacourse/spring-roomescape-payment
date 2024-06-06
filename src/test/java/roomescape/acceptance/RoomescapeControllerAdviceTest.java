package roomescape.acceptance;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.fixture.MemberFixture.MEMBER_ARU;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

class RoomescapeControllerAdviceTest extends AcceptanceTest {

    @Test
    @DisplayName("권한이 없는 사용자가 admin 페이지에 접근한다.")
    void unAuthorizedMemberTest() {
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String token = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        RestDocumentationFilter documentFilter = document(
                "unauthorized-member",
                requestCookies(cookieDescriptors)
        );

        givenWithSpec().log().all()
                .accept(APPLICATION_JSON_VALUE)
                .cookie("token", token)
                .filter(documentFilter)
                .when().get("/admin")
                .then().log().all()
                .statusCode(403);
    }
}
