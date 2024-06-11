package roomescape.admin.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import roomescape.member.dto.MemberResponse;
import roomescape.member.fixture.MemberFixture;
import roomescape.util.ControllerTest;

@WebMvcTest(AdminMemberController.class)
class AdminMemberControllerTest extends ControllerTest {

    @DisplayName("전체 멤버를 조회할 수 있다")
    @Test
    void should_get_all_members_id_and_name_when_requested() throws Exception {

        List<MemberResponse> memberResponses = List.of(new MemberResponse(MemberFixture.MEMBER_ID_1));
        when(memberService.findAll()).thenReturn(memberResponses);

        mockMvc.perform(get("/admin/members")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(memberResponses)));
    }
}
