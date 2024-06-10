package roomescape.document;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import roomescape.controller.MemberController;
import roomescape.document.config.RestDocsSupport;
import roomescape.dto.LoginRequest;
import roomescape.dto.MemberInfo;
import roomescape.fixture.MemberFixture;
import roomescape.service.MemberService;
import roomescape.service.TokenService;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
public class MemberRestDocsTest extends RestDocsSupport {

    @MockBean
    private MemberService memberService;

    @MockBean
    private TokenService tokenService;

    @Test
    public void login() throws Exception {
        LoginRequest request = new LoginRequest("email@email.com", "password");
        given(memberService.login(any()))
                .willReturn(MemberFixture.DEFAULT_MEMBER.getId());
        given(tokenService.createToken(anyLong(), any(), any()))
                .willReturn("token=eyJhbGciO.eyJleHAiOjE3MTc5MT.qfeIl0AmZSD3_d8x-Ysxbso");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().exists(HttpHeaders.SET_COOKIE))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("email").description("로그인 할 회원의 이메일"),
                                fieldWithPath("password").description("로그인 할 회원의 패스워드")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.SET_COOKIE).description("로그인한 회원 정보가 담긴 JWT 토큰")
                        )
                ));
    }

    @Test
    void myInfo() throws Exception {
        MemberInfo response = MemberInfo.from(MemberFixture.DEFAULT_MEMBER);
        given(memberService.findByMemberId(anyLong()))
                .willReturn(response);

        // TODO: param이 아닌 cookie의 값을 argumentResolver를 통해 주입받아야 함
        mockMvc.perform(get("/login/check")
                        .param("memberId", "1"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                    responseFields(
                            fieldWithPath("id").description("로그인한 회원 id"),
                            fieldWithPath("name").description("로그인한 회원 이름"),
                            fieldWithPath("role").description("로그인한 회원 권한")
                    )
                ));
    }

    @Test
    void allMembers() throws Exception {
        List<MemberInfo> response = List.of(
                MemberInfo.from(MemberFixture.DEFAULT_MEMBER),
                MemberInfo.from(MemberFixture.DEFAULT_MEMBER)
        );
        given(memberService.findAll())
                .willReturn(response);

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].id").description("로그인한 회원 id"),
                                fieldWithPath("[].name").description("로그인한 회원 이름"),
                                fieldWithPath("[].role").description("로그인한 회원 권한")
                        )
                ));
    }
}
