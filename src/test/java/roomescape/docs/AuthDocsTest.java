package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import roomescape.application.member.dto.request.MemberRegisterRequest;
import roomescape.application.member.dto.response.MemberResponse;
import roomescape.application.member.dto.response.TokenResponse;

class AuthDocsTest extends RestDocsTest {

    @Test
    @DisplayName("로그인을 한다.")
    void loginSuccess() {
        TokenResponse tokenResponse = new TokenResponse("tokenContent");

        doReturn(tokenResponse)
                .when(memberService)
                .login(any());

        MemberRegisterRequest request = new MemberRegisterRequest("wiib", "wiib@test.com", "12341234");

        restDocs
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .apply(document("/auth/login/success"));
    }

    @Test
    @DisplayName("로그인에 실패한다.")
    void loginFail() {

        doThrow(new IllegalArgumentException("errorMessage"))
                .when(memberService)
                .login(any());

        MemberRegisterRequest request = new MemberRegisterRequest("wiib", "wiib@test.com", "12341234");

        restDocs
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .apply(document("/auth/login/fail"));
    }

    @Test
    @DisplayName("로그인 인증 정보를 확인한다.")
    void checkLoginSuccess() {
        MemberResponse response = new MemberResponse(1L, "wiib");

        doReturn(response)
                .when(memberService)
                .findById(any(Long.class));

        restDocs
                .contentType(ContentType.JSON)
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .body(response.id())
                .when().get("/login/check")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .apply(document("/auth/loginCheck/success"));
    }

    @Test
    @DisplayName("인증 정보 확인에 실패한다.")
    void checkLoginFailUnauthorized() {
        MemberResponse response = new MemberResponse(1L, "wiib");

        restDocs
                .contentType(ContentType.JSON)
                .body(response.id())
                .when().get("/login/check")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .apply(document("/auth/loginCheck/fail"));
    }


    @Test
    @DisplayName("로그아웃을 한다.")
    void logOutSuccess() {
        restDocs
                .cookie(COOKIE_NAME, getMemberToken(1L, "wiib"))
                .when().post("/logout")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value())
                .apply(document("auth/logout/success"));
    }
}
