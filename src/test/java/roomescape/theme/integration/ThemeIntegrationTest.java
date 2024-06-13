package roomescape.theme.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import roomescape.auth.domain.Token;
import roomescape.auth.provider.CookieProvider;
import roomescape.model.IntegrationTest;
import roomescape.theme.dto.ThemeRequest;

import static org.hamcrest.Matchers.is;

class ThemeIntegrationTest extends IntegrationTest {

    @Test
    @DisplayName("요청한 테마를 정상적으로 등록, 확인, 삭제한다.")
    void themePageWork() {
        ThemeRequest themeRequest = new ThemeRequest("포레스트", "공포 테마", "thumbnail", 15000L);
        Token token = tokenProvider.getAccessToken(1);
        ResponseCookie cookie = CookieProvider.setCookieFrom(token);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(cookie.toString())
                .body(themeRequest)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));

        RestAssured.given().log().all()
                .cookie(cookie.toString())
                .when().delete("/admin/themes/3")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3));
    }
}
