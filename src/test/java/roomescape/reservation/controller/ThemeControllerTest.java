package roomescape.reservation.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.RestClientControllerTest;
import roomescape.auth.token.TokenProvider;
import roomescape.member.model.MemberRole;

class ThemeControllerTest extends RestClientControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("전체 테마 정보를 조회한다.")
    @Test
    void getThemesTest() {
        RestAssured.given(spec).log().all()
                .filter(document("findAll-themes"))
                .cookie("token", createUserAccessToken())
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(15));
    }

    @DisplayName("인기 테마를 조회한다.")
    @Test
    void getPopularThemes() {
        RestAssured.given(spec).log().all()
                .filter(document("findAll-popular-themes"))
                .cookie("token", createUserAccessToken())
                .when().get("/popular-themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(7));
    }

    private String createUserAccessToken() {
        return tokenProvider.createToken(3L, MemberRole.USER);
    }
}
