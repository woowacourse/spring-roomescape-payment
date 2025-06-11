package roomescape.auth;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
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
import org.junit.jupiter.api.Nested;
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
import roomescape.auth.dto.LoginRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql("/test-member-data.sql")
@ExtendWith(RestDocumentationExtension.class)
public class AuthApiTest {

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

    public static final String TOKEN_COOKIE_NAME = "token";

    @DisplayName("로그인 테스트")
    @Nested
    class LoginTest {

        @DisplayName("올바른 이메일과 비밀번호를 입력하면 200을 반환한다")
        @Test
        void testLogin() {
            RestAssured.given(spec).log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .filter(document(
                            "login",
                            requestFields(
                                    fieldWithPath("email").description("이메일"),
                                    fieldWithPath("password").description("패스워드")
                            ))
                    )
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(200)
                    .header("Set-Cookie", Matchers.notNullValue());
        }

        @DisplayName("이메일 또는 비밀번호 정보가 올바르지 않으면 401을 반환한다.")
        @Test
        void testInvalidEmailOrPassword() {
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("ddd@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(401);

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1111"))
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("이메일 형식이 올바르지 않으면 400을 반환한다.")
        @Test
        void testInvalidEmailFormat() {
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("ddd", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(400);
        }
    }

    @DisplayName("인증 정보 조회 API 테스트")
    @Nested
    class LoginCheckTest {

        @DisplayName("인증 정보 조회를 성공할 경우 200을 반환한다.")
        @Test
        void testLoginCheck() {
            // given
            String token = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .statusCode(200)
                    .extract().cookie(TOKEN_COOKIE_NAME);
            // when
            // then
            RestAssured.given(spec).log().all()
                    .cookie(TOKEN_COOKIE_NAME, token)
                    .filter(document(
                            "login/check",
                            requestCookies(
                                    cookieWithName(TOKEN_COOKIE_NAME).description("인증 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("name").description("유저 이름")
                            ))
                    )
                    .when().get("/login/check")
                    .then().log().all()
                    .statusCode(200)
                    .body("name", Matchers.equalTo("사용자1"));
        }

        @DisplayName("쿠키를 찾을 수 없는 경우 401을 반환한다.")
        @Test
        void testCookieAbsence() {
            RestAssured.given().log().all()
                    .when().get("/login/check")
                    .then().log().all()
                    .statusCode(401);
            RestAssured.given().log().all()
                    .cookie("invalidName", TOKEN_COOKIE_NAME)
                    .when().get("/login/check")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("쿠키의 정보가 올바르지 않을 경우 401을 반환한다.")
        @Test
        void testInvalidCookie() {
            RestAssured.given().log().all()
                    .cookie(TOKEN_COOKIE_NAME, "3L")
                    .when().get("/login/check")
                    .then().log().all()
                    .statusCode(401);
        }
    }

    @DisplayName("로그아웃 테스트")
    @Nested
    class LogoutTest {

        @DisplayName("로그아웃에 성공할 경우 204를 반환한다.")
        @Test
        void testLogout() {
            // given
            String token = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie(TOKEN_COOKIE_NAME);
            // when
            // then
            RestAssured.given(spec).log().all()
                    .cookie(TOKEN_COOKIE_NAME, token)
                    .filter(document(
                            "logout",
                            requestCookies(
                                    cookieWithName(TOKEN_COOKIE_NAME).description("인증 토큰")
                            ),
                            responseHeaders(
                                    headerWithName("Set-Cookie").description("쿠키 삭제를 위한 빈 값")
                            ))
                    )
                    .when().post("/logout")
                    .then().log().all()
                    .statusCode(204)
                    .cookie(TOKEN_COOKIE_NAME, "");
        }

        @DisplayName("토큰 정보가 올바르지 않을 경우 401을 반환한다.")
        @Test
        void testInvalidToken() {
            RestAssured.given().log().all()
                    .when().post("/logout")
                    .then().log().all()
                    .statusCode(401);
        }
    }
}
