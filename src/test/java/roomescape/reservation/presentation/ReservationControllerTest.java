package roomescape.reservation.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.dto.MemberReservationAddRequest;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.fixture.MemberReservationResponseFixture;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.fixture.DateTimeFixture;
import roomescape.util.ControllerTest;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest extends ControllerTest {

    @MockBean
    private ReservationService reservationService;

    @DisplayName("로그인한 특정 유저의 예약 목록을 읽는 요청을 처리할 수 있다")
    @Test
    void should_handle_read_my_reservations_request_when_requested() throws Exception {
        List<MyReservationResponse> responses = List.of(
                MemberReservationResponseFixture.RESERVATION_1_WAITING_1,
                MemberReservationResponseFixture.RESERVATION_2_WAITING_1
        );

        when(reservationService.findMyReservations(any(Long.class))).thenReturn(responses);

        mockMvc.perform(get("/reservations/my")
                        .cookie(MEMBER_COOKIE))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responses)));
    }

    @DisplayName("멤버 예약을 저장할 수 있다")
    @Test
    void should_handle_create_reservation_request() throws Exception {
        MemberReservationAddRequest request = new MemberReservationAddRequest(
                DateTimeFixture.TOMORROW,
                1L,
                1L,
                "paymentKey",
                "orderId",
                BigDecimal.valueOf(10000)
        );

        ReservationResponse response = new ReservationResponse(
                1L,
                new MemberResponse(1L, "멤버이름"),
                DateTimeFixture.TOMORROW,
                new ReservationTimeResponse(1L, DateTimeFixture.TIME_10_00),
                new ThemeResponse(1L, "테마 이름", "테마 설명", "썸네일 경로")
        );

        when(reservationService.saveMemberReservation(any(Long.class), any(MemberReservationAddRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/reservations")
                        .cookie(MEMBER_COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/reservations/" + response.id()))
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @DisplayName("예약 삭제 요청을 처리할 수 있다")
    @Test
    void should_handle_delete_reservation_when_requested() throws Exception {
        mockMvc.perform(delete("/reservations/{id}", 1)
                        .cookie(MEMBER_COOKIE))
                .andExpect(status().isNoContent());
    }
}
