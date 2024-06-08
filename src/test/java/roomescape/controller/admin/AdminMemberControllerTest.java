package roomescape.controller.admin;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.controller.config.RestDocsTestSupport;
import roomescape.service.dto.response.MemberResponse;
import roomescape.service.dto.response.MemberResponses;
import roomescape.service.reservation.MemberService;

import java.util.List;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AdminMemberController.class)
class AdminMemberControllerTest extends RestDocsTestSupport {

    @MockBean
    private MemberService memberService;

    @Test
    void findAllMembers_200() throws Exception {
        MemberResponses response = new MemberResponses(
                List.of(
                        new MemberResponse(1L, "memberName1", "ADMIN"),
                        new MemberResponse(2L, "memberName2", "USER")
                )
        );

        Mockito.when(memberService.findAll()).thenReturn(response);

        mockMvc.perform(get("/admin/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                )
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                                responseFields(
                                        fieldWithPath("memberResponses")
                                                .type(JsonFieldType.ARRAY)
                                                .description("전체 멤버 목록"),
                                        fieldWithPath("memberResponses[].id")
                                                .type(JsonFieldType.NUMBER)
                                                .description("멤버 아이디")
                                                .attributes(constraints("positive")),
                                        fieldWithPath("memberResponses[].name")
                                                .type(JsonFieldType.STRING)
                                                .description("멤버 이름")
                                                .attributes(constraints("not null")),
                                        fieldWithPath("memberResponses[].role")
                                                .type(JsonFieldType.STRING)
                                                .description("멤버 권한")
                                                .attributes(constraints("USER : 사용자%nADMIN : 관리자")))
                        )
                );
    }

    @Test
    @DisplayName("멤버 삭제")
    void withdrawMember_204() throws Exception {
        //given
        mockMvc.perform(delete("/admin/members/{id}", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", ADMIN_TOKEN))
                        .requestAttr("loginMember", ADMIN)
                )
                .andExpect(status().isNoContent())
                .andDo(restDocs.document());
    }
}