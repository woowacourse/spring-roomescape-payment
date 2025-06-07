package roomescape.api.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
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
import roomescape.waiting.dto.CreateWaitingRequest;
import roomescape.waiting.dto.WaitingResponse;
import roomescape.waiting.repository.WaitingRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-time-data.sql", "/test-theme-data.sql", "/test-member-data.sql", "/test-waiting-data.sql"})
@ExtendWith(RestDocumentationExtension.class)
public class WaitingApiTest {

    public static final String AUTH_COOKIE_NAME = "token";

    private RequestSpecification documentationSpecification;

    @Autowired
    private WaitingRepository waitingRepository;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.documentationSpecification = DocumentationFixture
                .createDefaultDocumentationSpecification(restDocumentation);
    }

    @DisplayName("예약 대기 생성에 성공하면 201을 반환한다.")
    @Test
    void create() {
        // given
        String loginToken = getMember1Token();
        CreateWaitingRequest request = new CreateWaitingRequest(LocalDate.now().plusDays(1), 1L, 1L);
        // when
        WaitingResponse actualResponse = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .cookie(AUTH_COOKIE_NAME, loginToken)
                .body(request)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().post("/waitings")
                .then().log().all()
                .statusCode(201)
                .extract().as(WaitingResponse.class);
        // then
        assertAll(
                () -> assertThat(actualResponse.id()).isEqualTo(3L),
                () -> assertThat(actualResponse.reservation().id()).isEqualTo(1L),
                () -> assertThat(actualResponse.reservation().member().id()).isEqualTo(2L),
                () -> assertThat(actualResponse.member().id()).isEqualTo(1L)
        );
    }

    @DisplayName("내 예약 대기 취소에 성공하면 204를 반환한다.")
    @Test
    void delete() {
        // given
        String loginToken = getMember1Token();
        long waitingIdPathParameter = 1L;
        // when
        RestAssured.given(documentationSpecification).log().all()
                .cookie(AUTH_COOKIE_NAME, loginToken)
                .filter(DocumentationFixture.createDocumentWithDefaultPath())
                .when().delete("/waitings/{id}", waitingIdPathParameter)
                .then().log().all()
                .statusCode(204);
        // then
        assertThat(waitingRepository.count()).isEqualTo(1);
    }

    private String getMember1Token() {
        LoginRequest loginRequest = new LoginRequest("aaa@gmail.com", "1234");
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .extract().cookie(AUTH_COOKIE_NAME);
    }
}
