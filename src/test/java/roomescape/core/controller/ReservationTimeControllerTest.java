package roomescape.core.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.utils.RestDocumentGenerator.reservationTimeFieldDescriptors;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import roomescape.utils.AdminGenerator;
import roomescape.utils.DatabaseCleaner;
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

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();

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

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("times/get/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("전체 시간 목록"))
                                .andWithPrefix("[].", reservationTimeFieldDescriptors())))
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }

    @Test
    @DisplayName("날짜와 테마 정보가 주어지면 예약 가능한 시간 목록을 조회한다.")
    void findBookable() {
        testFixture.persistReservationTimeAfterMinute(2);

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("times/with-book-status/get/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        queryParameters(
                                parameterWithName("date").description("날짜"),
                                parameterWithName("theme").description("테마 id")),
                        responseFields(
                                fieldWithPath("[]").description("날짜, 테마에 따른 예약 정보가 포함된 시간 목록"))
                                .andWithPrefix("[].",
                                        fieldWithPath("id").description("시간 id"),
                                        fieldWithPath("startAt").description("방탈출 시작 시간"),
                                        fieldWithPath("alreadyBooked").description("예약되었는지 여부")
                                )))
                .when().get("/times?date=" + TOMORROW + "&theme=1")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }
}
