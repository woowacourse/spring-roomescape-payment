package roomescape.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticMemberPageControllerTest {
    @DisplayName("로그인 폼이 있는 페이지를 응답한다.")
    @Test
    void should_response_login_when_request_get_login() {
        RestAssured.given().log().all()
                .when().get("/login")
                .then().log().all()
                .statusCode(200);
    }
}
