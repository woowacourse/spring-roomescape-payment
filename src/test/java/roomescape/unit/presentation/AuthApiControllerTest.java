package roomescape.unit.presentation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.auth.AuthToken;
import roomescape.business.model.entity.Member;
import roomescape.exception.auth.EmailNotRegisteredException;
import roomescape.presentation.dto.request.LoginRequest;
import roomescape.presentation.dto.response.MemberResponse;

class AuthApiControllerTest extends ControllerTest {

    @Test
    void 로그인에_성공한다() throws Exception {
        // given
        LoginRequest request = new LoginRequest("email1@domain.com", "password1");
        AuthToken response = new AuthToken("accessToken");
        given(authService.authenticate(request)).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, "authToken=accessToken; HttpOnly; SameSite=STRICT"))
                .andDo(document("auth/login/success",
                        requestFields(
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                        )
                ));
    }

    @Test
    void 로그인에_실패한다() throws Exception {
        // given
        LoginRequest request = new LoginRequest("email1@domain.com", "password1");
        AuthToken response = new AuthToken("accessToken");
        given(authService.authenticate(request)).willThrow(new EmailNotRegisteredException());
        // when
        ResultActions result = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isBadRequest())
                .andDo(document("auth/login/error",
                        responseFields(
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드")
                        )
                ));
    }

    @Test
    void 인증_정보_조회에_성공한다() throws Exception {
        // given
        MemberResponse response = new MemberResponse("memberId1", "name", "email1@domain.com");
        given(memberService.getById(anyString())).willReturn(response);
        AuthToken token = jwtUtil.createToken(Member.create("name", "email1@domain.com", "password1"));
        // when
        ResultActions result = mockMvc.perform(get("/login/check")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("memberId1"))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("email1@domain.com"))
                .andDo(document("auth/login-check/success",
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("회원 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                        )
                ));
    }
}