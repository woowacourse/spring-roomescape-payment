package roomescape.core.controller;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.utils.RestDocumentGenerator.documentWithTokenDescription;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.utils.AccessTokenGenerator;

@AcceptanceTest
class AdminViewControllerTest {
    @LocalServerPort
    private int port;

    private String accessToken;
    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();
        accessToken = AccessTokenGenerator.adminTokenGenerate();
    }

    @Test
    @DisplayName("관리자 페이지로 이동한다.")
    void moveToAdminPage() {
        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(documentWithTokenDescription("page/admin/", "어드민 토큰"))
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 관리 페이지로 이동한다.")
    void moveToReservationManagePage() {
        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(documentWithTokenDescription("page/admin/reservation/", "어드민 토큰"))
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("시간 관리 페이지로 이동한다.")
    void moveToTimeManagePage() {
        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(documentWithTokenDescription("page/admin/time/", "어드민 토큰"))
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("테마 관리 페이지로 이동한다.")
    void moveToThemeManagePage() {
        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(documentWithTokenDescription("page/admin/theme/", "어드민 토큰"))
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 대기 관리 페이지로 이동한다.")
    void moveToWaitingManagePage() {
        RestAssured.given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(documentWithTokenDescription("page/admin/waiting/", "어드민 토큰"))
                .when().get("/admin/waiting")
                .then().log().all()
                .statusCode(200);
    }
}
