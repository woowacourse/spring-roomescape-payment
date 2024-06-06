package roomescape.time.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.time.fixture.DateTimeFixture.DAY_AFTER_TOMORROW;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.time.dto.AvailableTimeResponse;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.fixture.DateTimeFixture;
import roomescape.time.fixture.ReservationTimeFixture;
import roomescape.time.service.ReservationTimeService;
import roomescape.util.ControllerTest;

@WebMvcTest(ReservationTimeController.class)
class ReservationTimeControllerTest extends ControllerTest {

    @MockBean
    private ReservationTimeService reservationTimeService;

    @DisplayName("전체 시간을 읽는 요청을 처리할 수 있다")
    @Test
    void should_handle_get_times_request_when_requested() throws Exception {
        List<ReservationTimeResponse> timeResponses = List.of(
                new ReservationTimeResponse(ReservationTimeFixture.RESERVATION_TIME_10_00_ID_1),
                new ReservationTimeResponse(ReservationTimeFixture.RESERVATION_TIME_11_00_ID_2)
        );

        when(reservationTimeService.findAllReservationTime()).thenReturn(timeResponses);

        mockMvc.perform(get("/times"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(timeResponses)));
    }

    @DisplayName("날짜와 테마를 기반으로 예약 상태를 담은 전체 시간 요청을 처리할 수 있다")
    @Test
    void should_handle_get_times_with_book_status_when_requested() throws Exception {
        List<AvailableTimeResponse> timeResponses = List.of(
                new AvailableTimeResponse(1L, DateTimeFixture.TIME_10_00, true),
                new AvailableTimeResponse(2L, DateTimeFixture.TIME_11_00, false)
        );

        when(reservationTimeService.findAllWithReservationStatus(any(LocalDate.class), any(Long.class)))
                .thenReturn(timeResponses);

        mockMvc.perform(get("/times/available")
                        .param("date", DAY_AFTER_TOMORROW.toString())
                        .param("themeId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(timeResponses)));
    }
}
