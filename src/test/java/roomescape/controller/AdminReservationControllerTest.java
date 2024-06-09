package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.IntegrationTestSupport;

import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

class AdminReservationControllerTest extends IntegrationTestSupport {

    private RequestSpecification specification;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.specification = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("예약 내역을 필터링하여 조회한다.")
    @Test
    void findReservationByFilter() {
        Map<String, String> params = Map.of("themeId", "1",
                "memberId", "1",
                "dateFrom", "2000-01-01",
                "dateTo", "9999-09-09"
        );

        RestAssured.given(specification).log().all()
                .filter(document("reservation-filter"))
                .accept(ContentType.JSON)
                .cookies("token", ADMIN_TOKEN)
                .queryParams(params)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));
    }

    @DisplayName("예약 대기 목록을 조회한다.")
    @Test
    void findAllWaiting() {
        RestAssured.given(specification).log().all()
                .filter(document("waiting-show"))
                .accept(ContentType.JSON)
                .cookies("token", ADMIN_TOKEN)
                .when().get("/admin/reservations/waiting")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3));
    }
}
