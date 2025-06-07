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
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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

    @DisplayName("테마 생성 시 비어있는 입력값이 있으면 400 에러가 발생한다.")
    @ParameterizedTest(name = "[{index}] {0} 필드가 비어있을 때")
    @MethodSource("invalidThemeParams")
    void when_given_invalid_theme_params(String field, String name, String description, String thumbnail) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("description", description);
        params.put("thumbnail", thumbnail);

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/themes")
                .then().log().all()
                .statusCode(BAD_REQUEST.value())
                .extract()
                .response();

        ErrorResponse actual = response.as(ErrorResponse.class);
        ErrorResponse expected = new ErrorResponse(
                actual.timestamp(),
                BAD_REQUEST.value(),
                BAD_REQUEST.getReasonPhrase(),
                "[ERROR] 테마 정보는 비어있을 수 없습니다.",
                "/themes"
        );

        assertThat(actual).isEqualTo(expected);
    }

    private static Stream<Arguments> invalidThemeParams() {
        return Stream.of(
                Arguments.of("name", "", "설명", "http"),
                Arguments.of("name", " ", "설명", "http"),
                Arguments.of("description", "제목", "", "http"),
                Arguments.of("description", "제목", " ", "http"),
                Arguments.of("thumbnail", "제목", "설명", "")
        );
    }
}
