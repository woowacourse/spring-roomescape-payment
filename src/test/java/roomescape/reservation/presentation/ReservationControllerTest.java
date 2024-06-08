package roomescape.reservation.presentation;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import roomescape.auth.presentation.AdminAuthorizationInterceptor;
import roomescape.auth.presentation.LoginMemberArgumentResolver;
import roomescape.common.ControllerTest;
import roomescape.common.TestWebMvcConfiguration;
import roomescape.global.config.WebMvcConfiguration;
import roomescape.global.exception.NotFoundException;
import roomescape.global.exception.ViolationException;
import roomescape.payment.application.PaymentService;
import roomescape.payment.domain.ConfirmedPayment;
import roomescape.payment.domain.Payment;
import roomescape.reservation.application.BookingManageService;
import roomescape.reservation.application.ReservationFactory;
import roomescape.reservation.application.ReservationQueryService;
import roomescape.reservation.application.WaitingManageService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationPayment;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.domain.WaitingReservation;
import roomescape.reservation.dto.request.PaymentConfirmRequest;
import roomescape.reservation.dto.request.ReservationPayRequest;
import roomescape.reservation.dto.request.ReservationSaveRequest;
import roomescape.reservation.dto.response.MyReservationResponse;

import java.util.List;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.HORROR_THEME;
import static roomescape.TestFixture.HORROR_THEME_NAME;
import static roomescape.TestFixture.MIA_NAME;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.USER_MIA;
import static roomescape.TestFixture.WOOTECO_THEME;
import static roomescape.TestFixture.WOOTECO_THEME_NAME;
import static roomescape.common.StubLoginMemberArgumentResolver.STUBBED_LOGIN_MEMBER;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;
import static roomescape.reservation.domain.ReservationStatus.WAITING;

@Import(TestWebMvcConfiguration.class)
@WebMvcTest(
        value = ReservationController.class,
        excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = {WebMvcConfiguration.class, LoginMemberArgumentResolver.class, AdminAuthorizationInterceptor.class})
)
class ReservationControllerTest extends ControllerTest {
    private static final Cookie COOKIE = new Cookie("token", "token");
    private static final PaymentConfirmRequest paymentConfirmRequest =
            new PaymentConfirmRequest("key", "orderId", 1000L, "none");

    @MockBean
    private ReservationQueryService reservationQueryService;

    @MockBean
    private BookingManageService bookingManageService;

    @MockBean
    private WaitingManageService waitingManageService;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private ReservationFactory reservationFactory;

    @Test
    @DisplayName("예약 POST 요청 시 상태코드 201을 반환한다.")
    void createReservation() throws Exception {
        // given
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(MIA_RESERVATION_DATE, 1L, 1L);
        ReservationPayRequest request = new ReservationPayRequest(reservationSaveRequest, paymentConfirmRequest);
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Theme expectedTheme = WOOTECO_THEME(1L);
        Reservation expectedReservation = MIA_RESERVATION(expectedTime, expectedTheme, USER_MIA(1L), BOOKING);
        ConfirmedPayment expectedConfirmedPayment = new ConfirmedPayment("paymentKey", "orderId", 10);

        BDDMockito.given(reservationFactory.create(anyLong(), anyLong(), anyLong(), any(), any()))
                .willReturn(expectedReservation);
        BDDMockito.given(bookingManageService.createWithPayment(any(), any()))
                .willReturn(expectedReservation);
        BDDMockito.given(paymentService.confirm(any()))
                .willReturn(expectedConfirmedPayment);

        // when & then
        mockMvc.perform(post("/reservations")
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.memberName").value(MIA_NAME))
                .andExpect(jsonPath("$.time.id").value(1L))
                .andExpect(jsonPath("$.time.startAt").value(MIA_RESERVATION_TIME.toString()))
                .andExpect(jsonPath("$.date").value(MIA_RESERVATION_DATE.toString()))
                .andDo(document("reservations/create/success"));
    }

    @ParameterizedTest
    @MethodSource(value = "invalidPostRequests")
    @DisplayName("예약 POST 요청 시 하나의 필드라도 없다면 상태코드 400을 반환한다.")
    void createReservationWithNullFieldRequest(ReservationSaveRequest reservationSaveRequest) throws Exception {
        ReservationPayRequest request = new ReservationPayRequest(reservationSaveRequest, paymentConfirmRequest);

        // when & then
        mockMvc.perform(post("/reservations")
                        .cookie(COOKIE)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andDo(document("reservations/create/fail/null-field"));
    }

    private static Stream<ReservationSaveRequest> invalidPostRequests() {
        return Stream.of(
                new ReservationSaveRequest(null, 1L, 1L),
                new ReservationSaveRequest(MIA_RESERVATION_DATE, null, 1L),
                new ReservationSaveRequest(MIA_RESERVATION_DATE, 1L, null)
        );
    }

    @Test
    @DisplayName("올바르지 않은 예약 날짜 형식으로 예약 POST 요청 시 상태코드 400을 반환한다.")
    void createReservationWithInvalidDateFormat() throws Exception {
        // given
        String invalidDateFormatRequest = """
                {
                    "reservationSaveRequest" : {
                        "date": "invalid"
                    }
                }
                """;

        // when & then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(COOKIE)
                        .content(invalidDateFormatRequest))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andDo(document("reservations/create/fail/date-format"));
    }

    @Test
    @DisplayName("동일한 사용자가 중복 예약 POST 요청 시 상태코드 400을 반환한다.")
    void createDuplicatedReservation() throws Exception {
        // given
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(MIA_RESERVATION_DATE, 1L, 1L);
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Theme expectedTheme = WOOTECO_THEME(1L);
        Reservation expectedReservation = MIA_RESERVATION(expectedTime, expectedTheme, USER_MIA(1L), BOOKING);
        ReservationPayRequest request = new ReservationPayRequest(reservationSaveRequest, paymentConfirmRequest);
        ConfirmedPayment expectedConfirmedPayment = new ConfirmedPayment("paymentKey", "orderId", 10);

        BDDMockito.given(reservationFactory.create(anyLong(), anyLong(), anyLong(), any(), any()))
                .willReturn(expectedReservation);
        BDDMockito.willThrow(new ViolationException("동일한 사용자의 중복된 예약입니다."))
                .given(bookingManageService)
                .createWithPayment(any(), any());
        BDDMockito.given(paymentService.confirm(any()))
                .willReturn(expectedConfirmedPayment);

        // when & then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(COOKIE)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andDo(document("reservations/create/fail/duplicated-reservation"));
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간의 예약 POST 요청 시 상태코드 404를 반환한다.")
    void createReservationWithNotExistingTime() throws Exception {
        // given
        Long notExistingTimeId = 1L;
        Long themeId = 1L;
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(MIA_RESERVATION_DATE, notExistingTimeId, themeId);
        ReservationPayRequest request = new ReservationPayRequest(reservationSaveRequest, paymentConfirmRequest);

        BDDMockito.willThrow(new NotFoundException("해당 ID의 예약 시간이 없습니다."))
                .given(reservationFactory)
                .create(anyLong(), anyLong(), anyLong(), any(), any());

        // when & then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(COOKIE)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andDo(document("reservations/create/fail/time-not-found"));
    }

    @Test
    @DisplayName("존재하지 않는 테마의 예약 POST 요청 시 상태코드 404를 반환한다.")
    void createReservationWithNotExistingTheme() throws Exception {
        // given
        Long timeId = 1L;
        Long notExistingThemeId = 1L;
        ReservationSaveRequest reservationSaveRequest = new ReservationSaveRequest(MIA_RESERVATION_DATE, timeId, notExistingThemeId);
        ReservationPayRequest request = new ReservationPayRequest(reservationSaveRequest, paymentConfirmRequest);

        BDDMockito.willThrow(new NotFoundException("해당 ID의 테마가 없습니다."))
                .given(reservationFactory)
                .create(anyLong(), anyLong(), anyLong(), any(), any());

        // when & then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(COOKIE)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists())
                .andDo(document("reservations/create/fail/theme-not-found"));
    }

    @Test
    @DisplayName("사용자의 예약 목록 GET 요청 시 상태코드 200을 반환한다.")
    void findMyReservations() throws Exception {
        // given
        ReservationTime expectedTime = new ReservationTime(1L, MIA_RESERVATION_TIME);
        Reservation expectedReservation = MIA_RESERVATION(expectedTime, WOOTECO_THEME(), USER_MIA(), BOOKING);
        Payment expectedPayment = new Payment("paymentKey", "orderId", 10L, expectedReservation);
        ReservationPayment expectedReservationPayment = new ReservationPayment(expectedReservation, expectedPayment);
        WaitingReservation expectedWaitingReservation = new WaitingReservation(
                MIA_RESERVATION(expectedTime, HORROR_THEME(), USER_MIA(), WAITING), 0);
        List<MyReservationResponse> myReservations = List.of(
                MyReservationResponse.from(expectedReservationPayment),
                MyReservationResponse.from(expectedWaitingReservation)
        );

        BDDMockito.given(reservationQueryService.findAllMyReservations(any()))
                .willReturn(myReservations);

        // when & then
        mockMvc.perform(get("/reservations/mine").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].theme").value(WOOTECO_THEME_NAME))
                .andExpect(jsonPath("$[0].date").value(MIA_RESERVATION_DATE.toString()))
                .andExpect(jsonPath("$[0].time").value(MIA_RESERVATION_TIME.toString()))
                .andExpect(jsonPath("$[0].status").value("예약"))
                .andExpect(jsonPath("$[0].paymentKey").value("paymentKey"))
                .andExpect(jsonPath("$[0].amount").value(10L))
                .andExpect(jsonPath("$[1].theme").value(HORROR_THEME_NAME))
                .andExpect(jsonPath("$[1].status").value("1번째 예약대기"))
                .andDo(document("reservations/find-mine/success"));
    }

    @Test
    @DisplayName("사용자 대기 예약 DELETE 요청 시 상태코드 204를 반환한다.")
    void deleteMyWaitingReservation() throws Exception {
        // given
        BDDMockito.willDoNothing()
                .given(waitingManageService)
                .delete(1L, STUBBED_LOGIN_MEMBER);

        // when & then
        mockMvc.perform(delete("/reservations/{id}/waiting", 1L)
                        .cookie(COOKIE))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document("reservations/delete/success"));
    }

    @Test
    @DisplayName("예약자가 아닌 사용자가 대기 예약 DELETE 요청 시 상태코드 400을 반환한다.")
    void deleteMyWaitingReservationWithoutOwnerShip() throws Exception {
        // given
        BDDMockito.willThrow(new ViolationException("대기 예약을 삭제할 권한이 없습니다. 예약자 혹은 관리자만 삭제할 수 있습니다."))
                .given(waitingManageService)
                .delete(1L, STUBBED_LOGIN_MEMBER);

        // when & then
        mockMvc.perform(delete("/reservations/{id}/waiting", 1L)
                        .cookie(COOKIE))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists())
                .andDo(document("reservations/delete/fail/permission"));
    }
}
