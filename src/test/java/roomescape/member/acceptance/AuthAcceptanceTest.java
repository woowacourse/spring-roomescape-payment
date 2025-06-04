package roomescape.member.acceptance;

import static org.hamcrest.Matchers.equalTo;

import fixture.MemberFixture;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.helper.TestHelper;
import roomescape.member.dto.request.LoginRequest;
import roomescape.member.entity.Member;
import roomescape.member.repository.MemberRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthAcceptanceTest {

    @LocalServerPort
    private int port;
    
    @Autowired
    private MemberRepository memberRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    @DisplayName("로그인에 성공한다.")
    void login() {
        // given
        Member member = MemberFixture.createDefault();
        memberRepository.save(member);
        var loginRequest = new LoginRequest(member.getEmail(), member.getPassword());

        // when & then
        TestHelper.post("/auth/login", loginRequest)
                .then()
                .statusCode(HttpStatus.OK.value())
                .cookie("token");
    }

    @Test
    @DisplayName("로그인에 실패한다.")
    void loginFail() {
        // given
        Member member = MemberFixture.createDefault();
        memberRepository.save(member);
        var loginRequest = new LoginRequest(member.getEmail(), member.getPassword() + "wrong");

        // when & then
        TestHelper.post("/auth/login", loginRequest)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("로그아웃에 성공한다.")
    void logout() {
        // given
        Member member = MemberFixture.createDefault();
        memberRepository.save(member);
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        // when & then
        TestHelper.postWithToken("/auth/logout", token)
                .then()
                .statusCode(HttpStatus.OK.value());

        // 로그아웃 후 로그인 체크 시도
        TestHelper.get("/auth/check")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("로그인 상태를 체크한다.")
    void checkLogin() {
        // given
        Member member = MemberFixture.createDefault();
        memberRepository.save(member);
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        // when & then
        TestHelper.getWithToken("/auth/check", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("id", equalTo(1))
                .body("name", equalTo(member.getName()))
                .body("role", equalTo(member.getRole().getValue()));
    }

    @Test
    @DisplayName("로그인하지 않은 상태에서 로그인 체크 시 실패한다.")
    void checkLoginFail() {
        // when & then
        TestHelper.get("/auth/check")
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }
}
