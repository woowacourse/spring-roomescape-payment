package roomescape.time;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/test-time-data.sql")
@ExtendWith(RestDocumentationExtension.class)
public class ReservationTimeApiTest {

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withRequestDefaults(modifyHeaders().remove("Foo"), prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @DisplayName("시간 조회를 성공할 경우 200을 반환한다.")
    @Test
    void testFindAll() {
        RestAssured.given(spec).log().all()
                .filter(document(
                        "get-times",
                        responseFields(
                                fieldWithPath("[].id").description("시간 ID"),
                                fieldWithPath("[].startAt").description("시작 시간")
                        )
                ))
                .when().get("/times")
                .then()
                .statusCode(200)
                .body("size()", Matchers.is(3));
    }

    @DisplayName("시간 생성에 성공할 경우 201을 반환한다.")
    @Test
    void testCreate() {
        String requestBody = """
                {
                    "startAt": "20:00"
                }
                """;

        RestAssured.given(spec).log().all()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .filter(document(
                        "create-time",
                        requestFields(
                                fieldWithPath("startAt").description("시작 시간")
                        ),
                        responseFields(
                                fieldWithPath("id").description("시간 ID"),
                                fieldWithPath("startAt").description("시작 시간")
                        )
                ))
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .body("startAt", Matchers.is("20:00"));
    }

    @DisplayName("시간별 예약 가능 여부 조회를 성공할 경우 200을 반환한다.")
    @Test
    @Sql({"/test-reservation-availability-data.sql"})
    void testFindAllTimeAvailability() {
        RestAssured.given(spec).log().all()
                .filter(document(
                        "get-time-availability",
                        responseFields(
                                fieldWithPath("[].timeId").description("시간 ID"),
                                fieldWithPath("[].startAt").description("시작 시간"),
                                fieldWithPath("[].alreadyBooked").description("예약 존재 여부")
                        )
                ))
                .when().get("/times/availability?date=2024-03-20&themeId=1")
                .then().log().all()
                .statusCode(200)
                .body("size()", Matchers.is(3));
    }

    @DisplayName("시간 삭제에 성공할 경우 204를 반환한다.")
    @Test
    @Sql("/test-time-data.sql")
    void testDelete() {
        RestAssured.given(spec).log().all()
                .filter(document("delete-time"))
                .when().delete("/times/1")
                .then().log().all()
                .statusCode(204);
    }
}
