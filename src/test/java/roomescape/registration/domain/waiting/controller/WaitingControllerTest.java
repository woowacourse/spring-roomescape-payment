package roomescape.registration.domain.waiting.controller;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.model.ControllerTest;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.waiting.domain.Waiting;
import roomescape.registration.domain.waiting.dto.WaitingResponse;
import roomescape.registration.domain.waiting.service.WaitingService;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.vo.Name;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WaitingController.class)
class WaitingControllerTest extends ControllerTest {

    @MockBean
    private MemberRepository memberRepository;

    private final Member owner = new Member(1L, new Name("폴라"), "polla@wooteco.com", "polla1234", MemberRole.MEMBER);
    private final Reservation reservation = new Reservation(
            1L,
            LocalDate.now().plusDays(1),
            new ReservationTime(1L, LocalTime.parse("14:00")),
            new Theme(1L, new Name("레모네 테마"), "레모네가 숨겨둔 보물을 찾으세요!", "썸네일 링크", 15000L),
            owner
    );
    private final Waiting waiting = new Waiting(
            1L,
            reservation,
            LocalDateTime.now()
    );

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WaitingService waitingService;

    @Test
    void 예약대기_정보를_잘_불러오는지_확인한다() throws Exception {
        when(waitingService.findWaitings())
                .thenReturn(List.of(WaitingResponse.from(waiting)));

        mockMvc.perform(get("/waitings"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(waiting.getId()))
                .andExpect(jsonPath("$[0].memberName").value(waiting.getReservation().getMember().getName()))
                .andExpect(jsonPath("$[0].themeName").value(waiting.getReservation().getTheme().getName()))
                .andExpect(jsonPath("$[0].date").value(waiting.getReservation().getDate().toString()))
                .andExpect(jsonPath("$[0].startAt")
                        .value(waiting.getReservation().getReservationTime().getStartAt().format(DateTimeFormatter.ofPattern("HH:mm:ss"))));
    }

    @Test
    void 예약대기_신청을_취소한다() throws Exception {
        given(memberRepository.findMemberById(1L))
                .willReturn(Optional.of(owner));

        mockMvc.perform(delete("/waitings/1"))
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
