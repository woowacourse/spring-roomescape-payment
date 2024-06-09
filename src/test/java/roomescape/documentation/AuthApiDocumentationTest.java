package roomescape.documentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import roomescape.controller.api.AuthController;
import roomescape.controller.dto.request.LoginRequest;
import roomescape.service.AuthService;
import roomescape.service.dto.response.TokenResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class AuthApiDocumentationTest extends BaseDocumentationTest {
    private final AuthService authService = Mockito.mock(AuthService.class);

    @Test
    @DisplayName("로그인을 한다")
    void login() throws Exception {
        TokenResponse response = new TokenResponse("member-token");
        when(authService.authenticateMember(any()))
                .thenReturn(response);
        LoginRequest request = new LoginRequest("prin@email.com", "password");
        String content = objectMapper.writeValueAsString(request);


        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, "token=member-token; Path=/; HttpOnly"))
                .andDo(print())
                .andDo(document("auth/login",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        )
                ));
    }

    @Test
    @DisplayName("로그인을 체크한다")
    void checkLogin() throws Exception {
        mockMvc.perform(get("/login/check")
                        .cookie(memberCookie)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("auth/check",
                        responseFields(
                                fieldWithPath("id").description("회원 id"),
                                fieldWithPath("name").description("이름")
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃을 한다")
    void logout() throws Exception {
        mockMvc.perform(post("/logout")
                        .cookie(memberCookie)
                )
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, "token=; Path=/; Max-Age=0; Expires=Thu, 1 Jan 1970 00:00:00 GMT"))
                .andDo(print())
                .andDo(document("auth/logout"));
    }

    @Override
    Object controller() {
        return new AuthController(authService);
    }
}
