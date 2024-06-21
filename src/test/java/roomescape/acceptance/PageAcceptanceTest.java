package roomescape.acceptance;

import static roomescape.fixture.MemberFixture.memberFixture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.restassured.RestAssured;

class PageAcceptanceTest extends AcceptanceTest {

    @ParameterizedTest
    @ValueSource(strings = {"/admin", "/admin/reservation", "/admin/time", "/admin/theme", "admin/waiting"})
    @DisplayName("관리자가 관리자 페이지에 접근하면 200을 응답한다.")
    void respondOkWhenAdminAccessAdminPage(final String path) {
        var admin = saveMember(memberFixture(1L));

        RestAssured.given().log().all()
                .cookie("token", accessToken(admin.getId()))
                .when().get(path)
                .then().log().all()
                .statusCode(200);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/admin", "/admin/reservation", "/admin/time", "/admin/theme", "admin/waiting"})
    @DisplayName("사용자가 관리자 페이지에 접근하면 403을 응답한다.")
    void respondForbiddenWhenMemberAccessAdminPage(final String path) {
        var member = saveMember(memberFixture(2L));

        RestAssured.given().log().all()
                .cookie("token", accessToken(member.getId()))
                .when().get(path)
                .then().log().all()
                .statusCode(403);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/", "/reservation", "/login", "/reservation-mine"})
    @DisplayName("사용자가 사용자 페이지에 접근하면 200을 응답한다.")
    void respondOkWhenMemberAccessMemberPage(String path) {
        var member = saveMember(memberFixture(2L));
        RestAssured.given().log().all()
                .cookie("token", accessToken(member.getId()))
                .when().get(path)
                .then().log().all()
                .statusCode(200);
    }
}
