package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import roomescape.model.Member;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MemberControllerTest extends AbstractControllerTest {

    @DisplayName("로그인 요청시 쿠키를 응답한다.")
    @Test
    void should_response_cookie_when_login() {
        String email = "sun@email.com";
        String password = "1234";
        String cookie = getAuthenticationCookie(email, password);
        assertSoftly(assertSoftly -> {
            assertSoftly.assertThat(cookie).isNotBlank();
            assertSoftly.assertThat(cookie).containsPattern("^token=");
        });
    }

    @DisplayName("요청시 쿠키를 제공하면 이름을 반환한다.")
    @Test
    void should_response_member_name_when_given_cookie() {
        String email = "sun@email.com";
        String password = "1234";
        String cookie = getAuthenticationCookie(email, password);

        String name = RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .cookie(cookie)
                .when().get("/login/check")
                .then().statusCode(200)
                .extract().jsonPath().get("name");

        assertThat(name).isEqualTo("썬");
    }

    @DisplayName("모든 사용자들을 반환한다.")
    @Test
    void should_response_all_members() {
        RestAssured
                .given().log().all()
                .contentType(ContentType.JSON)
                .when().get("/members")
                .then().statusCode(200)
                .extract().jsonPath().getList(".", Member.class);
    }
}
