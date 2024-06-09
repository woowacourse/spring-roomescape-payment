package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.utils.AdminGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.DocumentHelper;
import roomescape.utils.TestFixture;

@AcceptanceTest
class ReservationTimeControllerTest {
    private static final String TODAY = TestFixture.getTodayDate();
    private static final String TOMORROW = TestFixture.getTomorrowDate();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private AdminGenerator adminGenerator;

    @Autowired
    private TestFixture testFixture;

    private RequestSpecification specification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        specification = DocumentHelper.specification(restDocumentation);

        databaseCleaner.executeTruncate();
        adminGenerator.generate();

        testFixture.persistTheme("테마 1");
        testFixture.persistReservationTimeAfterMinute(1);
        testFixture.persistReservationWithDateAndTimeAndTheme(TODAY, 1L, 1L);
    }

    @Test
    @DisplayName("전체 시간 목록을 조회한다.")
    void findAll() {
        testFixture.persistReservationTimeAfterMinute(2);

        RestAssured.given(this.specification).log().all()
                .accept("application/json")
                .filter(document("time", responseFields(
                        fieldWithPath("[].id").description("예약 시간 ID"),
                        fieldWithPath("[].startAt").description("예약 시간 값"))))
                .when().get("/times")
                .then().assertThat()
                .statusCode(is(200))
                .body("size()", is(2));
    }

    @Test
    @DisplayName("날짜와 테마 정보가 주어지면 예약 가능한 시간 목록을 조회한다.")
    void findBookable() {
        testFixture.persistReservationTimeAfterMinute(2);

        RestAssured.given(this.specification).log().all()
                .accept("application/json")
                .filter(document("time-bookable", queryParameters(
                                parameterWithName("date").description("예약하려는 날짜"),
                                parameterWithName("theme").description("예약하려는 테마 ID")),
                        responseFields(fieldWithPath("[].id").description("예약 시간 ID"),
                                fieldWithPath("[].startAt").description("예약 시간 값"),
                                fieldWithPath("[].alreadyBooked").description("예약 여부"))))
                .when().get("/times?date=" + TOMORROW + "&theme=1")
                .then().assertThat()
                .statusCode(is(200))
                .body("size()", is(2));
    }
}
