package roomescape.view;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import roomescape.util.ControllerTest;

@WebMvcTest(ClientViewController.class)
class ClientViewControllerTest extends ControllerTest {

    @DisplayName("메인 페이지 요청을 처리할 수 있다")
    @Test
    void should_handle_main_page_request_when_requested() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @DisplayName("예약 페이지 요청을 처리할 수 있다")
    @Test
    void should_handle_reservation_page_request_when_requested() throws Exception {
        mockMvc.perform(get("/reservation"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation"));
    }

    @DisplayName("로그인 페이지 요청을 처리할 수 있다")
    @Test
    void should_handle_login_page_request_when_requested() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }
}
