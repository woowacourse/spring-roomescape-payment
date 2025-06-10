package roomescape.member.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.AbstractRestDocsTests;
import roomescape.member.controller.request.SignUpRequest;
import roomescape.member.controller.response.MemberResponse;
import roomescape.member.service.MemberService;

@WebMvcTest(controllers = MemberApiController.class)
class MemberApiControllerTest extends AbstractRestDocsTests {

    @MockitoBean
    private MemberService memberService;

    @Test
    void 회원가입을_할_수_있다() throws Exception {
        // given
        SignUpRequest request = new SignUpRequest("user1@email.com", "1234", "유저1");
        MemberResponse response = new MemberResponse(1L, "user1@email.com", "유저1");
        given(memberService.signUp(any())).willReturn(response);

        // when & then
        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("MBS001"))
                .andExpect(jsonPath("$.message").value("회원가입에 성공했습니다."))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.email").value("user1@email.com"))
                .andExpect(jsonPath("$.data.name").value("유저1"))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("email").description("회원 이메일")
                                        .attributes(constraints("이메일 형식이어야 합니다.")),
                                fieldWithPath("password").description("회원 비밀번호")
                                        .attributes(constraints("비밀번호는 25자 이하")),
                                fieldWithPath("name").description("회원 이름")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.id").description("회원 ID"),
                                fieldWithPath("data.email").description("회원 이메일"),
                                fieldWithPath("data.name").description("회원 이름")
                        )
                ));
    }
}
