package roomescape.admin.presentation;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.admin.AdminHandlerInterceptor;
import roomescape.login.LoginMemberArgumentResolver;
import roomescape.member.service.MemberService;

@WebMvcTest(AdminMemberController.class)
class AdminMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    @MockBean
    private AdminHandlerInterceptor adminHandlerInterceptor;

    @DisplayName("전체 멤버를 조회할 수 있다")
    @Test
    void should_get_all_members_id_and_name_when_requested() throws Exception {
        when(adminHandlerInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class),
                any(Object.class))).thenReturn(true);

        when(memberService.findAll()).thenReturn(
                Collections.emptyList());

        mockMvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
