package roomescape.admin.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.fixture.ReservationFixture;
import roomescape.reservation.service.ReservationService;
import roomescape.util.ControllerTest;

@WebMvcTest(AdminReservationController.class)
class AdminReservationControllerTest extends ControllerTest {

    @MockBean
    private ReservationService reservationService;

    @DisplayName("전체 예약 목록을 읽는 요청을 처리할 수 있다")
    @Test
    void should_handle_read_all_reservations_request_when_requested() throws Exception {
        List<ReservationResponse> reservationResponses = List.of(
                new ReservationResponse(ReservationFixture.SAVED_RESERVATION_1),
                new ReservationResponse(ReservationFixture.SAVED_RESERVATION_2)
        );

        when(reservationService.findAllReservation()).thenReturn(reservationResponses);

        mockMvc.perform(get("/admin/reservations")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(reservationResponses)));
    }
}
