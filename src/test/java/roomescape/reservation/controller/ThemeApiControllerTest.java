package roomescape.reservation.controller;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static roomescape.util.RestDocsFilter.CREATE_THEME;
import static roomescape.util.RestDocsFilter.DELETE_THEMES;
import static roomescape.util.RestDocsFilter.GET_ENTIRE_THEMES;
import static roomescape.util.RestDocsFilter.GET_POPULAR_THEMES;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.config.IntegrationTest;
import roomescape.reservation.dto.ThemeSaveRequest;
import roomescape.util.CookieUtils;

class ThemeApiControllerTest extends IntegrationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("인기 테마 목록 조회를 성공하면 200 응답을 받는다.")
    @Test
    void findTopTenThemesOfLastWeek() {
        saveThemeAsHorror();
        saveReservationTimeAsTen();
        saveMemberAsKaki();
        saveSuccessReservationAsDateNow();

        RestAssured.given(spec).log().all()
                .filter(GET_POPULAR_THEMES.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/themes/popular")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("responses", hasSize(1));
    }

    @DisplayName("테마 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAll() {
        saveThemeAsHorror();

        RestAssured.given(spec).log().all()
                .filter(GET_ENTIRE_THEMES.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/themes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("responses", hasSize(1));
    }

    @DisplayName("테마를 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void save() throws JsonProcessingException {
        ThemeSaveRequest themeSaveRequest = new ThemeSaveRequest("공포", "진짜 무서움", "https://i.pinimg.com/236x.jpg");

        RestAssured.given(spec).log().all()
                .filter(CREATE_THEME.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(themeSaveRequest))
                .accept(ContentType.JSON)
                .when()
                .post("/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", "/themes/1")
                .body("name", equalTo("공포"));
    }

    @DisplayName("테마를 성공적으로 제거하면 204 응답을 받는다.")
    @Test
    void delete() {
        RestAssured.given(spec).log().all()
                .filter(DELETE_THEMES.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .delete("/themes/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
