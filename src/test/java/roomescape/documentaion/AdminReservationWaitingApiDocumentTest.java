package roomescape.documentaion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import roomescape.reservation.application.ReservationManageService;
import roomescape.reservation.application.WaitingQueryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.presentation.AdminReservationWaitingController;

import java.util.List;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.common.StubLoginMemberArgumentResolver.STUBBED_LOGIN_MEMBER;
import static roomescape.documentaion.ReservationResponseSnippets.RESERVATION_RESPONSE_ARRAY_SNIPPETS;
import static roomescape.reservation.domain.ReservationStatus.WAITING;

class AdminReservationWaitingApiDocumentTest extends DocumentTest {
    private final WaitingQueryService waitingQueryService = Mockito.mock(WaitingQueryService.class);
    private final ReservationManageService reservationManageService = Mockito.mock(ReservationManageService.class);

    @Test
    @DisplayName("어드민 대기 예약 삭제 API")
    void deleteWaiting() throws Exception {
        BDDMockito.willDoNothing()
                .given(reservationManageService)
                .delete(1L, STUBBED_LOGIN_MEMBER);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/reservations/waiting/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document(
                                "waiting-delete-admin",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("삭제 대상 대기 중 예약 식별자")
                                )
                        )
                );
    }

    @Test
    @DisplayName("어드민 예약 대기 목록 조회 API")
    void findWaitings() throws Exception {
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Reservation expectedReservation = MIA_RESERVATION(1L, expectedTime, WOOTECO_THEME(1L), USER_MIA(1L), WAITING);

        BDDMockito.given(waitingQueryService.findAll())
                .willReturn(List.of(expectedReservation));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/reservations/waiting").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "waiting-find-admin",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                RESERVATION_RESPONSE_ARRAY_SNIPPETS()
                        )
                );
    }

    @Override
    protected Object initController() {
        return new AdminReservationWaitingController(waitingQueryService, reservationManageService);
    }
}
