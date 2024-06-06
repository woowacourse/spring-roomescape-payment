package roomescape.reservation.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import roomescape.auth.presentation.AdminAuthorizationInterceptor;
import roomescape.auth.presentation.LoginMemberArgumentResolver;
import roomescape.common.ControllerTest;
import roomescape.global.config.WebMvcConfiguration;
import roomescape.reservation.application.WaitingManageService;
import roomescape.reservation.application.WaitingQueryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.MIA_NAME;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.TOMMY_NAME;
import static roomescape.TestFixture.TOMMY_RESERVATION;
import static roomescape.TestFixture.TOMMY_RESERVATION_DATE;
import static roomescape.TestFixture.TOMMY_RESERVATION_TIME;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.USER_TOMMY;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.TestFixture.WOOTECO_THEME_NAME;
import static roomescape.common.StubLoginMemberArgumentResolver.STUBBED_LOGIN_MEMBER;
import static roomescape.reservation.domain.ReservationStatus.PENDING_PAYMENT;
import static roomescape.reservation.domain.ReservationStatus.WAITING;

@WebMvcTest(
        value = AdminReservationWaitingController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {WebMvcConfiguration.class, LoginMemberArgumentResolver.class, AdminAuthorizationInterceptor.class})
)
class AdminReservationWaitingControllerTest extends ControllerTest {
    @MockBean
    private WaitingQueryService waitingQueryService;

    @MockBean
    private WaitingManageService waitingManageService;

    @Test
    @DisplayName("대기 예약 목록 GET 요청 시 상태코드 200을 반환한다.")
    void findWaitingReservations() throws Exception {
        // given
        List<Reservation> expectedWaitingReservations = List.of(
                MIA_RESERVATION(new ReservationTime(MIA_RESERVATION_TIME), WOOTECO_THEME(), USER_MIA(), WAITING),
                TOMMY_RESERVATION(new ReservationTime(TOMMY_RESERVATION_TIME), WOOTECO_THEME(), USER_TOMMY(), PENDING_PAYMENT)
        );
        BDDMockito.given(waitingQueryService.findAll())
                .willReturn(expectedWaitingReservations);

        // when & then
        mockMvc.perform(get("/admin/reservations/waiting"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].memberName").value(MIA_NAME))
                .andExpect(jsonPath("$[0].time.startAt").value(MIA_RESERVATION_TIME.toString()))
                .andExpect(jsonPath("$[0].theme.name").value(WOOTECO_THEME_NAME))
                .andExpect(jsonPath("$[0].date").value(MIA_RESERVATION_DATE.toString()))
                .andExpect(jsonPath("$[0].status").value("예약 대기"))
                .andExpect(jsonPath("$[1].memberName").value(TOMMY_NAME))
                .andExpect(jsonPath("$[1].time.startAt").value(TOMMY_RESERVATION_TIME.toString()))
                .andExpect(jsonPath("$[1].date").value(TOMMY_RESERVATION_DATE.toString()))
                .andExpect(jsonPath("$[1].status").value("결제 대기"))
                .andDo(document("admin-waiting-reservations/find-all/success"));
    }

    @Test
    @DisplayName("대기 예약 DELETE 요청 시 상태코드 204를 반환한다.")
    void deleteWaitingReservation() throws Exception {
        // given
        BDDMockito.willDoNothing()
                .given(waitingManageService)
                .delete(1L, STUBBED_LOGIN_MEMBER);

        // when & then
        mockMvc.perform(delete("/admin/reservations/waiting/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("admin-waiting-reservations/delete/success"));
    }

    @Test
    @DisplayName("대기 예약 승인 PATCH 요청 시 상태코드 200을 반환한다.")
    void approve() throws Exception {
        // given
        BDDMockito.willDoNothing()
                .given(waitingManageService)
                .approve(1L, STUBBED_LOGIN_MEMBER);

        // when & then
        mockMvc.perform(patch("/admin/reservations/waiting/{id}/approval", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-waiting-reservations/approve/success"));
    }
}
