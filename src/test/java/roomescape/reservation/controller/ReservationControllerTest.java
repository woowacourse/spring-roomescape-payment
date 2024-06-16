package roomescape.reservation.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;
import roomescape.client.payment.service.PaymentClient;
import roomescape.config.ClientConfig;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.model.ControllerTest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationDto;
import roomescape.reservation.dto.ReservationTimeAvailabilityResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.vo.Name;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(ReservationController.class)
@ContextConfiguration(classes = ClientConfig.class)
@Disabled
class ReservationControllerTest extends ControllerTest {

    private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    private final Reservation reservation = new Reservation(
            1L,
            TOMORROW,
            new ReservationTime(1L, LocalTime.of(10, 0)),
            new Theme(1L, new Name("polla"), "폴라 방탈출", "이미지~"),
            new Member(1L, new Name("polla"), "kyunellroll@gmail.com", "polla99", MemberRole.MEMBER)
    );

    private final String expectedStartAt = "10:00:00";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private PaymentClient paymentClient;

    @MockBean
    private RestClient restClient;

    @Test
    @DisplayName("예약 정보를 잘 불러오는지 확인한다.")
    void findAllReservations() throws Exception {
        when(reservationService.findReservations())
                .thenReturn(List.of(ReservationDto.from(reservation)));

        mockMvc.perform(get("/reservations"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(reservation.getId()))
                .andExpect(jsonPath("$[0].memberName").value(reservation.getMember().getName()))
                .andExpect(jsonPath("$[0].startAt").value(expectedStartAt))
                .andExpect(jsonPath("$[0].themeName").value(reservation.getTheme().getName()));
    }

    @Test
    @DisplayName("예약 가능한 시간을 잘 불러오는지 확인한다.")
    void findAvailableTimeList() throws Exception {
        when(reservationService.findTimeAvailability(1, TOMORROW))
                .thenReturn(
                        List.of(ReservationTimeAvailabilityResponse.fromTime(reservation.getReservationTime(), true)));

        mockMvc.perform(get("/reservations/1?date=" + TOMORROW))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].startAt").value(expectedStartAt))
                .andExpect(jsonPath("$[0].timeId").value(reservation.getReservationTime().getId()))
                .andExpect(jsonPath("$[0].alreadyBooked").value(true));
    }

    @Test
    @DisplayName("예약 정보를 잘 지우는지 확인한다.")
    void deleteReservation() throws Exception {
        mockMvc.perform(delete("/reservations/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
