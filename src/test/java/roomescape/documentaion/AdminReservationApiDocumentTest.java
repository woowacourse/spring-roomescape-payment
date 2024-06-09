package roomescape.documentaion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import roomescape.member.application.MemberService;
import roomescape.reservation.application.BookingQueryService;
import roomescape.reservation.application.ReservationManageService;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.application.ThemeService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.AdminReservationSaveRequest;
import roomescape.reservation.presentation.AdminReservationController;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.common.StubLoginMemberArgumentResolver.STUBBED_LOGIN_MEMBER;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;

class AdminReservationApiDocumentTest extends DocumentTest {
    private final BookingQueryService bookingQueryService = Mockito.mock(BookingQueryService.class);
    private final ReservationManageService reservationManageService = Mockito.mock(ReservationManageService.class);
    private final MemberService memberService = Mockito.mock(MemberService.class);
    private final ReservationTimeService reservationTimeService = Mockito.mock(ReservationTimeService.class);
    private final ThemeService themeService = Mockito.mock(ThemeService.class);

    @Test
    @DisplayName("어드민 예약 삭제 API")
    void deleteReservation() throws Exception {
        BDDMockito.willDoNothing()
                .given(reservationManageService)
                .delete(1L, STUBBED_LOGIN_MEMBER);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/admin/reservations/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document(
                                "reservation-delete-admin",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("삭제 대상 예약 식별자")
                                )
                        )
                );
    }

    @Test
    @DisplayName("어드민 예약 생성 API")
    void createReservation() throws Exception {
        AdminReservationSaveRequest request = new AdminReservationSaveRequest(MIA_RESERVATION_DATE, 1L, 1L, 1L);
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Theme expectedTheme = WOOTECO_THEME(1L);
        Reservation expectedReservation = MIA_RESERVATION(1L, expectedTime, expectedTheme, USER_MIA(1L), BOOKING);

        BDDMockito.given(reservationTimeService.findById(anyLong()))
                .willReturn(expectedTime);
        BDDMockito.given(themeService.findById(anyLong()))
                .willReturn(expectedTheme);
        BDDMockito.given(memberService.findById(anyLong()))
                .willReturn(USER_MIA(1L));
        BDDMockito.given(reservationManageService.scheduleRecentReservation(any()))
                .willReturn(expectedReservation);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document(
                                "reservation-create-admin",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("date").type(JsonFieldType.STRING).description("예약 시간(10분 단위) ex) 13:00"),
                                        fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                        fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                        fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("예약자 식별자")
                                ),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                        fieldWithPath("memberName").type(JsonFieldType.STRING).description("예약자 이름"),
                                        fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                        fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                        fieldWithPath("time.startAt").type(JsonFieldType.STRING)
                                                .description("예약 시간(10분 단위) ex) 13:00"),
                                        fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                        fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름")
                                )
                        )
                );
    }

    @Test
    @DisplayName("어드민 예약 목록 조회 API")
    void findReservations() throws Exception {
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Reservation expectedReservation = MIA_RESERVATION(1L, expectedTime, WOOTECO_THEME(1L), USER_MIA(1L), BOOKING);

        BDDMockito.given(bookingQueryService.findAll())
                .willReturn(List.of(expectedReservation));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/reservations").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "reservation-find-admin",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                getReservationArrayResponseFields()
                        )
                );
    }

    @Test
    @DisplayName("어드민 예약 목록 조건 조회 API")
    void findReservationsByMemberIdAndThemeIdAndDateBetween() throws Exception {
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Reservation expectedReservation = MIA_RESERVATION(1L, expectedTime, WOOTECO_THEME(1L), USER_MIA(1L), BOOKING);

        BDDMockito.given(bookingQueryService.findAllByMemberIdAndThemeIdAndDateBetween(anyLong(), anyLong(), any(), any()))
                .willReturn(List.of(expectedReservation));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/admin/reservations/searching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("memberId", String.valueOf(1))
                        .param("themeId", String.valueOf(1))
                        .param("fromDate", MIA_RESERVATION_DATE.toString())
                        .param("toDate", MIA_RESERVATION_DATE.toString()))
                .andDo(print())
                .andDo(document(
                                "reservation-search-admin",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("memberId").description("검색 대상 사용자 식별자"),
                                        parameterWithName("themeId").description("검색 대상 테마 식별자"),
                                        parameterWithName("fromDate").description("검색 대상 시작 날짜"),
                                        parameterWithName("toDate").description("검색 대상 종료 날짜")
                                ),
                                getReservationArrayResponseFields()
                        )
                );
    }

    private ResponseFieldsSnippet getReservationArrayResponseFields() {
        return responseFields(
                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                fieldWithPath("[].memberName").type(JsonFieldType.STRING).description("예약자 이름"),
                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING)
                        .description("예약 시간(10분 단위) ex) 13:00"),
                fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름")
        );
    }

    @Override
    protected Object initController() {
        return new AdminReservationController(
                bookingQueryService,
                reservationManageService,
                memberService,
                reservationTimeService,
                themeService
        );
    }
}
