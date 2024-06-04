package roomescape.acceptance;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import roomescape.service.auth.dto.LoginRequest;
import roomescape.service.auth.dto.SignUpRequest;

@Sql({"/truncate.sql", "/member.sql"})
class AuthAcceptanceTest extends AcceptanceTest {

    @DisplayName("로그인 성공 테스트")
    @Test
    void login() {
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("lily123", "lily@email.com"))
            .when().post("/login")
            .then().log().all()
            .assertThat().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("로그아웃 성공 테스트")
    @TestFactory
    Stream<DynamicTest> logout() {
        AtomicReference<String> token = new AtomicReference<>();
        return Stream.of(
            DynamicTest.dynamicTest("로그인을 한다.", () -> {
                token.set(RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("lini123", "lini@email.com"))
                    .when().post("/login")
                    .then().log().all().extract().cookie("token"));
            }),
            DynamicTest.dynamicTest("로그아웃을 한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", token)
                    .contentType(ContentType.JSON)
                    .when().post("/logout")
                    .then().log().all()
                    .assertThat().statusCode(HttpStatus.OK.value());
            })
        );
    }

    @DisplayName("인증 조회 성공 테스트")
    @TestFactory
    Stream<DynamicTest> check() {
        AtomicReference<String> token = new AtomicReference<>();
        return Stream.of(
            DynamicTest.dynamicTest("로그인을 한다.", () -> {
                token.set(RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("lini123", "lini@email.com"))
                    .when().post("/login")
                    .then().log().all().extract().cookie("token"));
            }),
            DynamicTest.dynamicTest("인증을 조회한다.", () -> {
                RestAssured.given().log().all()
                    .cookie("token", token)
                    .when().get("/login/check")
                    .then().log().all()
                    .assertThat().statusCode(HttpStatus.OK.value()).body("name", is("리니"));
            })
        );
    }

    @DisplayName("회원가입 성공 테스트")
    @Test
    void signUp() {
        RestAssured.given().log().all()
            .contentType(ContentType.JSON)
            .body(new SignUpRequest("user", "user@email.com", "user123"))
            .when().post("/signup")
            .then().log().all()
            .assertThat().statusCode(HttpStatus.CREATED.value());
    }
}
