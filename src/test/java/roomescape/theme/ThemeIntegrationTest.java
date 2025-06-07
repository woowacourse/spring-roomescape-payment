package roomescape.theme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.common.dto.response.ErrorResponse;
import roomescape.theme.dto.response.ThemeResponse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ThemeIntegrationTest {

    @DisplayName("테마를 조회할 수 있다.")
    @Test
    void theme_view() {
        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(OK.value())
                .body("size()", is(3));
    }

    @DisplayName("테마를 추가 및 삭제 할 수 있다.")
    @Test
    void reservation_time_post_to_add() {
        Map<String, String> params = new HashMap<>();
        params.put("name", "테스트1");
        params.put("description", "테스트2");
        params.put("thumbnail", "테스트3");

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(CREATED.value())
                .extract()
                .response();

        ThemeResponse expected = new ThemeResponse(4L, "테스트1", "테스트2", "테스트3");
        ThemeResponse actual = response.as(ThemeResponse.class);
        assertThat(actual).isEqualTo(expected);

        RestAssured.given().log().all()
                .when().delete("/themes/4")
                .then().log().all()
                .statusCode(NO_CONTENT.value());
    }

    @DisplayName("테마 이름이 null 또는 빈 상태로 생성 요청 시 400 응답을 준다.")
    @ParameterizedTest
    @NullAndEmptySource
    void when_given_null_and_empty_theme_name(final String name) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("description", "hi");
        params.put("thumbnail", "http");

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract()
                .response();

        ErrorResponse actual = response.as(ErrorResponse.class);
        ErrorResponse expected = new ErrorResponse(actual.timestamp(), BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(), "[ERROR] 요청 본문 형식이 올바르지 않습니다.", "/themes");

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("테마 설명이 null 또는 빈 상태로 생성 요청 시 400 응답을 준다.")
    @Test
    void when_given_null_and_empty_theme_description() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "제목");
        params.put("description", null);
        params.put("thumbnail", "http");

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract()
                .response();

        ErrorResponse actual = response.as(ErrorResponse.class);
        ErrorResponse expected = new ErrorResponse(actual.timestamp(), BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(), "[ERROR] 요청 본문 형식이 올바르지 않습니다.", "/themes");

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("테마 썸네일이 null 또는 빈 상태로 생성 요청 시 400 응답을 준다.")
    @Test
    void when_given_null_and_empty_theme_thumbnail() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "제목");
        params.put("description", "hi");
        params.put("thumbnail", null);

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract()
                .response();

        ErrorResponse actual = response.as(ErrorResponse.class);
        ErrorResponse expected = new ErrorResponse(actual.timestamp(), BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(), "[ERROR] 요청 본문 형식이 올바르지 않습니다.", "/themes");

        assertThat(actual).isEqualTo(expected);
    }
}
