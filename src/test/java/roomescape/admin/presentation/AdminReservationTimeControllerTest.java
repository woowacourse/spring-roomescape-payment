package roomescape.admin.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.time.fixture.DateTimeFixture.TIME_11_00;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.admin.AdminHandlerInterceptor;
import roomescape.login.LoginMemberArgumentResolver;
import roomescape.time.dto.ReservationTimeAddRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@WebMvcTest(AdminReservationTimeController.class)
public class AdminReservationTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationTimeService reservationTimeService;

    @MockBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    @MockBean
    private AdminHandlerInterceptor adminHandlerInterceptor;


    @DisplayName("예약 시간 추가 요청을 처리할 수 있다")
    @Test
    void should_handle_post_times_when_requested() throws Exception {
        when(adminHandlerInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class),
                any(Object.class))).thenReturn(true);

        ReservationTimeAddRequest reservationTimeAddRequest = new ReservationTimeAddRequest(TIME_11_00);
        ReservationTimeResponse mockResponse = new ReservationTimeResponse(1L, TIME_11_00);

        when(reservationTimeService.saveReservationTime(reservationTimeAddRequest)).thenReturn(mockResponse);

        mockMvc.perform(post("/admin/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationTimeAddRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/times/" + mockResponse.id()));
    }

    @DisplayName("존재하는 리소스에 대한 삭제 요청시, 204 no content를 응답한다.")
    @Test
    void should_handle_delete_time_when_requested() throws Exception {
        when(adminHandlerInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class),
                any(Object.class))).thenReturn(true);

        mockMvc.perform(delete("/admin/times/{id}", 1))
                .andExpect(status().isNoContent());
    }
}
