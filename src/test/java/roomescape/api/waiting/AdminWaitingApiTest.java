package roomescape.api.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import roomescape.api.fixture.DocumentationFixture;
import roomescape.auth.dto.LoginRequest;
import roomescape.waiting.dto.WaitingSimpleResponse;
import roomescape.waiting.repository.WaitingRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-time-data.sql", "/test-theme-data.sql", "/test-member-data.sql", "/test-waiting-data.sql"})
@ExtendWith(RestDocumentationExtension.class)
public class AdminWaitingApiTest {

    public static final String AUTH_COOKIE_NAME = "token";

    @LocalServerPort
    private int port;

    @Autowired
    private WaitingRepository waitingRepository;

    private RequestSpecification documentationSpecification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.documentationSpecification = DocumentationFixture
                .createDefaultDocumentationSpecification(restDocumentation);
    }

    @DisplayName("GET /admin/waitings : 어드민 예약 대기 목록 조회 API 테스트")
    @Test
    void findAll() {
        // given
        String adminToken = getAdminToken();
        WaitingSimpleResponse expectedResponse = new WaitingSimpleResponse(1L, "사용자1", "테마2",
                LocalDate.now().plusDays(1), LocalTime.of(11, 0));
        // when
        WaitingSimpleResponse[] actualResponse = RestAssured.given(documentationSpecification).log().all()
                .cookie(AUTH_COOKIE_NAME, adminToken)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().get("/admin/waitings")
                .then().log().all()
                .statusCode(200)
                .extract().as(WaitingSimpleResponse[].class);
        // then
        assertAll(
                () -> assertThat(actualResponse[0]).isEqualTo(expectedResponse),
                () -> assertThat(actualResponse).hasSize(2)
        );
    }

    @DisplayName("DELETE /admin/waitings/{id} : 어드민 예약 대기 거절 API 테스트")
    @Test
    void delete() {
        // given
        String adminToken = getAdminToken();
        long waitingIdPathParameter = 1L;
        // when
        RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .cookie(AUTH_COOKIE_NAME, adminToken)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().delete("/admin/waitings/{id}", waitingIdPathParameter)
                .then().log().all()
                .statusCode(204);
        // then
        assertThat(waitingRepository.count()).isEqualTo(1);
    }

    private String getAdminToken() {
        LoginRequest adminLoginRequest = new LoginRequest("admin@gmail.com", "1234");
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(adminLoginRequest)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract().cookie(AUTH_COOKIE_NAME);
    }
}
