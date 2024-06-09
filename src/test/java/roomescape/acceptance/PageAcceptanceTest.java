package roomescape.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.FieldDescriptorFixture.tokenCookieDescriptor;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.MEMBER_CAT_EMAIL;

class PageAcceptanceTest extends AcceptanceTest {

    @ParameterizedTest
    @ValueSource(strings = {"/admin", "/admin/reservation", "/admin/time", "/admin/theme"})
    @DisplayName("관리자가 관리자 페이지에 접근하면 200을 응답한다.")
    void respondOkWhenAdminAccessAdminPage(final String adminPath) {
        final String accessToken = getAccessToken(ADMIN_EMAIL);

        given(spec)
                .filter(document("template/admin",
                        requestCookies(tokenCookieDescriptor)
                ))
                .cookie("token", accessToken)
                .when()
                .get(adminPath)
                .then()
                .statusCode(200)
                .body(containsString("<!DOCTYPE html>"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/admin", "/admin/reservation", "/admin/time", "/admin/theme"})
    @DisplayName("사용자가 관리자 페이지에 접근하면 403을 응답한다.")
    void respondForbiddenWhenMemberAccessAdminPage(final String adminPath) {
        final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);

        given(spec)
                .filter(document("template/admin",
                        requestCookies(tokenCookieDescriptor)
                ))
                .cookie("token", accessToken)
                .when()
                .get(adminPath)
                .then()
                .statusCode(403);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/reservation", "/login"})
    @DisplayName("사용자가 사용자 페이지에 접근하면 200을 응답한다.")
    void respondOkWhenMemberAccessMemberPage(String path) {
        given(spec)
                .when()
                .get(path)
                .then()
                .statusCode(200)
                .body(containsString("<!DOCTYPE html>"));
    }
}
