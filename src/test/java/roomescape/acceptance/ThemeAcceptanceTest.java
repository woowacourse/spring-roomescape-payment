package roomescape.acceptance;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ThemeAcceptanceTest extends AcceptanceTest {
    @DisplayName("모든 테마를 조회할 수 있다.")
    @Test
    void findAll() {
        RestAssured.given().log().all()
                .when().get("/themes")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("인기 테마 목록을 조회할 수 있다.")
    @Test
    void findPopularThemes() {
        //when&then
        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.OK.value());
    }
}
