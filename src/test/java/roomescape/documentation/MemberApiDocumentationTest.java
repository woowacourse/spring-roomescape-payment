package roomescape.documentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import roomescape.controller.api.MemberController;
import roomescape.controller.dto.request.SignupRequest;
import roomescape.service.MemberService;
import roomescape.service.dto.response.MemberResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class MemberApiDocumentationTest extends BaseDocumentationTest {

    private final MemberService memberService = Mockito.mock(MemberService.class);

    @Test
    @DisplayName("회원가입을 한다")
    void signup() throws Exception {
        MemberResponse response = new MemberResponse(1L, "미르");
        when(memberService.createMember(any()))
                .thenReturn(response);
        SignupRequest request = new SignupRequest("mir@email.com", "password", "미르");
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("member/signup",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("이름")
                        ),
                        responseFields(
                                fieldWithPath("id").description("회원 id"),
                                fieldWithPath("name").description("이름")
                        )
                ));
    }

    @Test
    @DisplayName("모든 회원을 조회한다")
    void getAllMembers() throws Exception {
        when(memberService.getAllMembers())
                .thenReturn(List.of(
                        new MemberResponse(1L, "미르"),
                        new MemberResponse(2L, "프린")
                ));

        mockMvc.perform(get("/members"))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("member/findAll",
                        responseFields(
                                fieldWithPath("list.[].id").description("회원 id"),
                                fieldWithPath("list.[].name").description("이름")
                        )
                ));
    }

    @Override
    Object controller() {
        return new MemberController(memberService);
    }
}
