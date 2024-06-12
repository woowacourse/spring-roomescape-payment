package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.support.DatabaseCleanerExtension;

@ExtendWith(DatabaseCleanerExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthenticationTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @DisplayName("로그인에 성공한다")
    @TestFactory
    Stream<DynamicTest> when_loginSuccess_then_getJwtToken() {
        String name = "mang";
        String email = "mang@woowa.net";
        String password = "password";

        return Stream.of(
                DynamicTest.dynamicTest("회원가입한다", () -> {
                    signUp(name, email, password);
                }),
                DynamicTest.dynamicTest("로그인한다", () -> {
                    login(email, password);
                })
        );
    }

    @DisplayName("인증 토큰이 없으면 인증에 실패한다")
    @TestFactory
    Stream<DynamicTest> when_noToken_then_failAuthentication() {
        return Stream.of(
                DynamicTest.dynamicTest("인증에 실패한다", this::failToAuthCheck)
        );
    }

    @DisplayName("토큰이 있으면 인증에 성공한다")
    @TestFactory
    Stream<DynamicTest> when_havingToken_then_succeedAuthentication() {
        String name = "mang";
        String email = "mang@woowa.net";
        String password = "password";

        AtomicReference<String> token = new AtomicReference<>();

        return Stream.of(
                DynamicTest.dynamicTest("회원가입한다", () -> {
                    signUp(name, email, password);
                }),
                DynamicTest.dynamicTest("로그인한다", () -> {
                    token.set(login(email, password));
                }),
                DynamicTest.dynamicTest("인증에 성공한다", () -> {
                    succeedToAuthCheck(token.get());
                })
        );
    }

    @DisplayName("로그아웃하면 인증에 실패한다")
    @TestFactory
    Stream<DynamicTest> when_logout_then_failAuthentication() {
        String name = "mang";
        String email = "mang@woowa.net";
        String password = "password";

        AtomicReference<String> token = new AtomicReference<>();

        return Stream.of(
                DynamicTest.dynamicTest("회원가입한다", () -> {
                    signUp(name, email, password);
                }),
                DynamicTest.dynamicTest("로그인한다", () -> {
                    token.set(login(email, password));
                }),
                DynamicTest.dynamicTest("로그아웃한다", this::logout),
                DynamicTest.dynamicTest("인증에 실패한다", this::failToAuthCheck)
        );
    }

    private void signUp(String name, String email, String password) {
        RestAssured.given().log().all()
                .body(signUpRequestBody(name, email, password))
                .contentType(ContentType.JSON)
                .when().post("/members")
                .then().log().all()
                .assertThat()
                .statusCode(201);
    }

    private String login(String email, String password) {
        Response response = RestAssured.given().log().all()
                .body(loginRequestBody(email, password))
                .contentType(ContentType.JSON)
                .when().post("/login")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .header("Set-Cookie", Matchers.containsString("token="))
                .extract().response();

        return response.cookie("token");
    }

    private void logout() {
        RestAssured.given().log().all()
                .when().post("/logout")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .assertThat()
                .cookie("token", "");
    }

    private void succeedToAuthCheck(String token) {
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .assertThat()
                .statusCode(200);
    }

    private void failToAuthCheck() {
        RestAssured.given().log().all()
                .when().get("/login/check")
                .then().log().all()
                .assertThat()
                .statusCode(401);
        ;
    }

    private String loginRequestBody(String email, String password) {
        return String.format(
                """
                        {
                            "email": "%s",
                            "password": "%s"
                        }
                        """, email, password
        );
    }

    private String signUpRequestBody(String name, String email, String password) {
        return String.format(
                """
                        {
                            "name": "%s",
                            "email": "%s",
                            "password": "%s"
                        }
                        """, name, email, password
        );
    }
}
