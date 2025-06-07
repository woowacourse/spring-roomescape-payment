package roomescape.api.auth;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
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
import roomescape.api.fixture.DocumentationFixture;
import roomescape.auth.dto.LoginCheckResponse;
import roomescape.auth.dto.LoginRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/test-member-data.sql")
@ExtendWith(RestDocumentationExtension.class)
public class AuthApiTest {

    @LocalServerPort
    private int port;

    private RequestSpecification documentationSpecification;

    public static final String TOKEN_COOKIE_NAME = "token";

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        this.documentationSpecification = DocumentationFixture
                .createDefaultDocumentationSpecification(restDocumentation);
    }

    @DisplayName("POST /login : 로그인 API 테스트")
    @Test
    void login() {
        // given
        LoginRequest request = new LoginRequest("aaa@gmail.com", "1234");
        // when
        String actualCookie = RestAssured.given(documentationSpecification).log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .filter(AuthDocumentationFixture.LOGIN_DOCUMENT)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .header("Set-Cookie");
        // then
        assertThat(actualCookie)
                .isNotNull()
                .contains("token=")
                .contains("HttpOnly");
    }

    @DisplayName("POST /logout : 로그아웃 API 테스트")
    @Test
    void logout() {
        // given
        String loginToken = getLoginUserToken();
        // when
        String actualToken = RestAssured.given(documentationSpecification).log().all()
                .cookie(TOKEN_COOKIE_NAME, loginToken)
                .filter(AuthDocumentationFixture.LOGOUT_DOCUMENTATION)
                .when().post("/logout")
                .then().log().all()
                .statusCode(204)
                .extract().cookie(TOKEN_COOKIE_NAME);
        // then
        assertThat(actualToken).isBlank();
    }

    @DisplayName("GET /login/check : 인증 정보 조회 API 테스트")
    @Test
    void loginCheck() {
        // given
        String loginToken = getLoginUserToken();
        LoginCheckResponse expectedResponse = new LoginCheckResponse("사용자1");
        // when
        LoginCheckResponse actualResponse = RestAssured.given(documentationSpecification).log().all()
                .cookie(TOKEN_COOKIE_NAME, loginToken)
                .filter(AuthDocumentationFixture.LOGIN_CHECK_DOCUMENT)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract().as(LoginCheckResponse.class);
        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    private String getLoginUserToken() {
        LoginRequest request = new LoginRequest("aaa@gmail.com", "1234");
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().log().all()
                .extract().cookie(TOKEN_COOKIE_NAME);
    }
}
