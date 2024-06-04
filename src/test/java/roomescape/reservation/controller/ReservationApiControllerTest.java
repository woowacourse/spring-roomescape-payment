package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import roomescape.client.PaymentClient;
import roomescape.config.ControllerConfig;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.PaymentResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ThemeResponse;
import roomescape.reservation.dto.response.TimeResponse;
import roomescape.reservation.service.ReservationService;

@WebMvcTest(ReservationApiController.class)
@Import(ControllerConfig.class)
class ReservationApiControllerTest {

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private PaymentClient paymentClient;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("예약 목록 조회에 성공하면 200 응답을 받는다.")
    void findAll() throws Exception {
        mockMvc.perform(get("/reservations")
                        .cookie(new Cookie("token", "cookieValue"))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("나의 예약 내역을 조회 성공하면 200을 반환한다.")
    void myReservationsTest() throws Exception {
        mockMvc.perform(get("/reservations/me")
                        .cookie(new Cookie("token", "cookieValue"))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("예약을 성공적으로 제거하면 204 응답을 받는다.")
    void deleteReservationRequestTest() throws Exception {
        mockMvc.perform(delete("/reservations/{id}", 1L)
                        .cookie(new Cookie("token", "cookieValue"))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("예약 대기목록을 성공적으로 조회하면 200 응답을 받는다.")
    void findWaitingTest() throws Exception {
        mockMvc.perform(get("/reservations/waiting")
                        .cookie(new Cookie("token", "cookieValue"))
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("예약 대기를 성공적으로 생성하면 201 응답을 받는다.")
    void createMemberReservationTest() throws Exception {
        // given
        TimeResponse time = new TimeResponse(1L, LocalTime.of(1, 0));
        ThemeResponse theme = new ThemeResponse(1L, "n", "d", "t");
        LocalDate date = LocalDate.of(2023, 1, 1);
        MemberResponse name = new MemberResponse(1L, "name");
        ReservationCreateRequest request = new ReservationCreateRequest(date, theme.id(), time.id(),
                "", "", 1000L, "");
        ReservationResponse response = new ReservationResponse(1L, name, date, theme, time);

        Mockito.when(reservationService.save(any(), any()))
                .thenReturn(1L);
        Mockito.when(paymentClient.paymentReservation(any(), any()))
                .thenReturn(new ResponseEntity<>(new PaymentResponse(""), HttpStatus.OK));
        Mockito.when(reservationService.findById(anyLong()))
                .thenReturn(response);

        // when & then
        mockMvc.perform(post("/reservations")
                        .cookie(new Cookie("token", "value"))
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }
}
