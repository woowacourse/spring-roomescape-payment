package roomescape.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.createAdminMember;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createMember;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;

class AuthTest extends IntegrationTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    void 로그인_요청시_set_cookie로_토큰을_받을_수_있다() {
        // given
        dbHelper.insertMember(createMember("멍구", "test@naver.com", "1234"));

        // when
        Map<String, String> loginParams = Map.of("email", "test@naver.com", "password", "1234");
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginParams)
                .when()
                .post("/login")
                .then().log().all()
                .statusCode(200)
                .extract();

        assertThat(response.cookie("token")).isNotEmpty();
    }

    @Test
    void 존재하지_않는_회원이_로그인을_시도하면_예외_발생() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "InvalidMember@naver.com", "password", "1234"))
                .when().post("/login")
                .then().log().all()
                .statusCode(401);
    }

    @Test
    void 어드민_회원이_어드민_페이지에_접근_시_통과() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);

        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 어드민_예약페이지_접근_통과() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);

        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 일반_회원이_어드민_페이지에_접근_시_예외_발생() {
        // given
        Member normalMember = createMember("멍구", "test@naver.com", "1234");
        dbHelper.insertMember(normalMember);

        String token = jwtTokenProvider.createToken(createClaims(normalMember));

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin")
                .then().log().all()
                .statusCode(403);
    }

    @Test
    void 정상적으로_로그아웃() {
        // given
        Member normalMember = createMember("멍구", "test@naver.com", "1234");
        dbHelper.insertMember(normalMember);

        String token = jwtTokenProvider.createToken(createClaims(normalMember));

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().post("/logout")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void 쿠키가_존재_한다면_로그인체크_가능() {
        // given
        Member normalMember = createMember("멍구", "test@naver.com", "1234");
        dbHelper.insertMember(normalMember);

        String token = jwtTokenProvider.createToken(createClaims(normalMember));

        // when & then
        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void 쿠키가_존재하지_않는다면_로그인_체크할_때_예외_발생() {
        RestAssured.given().log().all()
                .cookie("token", "")
                .when().get("/login/check")
                .then().log().all()
                .statusCode(401);
    }
}
