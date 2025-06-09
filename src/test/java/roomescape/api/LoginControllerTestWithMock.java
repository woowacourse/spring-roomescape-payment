package roomescape.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.global.ControllerTest;
import roomescape.member.controller.LoginController;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.dto.LoginRequest;
import roomescape.member.service.AuthService;
import roomescape.member.service.MemberService;

@WebMvcTest(LoginController.class)
@AutoConfigureRestDocs
class LoginControllerTestWithMock extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("로그인 API 테스트")
    class LoginTest {

        @DisplayName("정상적인 로그인 요청 시 200 OK를 반환한다")
        @Test
        void loginSuccess() throws Exception {
            // given
            final Member member = new Member(1L, "test@example.com", "password", "테스터", "session123",  MemberRole.USER);
            final LoginRequest request = new LoginRequest("test@example.com", "password");

            given(authService.getMemberByEmailAndPassword(any(LoginRequest.class)))
                    .willReturn(member);
            willDoNothing().given(authService).updateSessionId(any(Member.class), any(String.class));

            // when & then
            mockMvc.perform(post("/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                    fieldWithPath("password").type(JsonFieldType.STRING).description("비밀번호")
                            ),
                            responseHeaders(
                                    headerWithName("Set-Cookie").description("세션 쿠키가 설정됩니다").optional()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("로그아웃 API 테스트")
    class LogoutTest {

        @DisplayName("로그아웃 요청 시 200 OK를 반환한다")
        @Test
        void logoutSuccess() throws Exception {
            // given
            final MockHttpSession session = new MockHttpSession();
            session.setAttribute("id", 1L);

            // when & then
            mockMvc.perform(post("/logout")
                            .session(session)
                            .cookie(new Cookie("JSESSIONID", session.getId())))
                    .andExpect(status().isOk())
                    .andDo(restDocs.document(
                            requestCookies(
                                    cookieWithName("JSESSIONID").description("회원 ID 값")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("로그인 확인 API 테스트")
    class LoginCheckTest {

        @DisplayName("로그인된 상태에서 로그인 확인 시 200 OK를 반환한다")
        @Test
        void loginCheckSuccess() throws Exception {
            // given
            final Member member = new Member(1L, "test@example.com", "password", "테스터", "session123",  MemberRole.USER);
            final MockHttpSession session = new MockHttpSession();
            session.setAttribute("id", 1L);

            given(memberService.getMemberById(1L)).willReturn(member);

            // when & then
            mockMvc.perform(get("/login/check")
                            .session(session)
                            .cookie(new Cookie("JSESSIONID", session.getId())))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("테스터"))
                    .andDo(restDocs.document(
                            requestCookies(
                                    cookieWithName("JSESSIONID").description("회원 ID 값")
                            ),
                            responseFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("로그인된 회원 이름")
                            )
                    ));
        }

        @DisplayName("로그인되지 않은 상태에서 로그인 확인 시 401 Unauthorized를 반환한다")
        @Test
        void loginCheckUnauthorized() throws Exception {
            // when & then
            mockMvc.perform(get("/login/check"))
                    .andExpect(status().isUnauthorized())
                    .andDo(restDocs.document());
        }
    }
}
