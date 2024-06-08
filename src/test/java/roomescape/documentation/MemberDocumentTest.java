package roomescape.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.MemberService;
import roomescape.application.dto.request.SignupRequest;
import roomescape.application.dto.response.MemberResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.presentation.api.MemberController;

@WebMvcTest(MemberController.class)
class MemberDocumentTest extends AbstractDocumentTest {

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원 가입을 한다.")
    void signUp() throws Exception {
        SignupRequest request = new SignupRequest("user@gmail.com", "password", "유저");
        Member member = new Member(1L, "user@gmail.com", "password", "유저", Role.USER);
        MemberResponse response = MemberResponse.from(member);

        when(memberService.createMember(any()))
                .thenReturn(response);

        mockMvc.perform(
                post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated(),
                content().json(objectMapper.writeValueAsString(response))
        ).andDo(
                document("members/signup",
                        responseFields(
                                fieldWithPath("id").description("회원 식별자"),
                                fieldWithPath("email").description("회원 이메일"),
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("role").description("회원 권한")
                        ))
        );
    }
}
