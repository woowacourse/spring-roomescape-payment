package roomescape.web.page;

import io.restassured.RestAssured;
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
import roomescape.fixture.MemberFixture;
import roomescape.infrastructure.repository.MemberRepository;
import roomescape.support.DatabaseCleanupListener;

@TestExecutionListeners(value = {
        DatabaseCleanupListener.class,
        DependencyInjectionTestExecutionListener.class
})
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AdminPageControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private MemberRepository memberRepository;

    private String memberToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        Member member = memberRepository.save(MemberFixture.MEMBER_JAZZ.create());
        Member admin = memberRepository.save(MemberFixture.MEMBER_SOLAR.create());
        memberToken = jwtProvider.encode(member);
        adminToken = jwtProvider.encode(admin);
    }

    @DisplayName("로그인하지 않은 사용자가 어드민 페이지를 요청하면 /login 페이지로 리다이렉트 시킨다.")
    @Test
    void redirect_login_page_when_not_login_member_get_admin_page() {
        RestAssured.given().log().all()
                .redirects().follow(false)
                .when().get("/admin/**")
                .then().log().all()
                .statusCode(302)
                .header("Location", "http://localhost:" + port + "/login");
    }

    @DisplayName("어드민 권한 토큰이 없는 사용자가 어드민 페이지를 요청하면 예외를 발생시키고 403 상태코드를 응답한다")
    @Test
    void redirect_index_page_when_not_admin_get_admin_page() {
        RestAssured.given().log().all()
                .cookie("token", memberToken)
                .when().get("/admin/**")
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("어드민 관리 페이지 호출 테스트")
    @Test
    void admin_main_page1() {
        RestAssured.given().log().all()
                .redirects().follow(false)
                .cookie("token", adminToken)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("어드민 예약 관리 페이지 호출 테스트")
    @Test
    void admin_confirmed_reservation_page() {
        RestAssured.given().log().all()
                .redirects().follow(false)
                .cookie("token", adminToken)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("어드민 예약 대기 관리 페이지 호출 테스트")
    @Test
    void admin_waiting_reservation_page() {
        RestAssured.given().log().all()
                .redirects().follow(false)
                .cookie("token", adminToken)
                .when().get("/admin/waiting")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("어드민 시간 관리 페이지 호출 테스트")
    @Test
    void admin_time_page() {
        RestAssured.given().log().all()
                .redirects().follow(false)
                .cookie("token", adminToken)
                .when().get("/admin/time")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("어드민 테마 관리 페이지 호출 테스트")
    @Test
    void admin_theme_page() {
        RestAssured.given().log().all()
                .redirects().follow(false)
                .cookie("token", adminToken)
                .when().get("/admin/theme")
                .then().log().all()
                .statusCode(200);
    }
}
