package roomescape.member.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.AbstractRestDocsTests;
import roomescape.member.controller.response.MemberResponse;
import roomescape.member.service.MemberService;

@WebMvcTest(controllers = MemberAdminApiController.class)
class MemberAdminApiControllerTest extends AbstractRestDocsTests {

    @MockitoBean
    private MemberService memberService;

    @Test
    void 모든_멤버를_조회할_수_있다() throws Exception {
        // given
        List<MemberResponse> responses = List.of(
                new MemberResponse(1L, "user1@naver.com", "USER"),
                new MemberResponse(2L, "admin@naver.com", "ADMIN")
        );
        given(memberService.getMembers()).willReturn(responses);

        // when & then
        mockMvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("MBS002"))
                .andExpect(jsonPath("$.message").value("모든 회원을 조회하였습니다."))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].email").value("user1@naver.com"))
                .andExpect(jsonPath("$.data[0].name").value("USER"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].email").value("admin@naver.com"))
                .andExpect(jsonPath("$.data[1].name").value("ADMIN"))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data[].id").description("회원 ID"),
                                fieldWithPath("data[].email").description("회원 이메일"),
                                fieldWithPath("data[].name").description("회원 이름")
                        )
                ));
    }

}
