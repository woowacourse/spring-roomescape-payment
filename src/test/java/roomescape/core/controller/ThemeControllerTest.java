package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.DocumentHelper;
import roomescape.utils.TestFixture;

@AcceptanceTest
class ThemeControllerTest {
    private static final String TODAY = TestFixture.getTodayDate();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private TestFixture testFixture;

    private RequestSpecification specification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        specification = DocumentHelper.specification(restDocumentation);

        databaseCleaner.executeTruncate();

        testFixture.persistAdmin();
        testFixture.persistTheme("테마 1");
        testFixture.persistTheme("테마 2");
    }

    @Test
    @DisplayName("모든 테마 목록을 조회한다.")
    void findAllThemes() {
        RestAssured.given(this.specification).log().all()
                .accept("application/json")
                .filter(document("theme", responseFields(
                        fieldWithPath("[].id").description("테마 ID"),
                        fieldWithPath("[].name").description("테마 이름"),
                        fieldWithPath("[].description").description("테마 설명"),
                        fieldWithPath("[].thumbnail").description("테마 이미지"))))
                .when().get("/themes")
                .then().assertThat()
                .statusCode(is(200))
                .body("size()", is(2));
    }

    @Test
    @DisplayName("지난 한 주 동안의 인기 테마 목록을 조회한다.")
    void findPopularThemes() {
        createReservationTimes();
        createReservations();

        RestAssured.given(this.specification).log().all()
                .accept("application/json")
                .filter(document("theme-popular", responseFields(
                        fieldWithPath("[].id").description("테마 ID"),
                        fieldWithPath("[].name").description("테마 이름"),
                        fieldWithPath("[].description").description("테마 설명"),
                        fieldWithPath("[].thumbnail").description("테마 이미지"))))
                .when().get("/themes/popular")
                .then().assertThat()
                .statusCode(is(200))
                .body("size()", is(2))
                .body("name", is(List.of("테마 2", "테마 1")));
    }

    private void createReservationTimes() {
        testFixture.persistReservationTimeAfterMinute(1);
        testFixture.persistReservationTimeAfterMinute(2);
    }

    private void createReservations() {
        testFixture.persistReservationWithDateAndTimeAndTheme(TODAY, 1L, 2L);
        testFixture.persistReservationWithDateAndTimeAndTheme(TODAY, 2L, 2L);
        testFixture.persistReservationWithDateAndTimeAndTheme(TODAY, 1L, 1L);
    }
}
