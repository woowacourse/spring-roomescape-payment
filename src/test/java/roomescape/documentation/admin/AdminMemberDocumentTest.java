package roomescape.documentation.admin;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.MemberService;
import roomescape.application.dto.response.MemberResponse;
import roomescape.documentation.AbstractDocumentTest;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.presentation.api.admin.AdminMemberController;

@WebMvcTest(AdminMemberController.class)
class AdminMemberDocumentTest extends AbstractDocumentTest {

    @MockBean
    private MemberService memberService;

    @Test
    @DisplayName("회원 목록을 조회한다.")
    void getAllMembers() throws Exception {
        List<MemberResponse> responses = List.of(
                MemberResponse.from(new Member(1L, "user@gmail.com", "password", "유저", Role.USER)),
                MemberResponse.from(new Member(2L, "admin@gmail.com", "password", "어드민", Role.ADMIN))
        );

        when(memberService.getAllMembers())
                .thenReturn(responses);

        mockMvc.perform(
                get("/admin/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "{ADMIN_TOKEN}"))
        ).andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(responses))
        ).andDo(
                document("admin/members/list",
                        responseFields(
                                fieldWithPath("[].id").description("회원 식별자"),
                                fieldWithPath("[].email").description("이메일"),
                                fieldWithPath("[].name").description("이름"),
                                fieldWithPath("[].role").description("권한")
                        )));
    }
}
