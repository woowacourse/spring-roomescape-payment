package roomescape.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import roomescape.dto.MemberResponse;
import roomescape.dto.auth.TokenRequest;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.FieldDescriptorFixture.loginFieldDescriptor;
import static roomescape.FieldDescriptorFixture.memberFieldDescriptor;
import static roomescape.FieldDescriptorFixture.tokenCookieDescriptor;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.MEMBER_CAT_EMAIL;
import static roomescape.TestFixture.MEMBER_CAT_NAME;
import static roomescape.TestFixture.MEMBER_PASSWORD;

public class AuthAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("[공통] 로그인한다.")
    void login() {
        TokenRequest request = new TokenRequest(ADMIN_EMAIL, MEMBER_PASSWORD);

        given(spec)
                .filter(document("login",
                        requestFields(loginFieldDescriptor)))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/login")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("[공통] 로그인을 하여 얻은 accessToken으로 사용자의 정보를 받아온다.")
    void tokenLoginAndFindMemberInfo() {
        final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);

        final MemberResponse response = given(spec)
                .filter(document("login/check",
                        requestCookies(tokenCookieDescriptor),
                        responseFields(memberFieldDescriptor)))
                .cookie("token", accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .get("/login/check")
                .then()
                .statusCode(200)
                .extract().as(MemberResponse.class);

        assertThat(response.name()).isEqualTo(MEMBER_CAT_NAME);
    }

    @Test
    @DisplayName("[공통] 로그아웃에 성공하면 200을 응답한다.")
    void respondOkWhenLogout() {
        final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);

        given(spec)
                .filter(document("logout",
                        requestCookies(tokenCookieDescriptor)))
                .cookie("token", accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/logout")
                .then()
                .statusCode(200);
    }
}
