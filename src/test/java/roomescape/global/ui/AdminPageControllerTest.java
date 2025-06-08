package roomescape.global.ui;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.global.config.AdminAuthBaseTest;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class AdminPageControllerTest extends AdminAuthBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("관리자 홈페이지 접속 시 admin/index 뷰를 반환한다")
    void adminHomePageTest() throws Exception {
        mockMvc.perform(addAuthCookie(get("/admin")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"))
                .andDo(document("page-admin/home"));
    }

    @Test
    @DisplayName("관리자 예약 페이지 접속 시 admin/reservation-new 뷰를 반환한다")
    void adminReservationPageTest() throws Exception {
        mockMvc.perform(addAuthCookie(get("/admin/reservation")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/reservation-new"))
                .andDo(document("page-admin/reservation"));
    }

    @Test
    @DisplayName("관리자 대기 페이지 접속 시 admin/waiting 뷰를 반환한다")
    void adminWaitingPageTest() throws Exception {
        mockMvc.perform(addAuthCookie(get("/admin/waiting")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/waiting"))
                .andDo(document("page-admin/waiting"));
    }

    @Test
    @DisplayName("관리자 테마 페이지 접속 시 admin/theme 뷰를 반환한다")
    void adminThemePageTest() throws Exception {
        mockMvc.perform(addAuthCookie(get("/admin/theme")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/theme"))
                .andDo(document("page-admin/theme"));
    }

    @Test
    @DisplayName("관리자 시간 페이지 접속 시 admin/time 뷰를 반환한다")
    void adminTimePageTest() throws Exception {
        mockMvc.perform(addAuthCookie(get("/admin/time")))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/time"))
                .andDo(document("page-admin/time"));
    }
}
