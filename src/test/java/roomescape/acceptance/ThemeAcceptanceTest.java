package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import roomescape.BasicAcceptanceTest;
import roomescape.dto.request.theme.ThemeRequest;
import roomescape.dto.response.theme.ThemeResponse;

class ThemeAcceptanceTest extends BasicAcceptanceTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private String adminToken;

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
        jdbcTemplate.update(
                "INSERT INTO member (name, email, password, role) VALUES ('운영자', 'admin@wooteco.com', 'wootecoCrew6!', 'ADMIN')");
        adminToken = LoginTokenProvider.login("admin@wooteco.com", "wootecoCrew6!", 200);
    }

    @TestFactory
    @DisplayName("2개의 테마를 추가한다")
    Stream<DynamicTest> themePostTest() {
        return Stream.of(
                dynamicTest("테마를 추가한다", () -> postTheme(adminToken, 201)),
                dynamicTest("테마를 추가한다", () -> postTheme(adminToken, 201)),
                dynamicTest("모든 테마를 조회한다 (총 2개)", () -> getThemes(200, 2))
        );
    }

    @TestFactory
    @DisplayName("테마를 추가하고 삭제한다")
    Stream<DynamicTest> themePostAndDeleteTest() {
        AtomicLong themeId = new AtomicLong();

        return Stream.of(
                dynamicTest("테마를 추가한다", () -> themeId.set(postTheme(adminToken, 201))),
                dynamicTest("테마를 삭제한다", () -> deleteTheme(adminToken, themeId.longValue(), 204)),
                dynamicTest("모든 테마를 조회한다 (총 0개)", () -> getThemes(200, 0))
        );
    }

    @DisplayName("예약 횟수 상위 10개의 테마를 조회한다.")
    @Sql(value = {"classpath:setting-big-reservation.sql"})
    @Test
    void top10Theme() {
        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(10));
    }

    private Long postTheme(String token, int expectedHttpCode) {
        ThemeRequest themeRequest = new ThemeRequest("테마", "설명서", "썸네일", 1000);

        Response response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies("token", token)
                .body(themeRequest)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(expectedHttpCode)
                .extract().response();

        if (expectedHttpCode == 201) {
            return response.jsonPath().getLong("id");
        }

        return null;
    }

    private void getThemes(int expectedHttpCode, int expectedthemesSize) {
        Response response = RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .statusCode(expectedHttpCode)
                .extract().response();

        List<?> themeResponses = response.as(List.class);

        assertThat(themeResponses).hasSize(expectedthemesSize);
    }

    private void deleteTheme(String token, Long themeId, int expectedHttpCode) {
        RestAssured.given().log().all()
                .cookies("token", token)
                .when().delete("/admin/themes/" + themeId)
                .then().log().all()
                .statusCode(expectedHttpCode);
    }
}
