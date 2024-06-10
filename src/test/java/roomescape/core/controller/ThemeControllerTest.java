package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.utils.RestDocumentGenerator.themeFieldDescriptors;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import roomescape.utils.DatabaseCleaner;
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

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();

        databaseCleaner.executeTruncate();

        testFixture.persistAdmin();
        testFixture.persistTheme("테마 1");
        testFixture.persistTheme("테마 2");
    }

    @Test
    @DisplayName("모든 테마 목록을 조회한다.")
    void findAllThemes() {
        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("themes/show-all-themes/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("전체 테마 목록"))
                                .andWithPrefix("[].", themeFieldDescriptors())))
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    @DisplayName("지난 한 주 동안의 인기 테마 목록을 조회한다.")
    void findPopularThemes() {
        createReservationTimes();
        createReservations();

        RestAssured.given(spec).log().all()
                .filter(document("themes/show-all-themes-in-popular-order/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("한 주 동안 예약순으로 정렬된 상위 10개 테마 목록"))
                                .andWithPrefix("[].", themeFieldDescriptors())))
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
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
