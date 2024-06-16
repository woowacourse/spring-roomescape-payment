package roomescape.web.acceptance;

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
import roomescape.application.security.JwtProvider;
import roomescape.domain.member.Member;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.support.DatabaseCleanupListener;
import roomescape.support.fixture.MemberFixture;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        Member member = memberRepository.save(MemberFixture.MEMBER_SOLAR.create());
        adminToken = jwtProvider.encode(member);
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
    void return_204_when_delete_member() {
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
