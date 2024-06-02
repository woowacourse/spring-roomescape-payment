package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.fixture.ThemeFixture;
import roomescape.service.theme.dto.ThemeRequest;

import static org.hamcrest.Matchers.is;

class AdminThemeAcceptanceTest extends AcceptanceTest {
    @DisplayName("테마 삭제 성공 테스트")
    @Test
    void deleteTheme() {
        //given
        long themeId = (int) RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON).body(ThemeFixture.createThemeRequest())
                .when().post("/admin/themes")
                .then().extract().response().jsonPath().get("id");

        //when & then
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().delete("/admin/themes/" + themeId)
                .then().log().all().statusCode(204);
    }

    @DisplayName("일반 사용자는 테마를 삭제할 수 없다.")
    @Test
    void cannotDeleteThemeByGuest() {
        //given
        long themeId = (int) RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON).body(ThemeFixture.createThemeRequest())
                .when().post("/admin/themes")
                .then().extract().response().jsonPath().get("id");

        //when & then
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .when().delete("/admin/themes/" + themeId)
                .then().log().all()
                .assertThat().statusCode(403).body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));
    }

    @DisplayName("관리자가 테마를 추가한다.")
    @Test
    void createThemeByAdmin() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON).body(ThemeFixture.createThemeRequest())
                .when().post("/admin/themes")
                .then().log().all().statusCode(201);
    }

    @DisplayName("일반 사용자는 테마를 추가할 수 없다.")
    @Test
    void createThemeByMember() {
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .contentType(ContentType.JSON).body(ThemeFixture.createThemeRequest())
                .when().post("/admin/themes")
                .then().log().all()
                .assertThat().statusCode(403)
                .body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));
    }

    @DisplayName("올바르지 않은 형식으로 테마를 생성하 수 없다.")
    @Test
    void cannotCreateThemeByThumbnail() {
        //given
        ThemeRequest themeRequest = new ThemeRequest("레벨2 탈출", "우테코 레벨2를 탈출하는 내용입니다.", "//i.pinimg.com/236x/6e/bc/4");

        //when&then
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON).body(themeRequest)
                .when().post("/admin/themes")
                .then().log().all().statusCode(400)
                .body("message", is("올바르지 않은 썸네일 형식입니다."));
    }
}
