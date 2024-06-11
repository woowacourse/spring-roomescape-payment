package roomescape.admin.presentation;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.WaitingResponse;
import roomescape.reservation.fixture.ReservationFixture;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.fixture.DateTimeFixture;
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

    @DisplayName("예약 목록을 멤버, 테마, 기간으로 필터링하여 조회하는 요청을 처리할 수 있다")
    @Test
    void should_handle_filtered_read_all() throws Exception {
        List<ReservationResponse> reservationResponses = List.of(
                new ReservationResponse(ReservationFixture.SAVED_RESERVATION_1),
                new ReservationResponse(ReservationFixture.SAVED_RESERVATION_2)
        );

        when(reservationService.findAllReservation()).thenReturn(reservationResponses);

        mockMvc.perform(get("/admin/reservations")
                        .param("memberId", "1")
                        .param("themeId", "1")
                        .param("dateFrom", "2024-05-07")
                        .param("dateEnd", "2024-06-07")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(reservationResponses)));
    }

    @DisplayName("관리자 권한으로 예약을 생성하는 요청을 처리할 수 있다")
    @Test
    void should_handle_create_reservation_request_from_admin() throws Exception {
        ReservationRequest request = new ReservationRequest(
                DateTimeFixture.TOMORROW,
                1L,
                1L,
                1L);

        ReservationResponse response = new ReservationResponse(
                1L,
                new MemberResponse(1L, "멤버이름"),
                DateTimeFixture.TOMORROW,
                new ReservationTimeResponse(1L, DateTimeFixture.TIME_10_00),
                new ThemeResponse(1L, "테마 이름", "테마 설명", "썸네일 경로")
        );

        when(reservationService.saveAdminReservation(request)).thenReturn(response);

        mockMvc.perform(post("/admin/reservations")
                        .cookie(ADMIN_COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/reservations/" + response.id()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @DisplayName("대기 중인 예약 정보 조회 요청을 처리할 수 있다")
    @Test
    void should_handle_read_waiting_reservations_request() throws Exception {
        List<WaitingResponse> waitingResponses = List.of(
                new WaitingResponse(ReservationFixture.SAVED_RESERVATION_1),
                new WaitingResponse(ReservationFixture.SAVED_RESERVATION_2)
        );

        when(reservationService.findReservationsOnWaiting()).thenReturn(waitingResponses);

        mockMvc.perform(get("/admin/reservations/waitings")
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(waitingResponses)));
    }

    @DisplayName("예약 정보를 삭제하는 요청을 처리할 수 있다")
    @Test
    void should_handle_delete_reservation_request() throws Exception {
        mockMvc.perform(delete("/admin/reservations/{id}", 1)
                        .cookie(ADMIN_COOKIE))
                .andExpect(status().isNoContent());
    }
}
