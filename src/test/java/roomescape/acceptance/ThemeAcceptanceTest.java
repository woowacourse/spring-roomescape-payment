package roomescape.acceptance;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlMergeMode;
import roomescape.fixture.TokenFixture;
import roomescape.service.theme.dto.ThemeRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@SqlMergeMode(SqlMergeMode.MergeMode.MERGE)
@Sql({"/truncate.sql", "/member.sql"})
class ThemeAcceptanceTest extends AcceptanceTest {

    @DisplayName("테마 조회 성공 테스트")
    @TestFactory
    Stream<DynamicTest> findAll() {
        ThemeRequest themeRequest = new ThemeRequest("레벨2 탈출", "우테코 레벨2를 탈출하는 내용입니다.",
            "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");
        return Stream.of(
            DynamicTest.dynamicTest("테마를 생성한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", TokenFixture.getAdminToken())
                    .contentType(ContentType.JSON).body(themeRequest)
                    .when().post("/admin/themes")
                    .then().extract().response().jsonPath().get("id");
            }),
            DynamicTest.dynamicTest("모든 테마를 조회한다.", () -> {
                RestAssured.given().log().all()
                    .when().get("/themes")
                    .then().log().all()
                    .assertThat().statusCode(200).body("size()", is(1));
            })
        );
    }

    @DisplayName("인기 테마 조회 성공 테스트")
    @Test
    @Sql({"/truncate.sql", "/insert-popular-theme.sql"})
    void findPopularThemes() {
        //when&then
        RestAssured.given().log().all()
            .when().get("/themes/popular")
            .then().log().all()
            .assertThat().statusCode(200).body("size()", is(4));
    }
}
