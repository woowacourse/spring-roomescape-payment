package roomescape.admin.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.time.fixture.DateTimeFixture.TIME_11_00;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.time.dto.ReservationTimeAddRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;
import roomescape.util.ControllerTest;

@WebMvcTest(AdminReservationTimeController.class)
class AdminReservationTimeControllerTest extends ControllerTest {

    @MockBean
    private ReservationTimeService reservationTimeService;

    @DisplayName("예약 시간 추가 요청을 처리할 수 있다")
    @Test
    void should_handle_post_times_when_requested() throws Exception {
        ReservationTimeAddRequest request = new ReservationTimeAddRequest(TIME_11_00);
        ReservationTimeResponse response = new ReservationTimeResponse(1L, TIME_11_00);

        when(reservationTimeService.saveReservationTime(request)).thenReturn(response);

        mockMvc.perform(post("/admin/times")
                        .cookie(ADMIN_COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/times/" + response.id()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @DisplayName("예약 삭제 요청을 처리할 수 있다")
    @Test
    void should_handle_delete_time_when_requested() throws Exception {
        mockMvc.perform(delete("/admin/times/{id}", 1)
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isNoContent());
    }
}
