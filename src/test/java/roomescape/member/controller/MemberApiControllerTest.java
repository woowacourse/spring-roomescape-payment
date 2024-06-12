package roomescape.member.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static roomescape.util.RestDocsFilter.GET_ENTIRE_MEMBERS;
import static roomescape.util.RestDocsFilter.LOGOUT;
import static roomescape.util.RestDocsFilter.SIGN_UP;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import roomescape.config.IntegrationTest;
import roomescape.member.dto.MemberSignUpRequest;
import roomescape.util.CookieUtils;

class MemberApiControllerTest extends IntegrationTest {

    @DisplayName("회원가입에 성공하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void signup() {
        MemberSignUpRequest memberSignUpRequest = new MemberSignUpRequest("카키", "kaki@email.com", "1234");

        RestAssured.given(spec).log().all()
                .filter(SIGN_UP.getFilter())
                .contentType(ContentType.JSON)
                .body(memberSignUpRequest)
                .when()
                .post("/members")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/members/1");
    }

    @DisplayName("회원 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findAll() {
        saveMemberAsKaki();

        RestAssured.given(spec).log().all()
                .filter(GET_ENTIRE_MEMBERS.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, getAdminToken())
                .when()
                .get("/members")
                .then().log().all()
                .statusCode(200)
                .body("responses", hasSize(1));
    }

    @DisplayName("로그아웃을 하면 해당 사용자의 쿠키를 제거한다.")
    @Test
    void logout() {
        RestAssured.given(spec).log().all()
                .filter(LOGOUT.getFilter())
                .cookie(CookieUtils.TOKEN_KEY, "cookieValue")
                .when()
                .post("/logout")
                .then().log().all()
                .statusCode(200)
                .header("Set-Cookie", containsString("token=;"))
                .header("Set-Cookie", containsString("Max-Age=0"))
                .header("Set-Cookie", containsString("Path=/"));
    }
}
