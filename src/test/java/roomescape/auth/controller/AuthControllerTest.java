package roomescape.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static roomescape.fixture.MemberFixture.getMemberChoco;

import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.auth.controller.dto.MemberResponse;
import roomescape.auth.controller.dto.TokenResponse;
import roomescape.exception.AuthenticationException;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.util.ControllerTest;

@DisplayName("회원 API 통합 테스트")
@AutoConfigureRestDocs
class AuthControllerTest extends ControllerTest {

    @DisplayName("로그인에 성공할 경우, 200을 반환한다.")
    @Test
    void login() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");
        TokenResponse tokenResponse = new TokenResponse("accessToken");

        Map<String, String> params = new HashMap<>();
        params.put("email", getMemberChoco().getEmail());
        params.put("password", getMemberChoco().getPassword());

        //when
        doReturn(memberResponse)
                .when(authService)
                .signUp(any());

        doReturn(tokenResponse)
                .when(authService)
                .createToken(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/api/v1/login")
                .then().log().all()
                .apply(document("login/success",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        )))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("비밀번호가 다를 경우, 401을 반환한다.")
    @Test
    void invalidPassword() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("email", getMemberChoco().getEmail());
        params.put("password", "wrongPassword");

        //when
        doThrow(AuthenticationException.class)
                .when(authService)
                .authenticate(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/api/v1/login")
                .then().log().all()
                .apply(document("login/fail/invalid-password"))
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("토큰을 통해 회원을 검증할 경우, 200을 반환한다.")
    @Test
    void loginCheck() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when().get("/api/v1/login/check")
                .then().log().all()
                .apply(document("login/check/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("토큰 오류 발생 시, 401을 반환한다.")
    @Test
    void invalidTokenCheck() {
        //given
        String invalidToken = "invalidToken";

        doThrow(new AuthenticationException(ErrorType.TOKEN_PAYLOAD_EXTRACTION_FAILURE))
                .when(authService)
                .fetchByToken(invalidToken);

        // when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", invalidToken)
                .when().get("/api/v1/login/check")
                .then().log().all()
                .apply(document("login/check/fail/invalid-token"))
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("토큰을 통해 로그아웃할 경우, 200을 반환한다.")
    @Test
    void logout() {
        //given & when & then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when().post("/api/v1/logout")
                .then().log().all()
                .apply(document("logout/success"))
                .statusCode(HttpStatus.OK.value())
                .assertThat(header().doesNotExist("token"));
    }

    @DisplayName("회원 가입 시, 201을 반환한다.")
    @Test
    void signup() {
        //given
        MemberResponse memberResponse = new MemberResponse(1L, "초코칩");

        Map<String, String> params = new HashMap<>();
        params.put("name", "chocochip2");
        params.put("email", "dev.chocochip2@gmail.com");
        params.put("password", "12345Hsad@!sdlsadasdnk");

        //when
        doReturn(memberResponse)
                .when(authService)
                .signUp(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/api/v1/signup")
                .then().log().all()
                .apply(document("member/create/success",
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        )))
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("중복된 메일로 회원 가입할 경우, 400를 반환한다.")
    @Test
    void duplicatedEmail() {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "chocochip");
        params.put("email", "dev.chocochip@gmail.com");
        params.put("password", "12345");

        //when
        doThrow(new BadRequestException(ErrorType.DUPLICATED_EMAIL_ERROR))
                .when(authService)
                .signUp(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/api/v1/signup")
                .then().log().all()
                .apply(document("member/create/fail/duplicated-email"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("메일 형식이 아닐 경우, 400을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "chocochip", "chocochip@"})
    void signupInvalidMailFormat(String invalidMail) {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "chocochip");
        params.put("email", invalidMail);
        params.put("password", "12345Hsad@!sdlsadasdnk");

        //when & then
        restDocs
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/api/v1/signup")
                .then().log().all()
                .apply(document("member/create/fail/invalid-email-format",
                        responseFields(
                                fieldWithPath("code").description("에러코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("validation.email").description("상세 사유")
                        )))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("비밀번호가 최소 8자 이상, 하나 이상의 대문자, 소문자, 숫자, 특수 문자를 포함하지 아닐 경우, 400을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"abc", "   ", "chocochip", "chocochip@"})
    void signupInvalidPassword(String invalidPassword) {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", "chocochip");
        params.put("email", "dev.something@gmail.com");
        params.put("password", invalidPassword);

        //when & then
        restDocs
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/api/v1/signup")
                .then().log().all()
                .apply(document("member/create/fail/invalid-password-format",
                        responseFields(
                                fieldWithPath("code").description("에러코드"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("validation.password").description("상세 사유")
                        )))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
