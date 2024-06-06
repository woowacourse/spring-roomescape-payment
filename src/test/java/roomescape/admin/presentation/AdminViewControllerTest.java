package roomescape.admin.presentation;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import roomescape.util.ControllerTest;

@WebMvcTest(AdminViewController.class)
class AdminViewControllerTest extends ControllerTest {

    @DisplayName("어드민 메인 페이지 요청을 처리할 수 있다")
    @Test
    void should_handle_admin_main_page_request_when_requested() throws Exception {
        mockMvc.perform(get("/admin")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));
    }

    @DisplayName("어드민 예약 페이지 요청을 처리할 수 있다")
    @Test
    void should_handle_admin_reservation_page_request_when_requested() throws Exception {
        mockMvc.perform(get("/admin/reservation")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reservation-new"));
    }

    @DisplayName("어드민 시간 관리 페이지 요청을 처리할 수 있다")
    @Test
    void should_handle_admin_time_page_request_when_requested() throws Exception {
        mockMvc.perform(get("/admin/time")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/time"));
    }

    @DisplayName("어드민 테마 관리 페이지 요청을 처리할 수 있다")
    @Test
    void should_handle_admin_theme_page_request_when_requested() throws Exception {
        mockMvc.perform(get("/admin/theme")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/theme"));
    }

    @DisplayName("어드민 예약 대기 관리 페이지 요청을 처리할 수 있다")
    @Test
    void should_handle_admin_reservation_waiting_page_when_requested() throws Exception {
        mockMvc.perform(get("/admin/waiting")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/waiting"));
    }
}
