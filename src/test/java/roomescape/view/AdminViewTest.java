package roomescape.view;

import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.fixture.IntegrationFixture.ADMIN_EMAIL;
import static roomescape.fixture.IntegrationFixture.PASSWORD;
import static roomescape.fixture.IntegrationFixture.TOKEN;
import static roomescape.fixture.IntegrationFixture.loginAndGetAuthToken;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
@ExtendWith(RestDocumentationExtension.class)
class AdminViewTest {

    @LocalServerPort
    private int port;

    private RequestSpecification spec;
    private String authToken;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(provider))
                .build();
        authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
    }

    @Test
    void accessAdminMainPage() {
        RestAssured.given(spec).log().all()
                .filter(document("view-pages/어드민-메인-페이지"))
                .cookie(TOKEN, authToken)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void accessAdminReservationPage() {
        RestAssured.given(spec).log().all()
                .filter(document("view-pages/어드민-예약-관리-페이지"))
                .cookie(TOKEN, authToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void accessAdminTimePage() {
        RestAssured.given(spec).log().all()
                .filter(document("view-pages/어드민-시간-관리-페이지"))
                .cookie(TOKEN, authToken)
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void accessAdminThemePage() {
        RestAssured.given(spec).log().all()
                .filter(document("view-pages/어드민-테마-관리-페이지"))
                .cookie(TOKEN, authToken)
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void accessAdminWaitingReservationPage() {
        RestAssured.given(spec).log().all()
                .filter(document("view-pages/어드민-예약대기-관리-페이지"))
                .cookie(TOKEN, authToken)
                .when().get("/admin/waiting-reservation")
                .then().log().all()
                .statusCode(200);
    }
}
