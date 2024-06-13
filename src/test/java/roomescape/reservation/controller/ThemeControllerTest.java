package roomescape.reservation.controller;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.common.config.IntegrationTest;
import roomescape.common.util.CookieUtils;
import roomescape.reservation.controller.dto.request.ThemeSaveRequest;

class ThemeControllerTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("인기 테마 목록 조회를 성공하면 200 응답을 받는다.")
    @Test
    void findTopTenThemesOfLastWeek() {
        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document("themes/popular"))
                .when()
                .get("/themes/popular")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("테마 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAll() {
        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document("themes/findAll"))
                .when()
                .get("/themes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("테마를 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void save() throws JsonProcessingException {
        ThemeSaveRequest themeSaveRequest = new ThemeSaveRequest("공포", "진짜 무서움", "https://i.pinimg.com/236x.jpg");

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(themeSaveRequest))
                .accept(ContentType.JSON)
                .filter(document("themes/save"))
                .when()
                .post("/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/themes/1");
    }

    @DisplayName("테마 이름이 중복되면 저장에 실패한다.")
    @Test
    void failSaveWhenAlreadyHasSameThemeName() throws JsonProcessingException {
        saveThemeAsHorror();

        ThemeSaveRequest themeSaveRequest = new ThemeSaveRequest("공포", "진짜 무서움", "https://i.pinimg.com/236x.jpg");

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(themeSaveRequest))
                .accept(ContentType.JSON)
                .filter(document("themes/save/fail/duplicated"))
                .when()
                .post("/themes")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("테마를 성공적으로 제거하면 204 응답을 받는다.")
    @Test
    void delete() {
        saveThemeAsHorror();
        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document("themes/delete"))
                .when()
                .delete("/themes/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("예약이 존재하는 테마인 경우 테마 삭제에 실패한다.")
    @Test
    void failDeleteWhenAlreadyReserved() {
        saveThemeAsHorror();
        saveMemberAsKaki();
        saveReservationTimeAsTen();
        saveSuccessReservationAsDateNow();

        RestAssured.given(this.spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .filter(document("themes/delete/fail/already-reserved"))
                .when()
                .delete("/themes/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
