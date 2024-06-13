package roomescape.documentaion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.payment.application.PaymentService;
import roomescape.payment.application.ProductPayRequest;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentProduct;
import roomescape.reservation.application.BookingManageService;
import roomescape.reservation.application.BookingQueryService;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.application.ThemeService;
import roomescape.reservation.application.WaitingManageService;
import roomescape.reservation.application.WaitingQueryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.request.ReservationPayRequest;
import roomescape.reservation.dto.request.ReservationSaveRequest;
import roomescape.reservation.presentation.ReservationController;

import java.math.BigDecimal;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.HORROR_THEME;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.common.StubLoginMemberArgumentResolver.STUBBED_LOGIN_MEMBER;
import static roomescape.documentaion.ReservationResponseSnippets.RESERVATION_RESPONSE_SINGLE_SNIPPETS;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;
import static roomescape.reservation.domain.ReservationStatus.WAITING;

class ReservationApiDocumentTest extends DocumentTest {
    private final BookingQueryService bookingQueryService = Mockito.mock(BookingQueryService.class);
    private final BookingManageService bookingManageService = Mockito.mock(BookingManageService.class);
    private final WaitingManageService waitingManageService = Mockito.mock(WaitingManageService.class);
    private final WaitingQueryService waitingQueryService = Mockito.mock(WaitingQueryService.class);
    private final PaymentService paymentService = Mockito.mock(PaymentService.class);
    private final ReservationTimeService reservationTimeService = Mockito.mock(ReservationTimeService.class);
    private final ThemeService themeService = Mockito.mock(ThemeService.class);

    @Test
    @DisplayName("예약 생성 API")
    void createReservation() throws Exception {
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(MIA_RESERVATION_DATE, 1L, 1L);
        ProductPayRequest productPayRequest = new ProductPayRequest("key", "orderId", BigDecimal.valueOf(1000L), "none");
        ReservationPayRequest request = new ReservationPayRequest(reservationSaveRequest, productPayRequest);
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Theme expectedTheme = WOOTECO_THEME(1L);
        Reservation expectedReservation = MIA_RESERVATION(1L, expectedTime, expectedTheme, USER_MIA(1L), BOOKING);

        BDDMockito.given(bookingManageService.scheduleRecentReservation(any()))
                .willReturn(expectedReservation);
        BDDMockito.given(reservationTimeService.findById(anyLong()))
                .willReturn(expectedTime);
        BDDMockito.given(themeService.findById(anyLong()))
                .willReturn(expectedTheme);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document(
                                "reservation-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("reservationSaveRequest.date").type(JsonFieldType.STRING).description("예약 시간(10분 단위) ex) 13:00"),
                                        fieldWithPath("reservationSaveRequest.timeId").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                        fieldWithPath("reservationSaveRequest.themeId").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                        fieldWithPath("productPayRequest.paymentKey").type(JsonFieldType.STRING).description("결제 식별자"),
                                        fieldWithPath("productPayRequest.orderId").type(JsonFieldType.STRING).description("주문 식별자"),
                                        fieldWithPath("productPayRequest.amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                        fieldWithPath("productPayRequest.paymentType").type(JsonFieldType.STRING).description("결제 타입")
                                ),
                                RESERVATION_RESPONSE_SINGLE_SNIPPETS()
                        )
                );
    }

    @Test
    @DisplayName("예약 대기 생성 API")
    void createWaiting() throws Exception {
        ReservationSaveRequest request = new ReservationSaveRequest(MIA_RESERVATION_DATE, 1L, 1L);
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Theme expectedTheme = WOOTECO_THEME(1L);
        Reservation expectedReservation = MIA_RESERVATION(1L, expectedTime, expectedTheme, USER_MIA(1L), WAITING);

        BDDMockito.given(waitingManageService.scheduleRecentReservation(any()))
                .willReturn(expectedReservation);
        BDDMockito.given(reservationTimeService.findById(anyLong()))
                .willReturn(expectedTime);
        BDDMockito.given(themeService.findById(anyLong()))
                .willReturn(expectedTheme);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/reservations/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document(
                                "waiting-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("date").type(JsonFieldType.STRING).description("예약 시간(10분 단위) ex) 13:00"),
                                        fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                        fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 식별자")
                                ),
                                RESERVATION_RESPONSE_SINGLE_SNIPPETS()
                        )
                );
    }

    @Test
    @DisplayName("사용자 예약 조회 API")
    void findMyReservations() throws Exception {
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Reservation expectedReservation = MIA_RESERVATION(1L, expectedTime, WOOTECO_THEME(), USER_MIA(), BOOKING);
        WaitingReservation expectedWaitingReservation = new WaitingReservation(
                MIA_RESERVATION(1L, expectedTime, HORROR_THEME(1L), USER_MIA(1L), WAITING), 0);
        Payment expectedPayment = new Payment("paymentKey", "orderId", BigDecimal.valueOf(1000), new PaymentProduct(1L));

        BDDMockito.given(bookingQueryService.findAllByMember(any()))
                .willReturn(List.of(expectedReservation));
        BDDMockito.given(waitingQueryService.findAllWithPreviousCountByMember(any()))
                .willReturn(List.of(expectedWaitingReservation));
        BDDMockito.given(paymentService.findAllInPaymentProducts(any()))
                .willReturn(List.of(expectedPayment));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/reservations/mine").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "reservation-find-mine",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("[].reservationId").type(JsonFieldType.NUMBER).description("예약 식별자"),
                                        fieldWithPath("[].theme").type(JsonFieldType.STRING).description("예약된 테마 이름"),
                                        fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약된 날짜"),
                                        fieldWithPath("[].time").type(JsonFieldType.STRING).description("예약된 시간"),
                                        fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 상태"),
                                        fieldWithPath("[].paymentKey").type(JsonFieldType.STRING).description("결제 식별자").optional(),
                                        fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("결제 금액").optional()
                                )
                        )
                );
    }

    @Test
    @DisplayName("예약 대기 취소 API")
    void deleteMyWaitingReservation() throws Exception {
        BDDMockito.willDoNothing()
                .given(waitingManageService)
                .delete(1L, STUBBED_LOGIN_MEMBER);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/reservations/{id}/waiting", 1L))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document(
                                "waiting-delete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("삭제 대상 대기 중 예약 식별자")
                                )
                        )
                );
    }

    @Override
    protected Object initController() {
        return new ReservationController(
                bookingQueryService,
                waitingManageService,
                bookingManageService,
                waitingQueryService,
                reservationTimeService,
                themeService,
                paymentService
        );
    }
}
