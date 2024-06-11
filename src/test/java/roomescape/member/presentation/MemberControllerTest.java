package roomescape.member.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import roomescape.member.dto.JoinRequest;
import roomescape.member.dto.MemberResponse;
import roomescape.util.ControllerTest;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends ControllerTest {

    @DisplayName("회원가입 요청을 처리할 수 있다")
    @Test
    void should_handle_join_request() throws Exception {
        JoinRequest request = new JoinRequest("join@test.com", "123", "이름");
        MemberResponse response = new MemberResponse(1L, "이름");

        when(memberService.joinMember(request)).thenReturn(response);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/members/" + response.id()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
