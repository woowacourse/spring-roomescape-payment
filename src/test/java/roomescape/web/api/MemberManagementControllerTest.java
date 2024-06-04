package roomescape.web.api;

import static org.hamcrest.Matchers.is;

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
import roomescape.application.dto.request.member.SignupRequest;
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.fixture.MemberFixture;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberManagementControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    String adminToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        Member member = memberRepository.save(MemberFixture.MEMBER_SOLAR.create());
        adminToken = jwtProvider.encode(member);
    }

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

    @DisplayName("멤버 목록을 조회하는데 성공하면 응답과 200 상태코드를 반환한다.")
    @Test
    void return_200_when_find_all_members() {
        memberRepository.save(MemberFixture.MEMBER_BRI.create());
        memberRepository.save(MemberFixture.MEMBER_SUN.create());
        memberRepository.save(MemberFixture.MEMBER_JAZZ.create());

        RestAssured.given()
                .log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .when().get("/admin/members")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));
    }

    @DisplayName("멤버를 삭제하는데 성공하면 응답과 204 상태코드를 반환한다.")
    @Test
    void return_200_when_delete_member() {
        memberRepository.save(MemberFixture.MEMBER_BRI.create());

        RestAssured.given()
                .log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .when().delete("/members/2")
                .then().log().all()
                .statusCode(204);
    }
}
