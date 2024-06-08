package roomescape.documentaion;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.auth.dto.request.LoginRequest;
import roomescape.auth.presentation.AuthController;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.MIA_EMAIL;
import static roomescape.TestFixture.TEST_PASSWORD;
import static roomescape.TestFixture.USER_MIA;

class AuthApiDocumentTest extends DocumentTest {
    @Test
    @DisplayName("로그인 API")
    void login() throws Exception {
        String expectedToken = "authorization-token";
        LoginRequest request = new LoginRequest(MIA_EMAIL, TEST_PASSWORD);

        BDDMockito.given(authService.createToken(anyString(), anyString()))
                .willReturn(expectedToken);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, "token=" + expectedToken))
                .andDo(document(
                                "auth-login",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("로그인 이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("로그인 비밀번호")
                                )
                        )
                );
    }

    @Test
    @DisplayName("인증 정보 요청 API")
    void checkAuthInformation() throws Exception {
        Cookie cookie = new Cookie("token", "token");

        BDDMockito.given(authService.extractMember(any()))
                .willReturn(USER_MIA());

        mockMvc.perform(RestDocumentationRequestBuilders.get("/login/check")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "auth-login-check",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("사용자 이름")
                                )
                        )
                );
    }

    @Test
    @DisplayName("로그아웃 API")
    void logout() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.post("/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, startsWith("token=expired; Max-Age=0;")))
                .andDo(document(
                                "auth-logout",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint())
                        )
                );
    }

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }
}
