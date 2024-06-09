package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.IntegrationTestSupport;

import java.util.Map;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

class ReservationTimeControllerTest extends IntegrationTestSupport {

    private RequestSpecification specification;

    String createdId;
    int timeSize;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.specification = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("예약 시간 목록 조회")
    void showReservationTime() {
        RestAssured.given(specification).log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(document("time-show"))
                .cookie("token", ADMIN_TOKEN)
                .when().get("/times")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 시간 추가")
    void saveReservationTime() {
        Map<String, String> param = Map.of("startAt", "12:12");
        RestAssured.given(specification).log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(document("time-save"))
                .cookie("token", ADMIN_TOKEN)
                .body(param)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("예약 시간 삭제")
    void deleteReservationTime() {
        Map<String, String> param = Map.of("startAt", "13:13");
        String createdId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", ADMIN_TOKEN)
                .body(param)
                .when().post("/admin/times")
                .then().log().all()
                .statusCode(201).extract().header("location").split("/")[2];

        RestAssured.given(specification).log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(document("time-delete"))
                .cookie("token", ADMIN_TOKEN)
                .when().delete("/admin/times/" + createdId)
                .then().log().all()
                .statusCode(204);
    }

    @DisplayName("예약 시간 CRUD")
    @TestFactory
    Stream<DynamicTest> dynamicUserTestsFromCollection() {
        return Stream.of(
                dynamicTest("예약 시간 목록을 조회한다.", () -> {
                    timeSize = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/times")
                            .then().log().all()
                            .statusCode(200).extract()
                            .response().jsonPath().getList("$").size();
                }),
                dynamicTest("예약 시간을 추가한다.", () -> {
                    Map<String, String> param = Map.of("startAt", "12:12");

                    createdId = RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(param)
                            .when().post("/admin/times")
                            .then().log().all()
                            .statusCode(201).extract().header("location").split("/")[2];
                }),
                dynamicTest("예약 시간 목록 개수가 1증가한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/times")
                            .then().log().all()
                            .statusCode(200).body("size()", is(timeSize + 1));
                }),
                dynamicTest("유효하지 않은 형식으로 시간을 추가할 수 없다.", () -> {
                    Map<String, String> param = Map.of("startAt", "12:12:12");

                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .body(param)
                            .when().post("/admin/times")
                            .then().log().all()
                            .statusCode(400);
                }),
                dynamicTest("시간을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().delete("/admin/times/" + createdId)
                            .then().log().all()
                            .statusCode(204);
                }),
                dynamicTest("예약 시간 목록 개수가 1감소한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", ADMIN_TOKEN)
                            .when().get("/times")
                            .then().log().all()
                            .statusCode(200).body("size()", is(timeSize));
                })
        );
    }
}
