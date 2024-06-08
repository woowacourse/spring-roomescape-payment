package roomescape.web.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import roomescape.application.dto.request.member.LoginRequest;
import roomescape.application.dto.request.member.SignupRequest;
import roomescape.domain.member.Member;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.support.DatabaseCleanupListener;
import roomescape.support.fixture.MemberFixture;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("회원가입에 성공하면 응답과 201 상태코드를 반환한다.")
    @Test
    void return_201_when_signup() {
        SignupRequest request = new SignupRequest("재즈", "jazz@woowa.com", "123");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);
    }

    @DisplayName("로그인에 성공하면 200 상태코드와 토큰을 반환한다.")
    @Test
    void return_201_and_token_when_login() {
        Member member = memberRepository.save(MemberFixture.MEMBER_JAZZ.create());
        LoginRequest loginRequest = new LoginRequest(member.getEmail(), member.getPassword());

        String token = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract().cookie("token");

        assertThat(token).isNotNull();
    }

    @DisplayName("로그인에 실패하면 400 상태코드를 반환한다.")
    @Test
    void return_400_when_fail_login() {
        Member member = memberRepository.save(MemberFixture.MEMBER_JAZZ.create());
        LoginRequest loginRequest = new LoginRequest(member.getEmail(), "1111");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .statusCode(401);
    }

    @DisplayName("유효한 토큰인지 검증되면 200 상태코드와 로그인된 사용자의 정보를 응답한다.")
    @Test
    void return_200_when_login_check() {
        Member member = memberRepository.save(MemberFixture.MEMBER_JAZZ.create());
        LoginRequest loginRequest = new LoginRequest(member.getEmail(), member.getPassword());

        String token = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract().cookie("token");

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("유효한 토큰이 아니면 401 상태코드를 응답한다.")
    @Test
    void return_401_when_fail_login_check() {
        Member member = memberRepository.save(MemberFixture.MEMBER_JAZZ.create());
        LoginRequest loginRequest = new LoginRequest(member.getEmail(), member.getPassword());

        String token = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract().cookie("token");

        RestAssured.given().log().all()
                .cookie("token", token + "zxczdasd")
                .when().get("/login/check")
                .then().log().all()
                .statusCode(401);
    }
}
