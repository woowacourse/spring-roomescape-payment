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

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class UserPageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("홈페이지 접속 시 index 뷰를 반환한다")
    void homePageTest() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andDo(document("page-user/home"));
    }

    @Test
    @DisplayName("예약 페이지 접속 시 reservation 뷰를 반환한다")
    void reservationPageTest() throws Exception {
        mockMvc.perform(get("/reservation"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation"))
                .andDo(document("page-user/reservation"));
    }

    @Test
    @DisplayName("내 예약 페이지 접속 시 reservation-mine 뷰를 반환한다")
    void reservationsInfoPageTest() throws Exception {
        mockMvc.perform(get("/reservation-mine"))
                .andExpect(status().isOk())
                .andExpect(view().name("reservation-mine"))
                .andDo(document("page-user/mine"));
    }

    @Test
    @DisplayName("로그인 페이지 접속 시 login 뷰를 반환한다")
    void loginPageTest() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andDo(document("page-user/login"));
    }

    @Test
    @DisplayName("회원가입 페이지 접속 시 signup 뷰를 반환한다")
    void signupPageTest() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("signup"))
                .andDo(document("page-user/signup"));
    }
}
