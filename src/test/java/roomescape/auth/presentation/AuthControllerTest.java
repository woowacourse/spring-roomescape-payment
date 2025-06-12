package roomescape.auth.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.application.AuthService;
import roomescape.auth.application.LoginMember;
import roomescape.auth.presentation.dto.LoginRequest;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private CookieManager cookieManager;

    @Test
    @DisplayName("로그인 API")
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest("test@example.com", "password123!");
        String token = "token";
        Member member = Member.builder()
                .id(1L)
                .email("test@example.com")
                .password("encodedPassword")
                .name("홍길동")
                .role(MemberRole.MEMBER)
                .build();

        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(86400)
                .build();

        given(authService.createToken(any(LoginRequest.class))).willReturn(token);
        given(authService.getMemberByLoginRequest(any(LoginRequest.class))).willReturn(member);
        given(cookieManager.generateLoginCookie(token)).willReturn(cookie);

        // when && then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andDo(document("auth/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("인증 쿠키")
                        )
                ));
    }

    @Test
    @DisplayName("로그아웃 API")
    void logout() throws Exception {
        // given
        ResponseCookie cookie = ResponseCookie.from("AUTH", "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        given(cookieManager.generateLogoutCookie()).willReturn(cookie);

        // when && then
        mockMvc.perform(post("/logout"))
                .andExpect(status().isNoContent())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andDo(document("auth/logout",
                        preprocessResponse(prettyPrint()),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("로그아웃 처리된 쿠키")
                        )
                ));
    }

    @Test
    @DisplayName("로그인 상태 확인 API")
    void checkLogin() throws Exception {
        // given
        LoginMember loginMember = new LoginMember(1L, "비타", MemberRole.MEMBER);

        given(authService.extractMemberByRequest(any())).willReturn(loginMember);

        // when && then
        mockMvc.perform(get("/login/check"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("비타"))
                .andDo(document("auth/check-login",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("name").description("회원 이름")
                        )
                ));
    }
}
