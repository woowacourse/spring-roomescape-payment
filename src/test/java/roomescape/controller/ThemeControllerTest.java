package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;
import roomescape.web.controller.request.ThemeRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static roomescape.controller.doc.DocumentFilter.GET_THEMES;


class ThemeControllerTest extends ControllerTest {

    @BeforeEach
    void setInitialData() {
        jdbcTemplate.update("INSERT INTO theme(name, description, thumbnail, price) VALUES (?, ?, ?, ?)", "방탈출1", "설명1",
                "https://url1", 10000L);
        jdbcTemplate.update("INSERT INTO theme(name, description, thumbnail, price) VALUES (?, ?, ?, ?)", "방탈출2", "설명2",
                "https://url2", 1000L);
    }

    @DisplayName("테마를 생성한다 -> 201")
    @Test
    void create() {
        ThemeRequest request = new ThemeRequest("방탈출", "대충 설명", "https://url.jpg", 2000L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .body("id", is(3));
    }

    @DisplayName("테마를 삭제한다 -> 204")
    @Test
    void delete() {
        RestAssured.given().log().all()
                .when().delete("/themes/1")
                .then().log().all()
                .statusCode(204);

        Long count = jdbcTemplate.queryForObject("SELECT COUNT(id) FROM theme", Long.class);

        assertThat(count).isEqualTo(1L);
    }

    @DisplayName("테마를 조회한다 -> 200")
    @Test
    void findAll() {
        RestAssured.given(spec).log().all()
                .filter(GET_THEMES.getValue())
                .contentType(ContentType.JSON)
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @DisplayName("테마 정보 포맷이 잘못될 경우 -> 400")
    @Test
    void create_IllegalTheme() {
        ThemeRequest request = new ThemeRequest("방탈출3", "설명3", "ftp://url3", 7000L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("중복된 데이터를 추가한다 -> 400")
    @Test
    void create_Duplicate() {
        ThemeRequest request = new ThemeRequest("방탈출1", "설명1", "https://url1", 5000L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("요청이 잘못된 형식일 경우 -> 400")
    @Test
    void create_MethodArgNotValid() {
        ThemeRequest request = new ThemeRequest("", null, "https://url1", 6000L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("예약이 존재한 상태에서 테마를 삭제한다 -> 400")
    @Test
    void delete_ReservationExists() {
        jdbcTemplate.update("INSERT INTO reservation_time(start_at) VALUES (?)", "12:00");
        jdbcTemplate.update("INSERT INTO member(name,email,password,role) VALUES (?,?,?,?)", "wiib", "asd@naver.com",
                "123asd", "ADMIN");
        jdbcTemplate.update("INSERT INTO reservation(date,time_id,theme_id,member_id) VALUES (?,?,?,?)",
                "2026-02-01", 1L, 1L, 1L);

        RestAssured.given().log().all()
                .when().delete("/themes/1")
                .then().log().all()
                .statusCode(400);
    }

    @DisplayName("상위 10개 인기 테마를 조회 한다. -> 200")
    @Test
    @Sql({"/popularTestData.sql"})
    void findPopularTheme() {
        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(5));
    }
}
