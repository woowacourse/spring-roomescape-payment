package roomescape.auth.controller;


import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import roomescape.util.ControllerTest;

@DisplayName("회원 페이지 통합 테스트")
class AuthPageControllerTest extends ControllerTest {

    @DisplayName("로그인 페이지 조회 시, 200을 반환한다.")
    @Test
    void getLoginPage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .when().get("/login")
                .then().log().all()
                .apply(document("login/page/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("회원가입 페이지 조회 시, 200을 반환한다.")
    @Test
    void getSignupPage() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .when().get("/signup")
                .then().log().all()
                .apply(document("signup/page/success"))
                .statusCode(HttpStatus.OK.value());
    }
}
