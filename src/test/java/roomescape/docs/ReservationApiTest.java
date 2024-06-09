package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.docs.ApiDocumentUtils.getDocumentRequest;
import static roomescape.docs.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.controller.TokenCookieManager;
import roomescape.auth.dto.LoggedInMember;
import roomescape.auth.service.AuthService;
import roomescape.config.handler.AuthenticationArgumentResolver;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.controller.ReservationController;
import roomescape.reservation.domain.PaymentStatus;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.MyReservationWithPaymentResponse;
import roomescape.reservation.dto.PendingReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.UserReservationCreateRequest;
import roomescape.reservation.service.ReservationDeleteService;
import roomescape.reservation.service.ReservationFindService;
import roomescape.reservation.service.ReservationPayService;
import roomescape.theme.dto.ThemeResponse;
import roomescape.time.dto.TimeResponse;

@WebMvcTest(controllers = ReservationController.class)
@ExtendWith(RestDocumentationExtension.class)
class ReservationApiTest {
    private static final ReservationResponse RESPONSE1 = new ReservationResponse(
            1L, new MemberResponse(1L, "브라운"),
            LocalDate.of(2024, 8, 15),
            new TimeResponse(1L, LocalTime.of(19, 0)),
            new ThemeResponse(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"));
    private static final ReservationResponse RESPONSE2 = new ReservationResponse(
            2L, new MemberResponse(2L, "브리"),
            LocalDate.of(2024, 8, 20),
            new TimeResponse(1L, LocalTime.of(19, 0)),
            new ThemeResponse(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"));
    private static final MyReservationWithPaymentResponse RESERVATION_WITH_PAYMENT_RESPONSE1 = new MyReservationWithPaymentResponse(
            1L,
            "레벨2 탈출",
            LocalDate.of(2024, 8, 15),
            LocalTime.of(19, 0),
            "예약완료",
            1L,
            PaymentStatus.COMPLETED,
            "testPaymentKey", BigDecimal.ONE);
    private static final MyReservationWithPaymentResponse RESERVATION_WITH_PAYMENT_RESPONSE2 = new MyReservationWithPaymentResponse(
            2L,
            "레벨2 탈출",
            LocalDate.of(2024, 8, 20),
            LocalTime.of(19, 0),
            "예약대기",
            2L,
            PaymentStatus.PENDING,
            "testPaymentKey", BigDecimal.ONE);

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private ReservationPayService reservationPayService;
    @MockBean
    private ReservationFindService reservationFindService;
    @MockBean
    private ReservationDeleteService reservationDeleteService;
    @MockBean
    private AuthenticationArgumentResolver authenticationArgumentResolver;
    @MockBean
    private AuthService authService;
    @MockBean
    private TokenCookieManager tokenCookieManager;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("모든 예약 조회")
    @Test
    void findReservations() throws Exception {
        List<ReservationResponse> responses = List.of(RESPONSE1, RESPONSE2);

        given(reservationFindService.findReservations())
                .willReturn(responses);

        ResultActions result = mockMvc.perform(get("/reservations"));

        result.andExpect(status().isOk())
                .andDo(document("reservations/find-reservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").description("예약의 id"),
                                fieldWithPath("[].member.id").description("멤버의 id"),
                                fieldWithPath("[].member.name").description("멤버의 이름"),
                                fieldWithPath("[].date").description("날짜"),
                                fieldWithPath("[].time.id").description("시간의 id"),
                                fieldWithPath("[].time.startAt").description("시간"),
                                fieldWithPath("[].theme.id").description("테마의 id"),
                                fieldWithPath("[].theme.name").description("테마의 이름"),
                                fieldWithPath("[].theme.description").description("테마의 설명"),
                                fieldWithPath("[].theme.thumbnail").description("테마의 썸네일")
                        )
                ));
    }

    @DisplayName("사용자 예약 찾기")
    @Test
    void findMyReservationsTest() throws Exception {
        LoggedInMember loggedInMember = new LoggedInMember(1L, "testMember", "test@email.com", true);
        Cookie cookie = new Cookie("token", "testToken");
        List<MyReservationWithPaymentResponse> responses = List.of(RESERVATION_WITH_PAYMENT_RESPONSE1,
                RESERVATION_WITH_PAYMENT_RESPONSE2);

        given(authenticationArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(loggedInMember);
        given(reservationPayService.findMyReservationsWithPayment(any()))
                .willReturn(responses);

        ResultActions result = mockMvc.perform(get("/reservations/accounts")
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document("reservations/find-MineReservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").description("예약의 id"),
                                fieldWithPath("[].themeName").description("테마의 이름"),
                                fieldWithPath("[].date").description("날짜"),
                                fieldWithPath("[].startAt").description("시간"),
                                fieldWithPath("[].status").description("예약 상태"),
                                fieldWithPath("[].waitingId").description("대기의 id"),
                                fieldWithPath("[].paymentStatus").description("결제 상태"),
                                fieldWithPath("[].paymentKey").description("예약 key"),
                                fieldWithPath("[].amount").description("가격")
                        )
                ));
    }

    @DisplayName("예약 생성")
    @Test
    void createReservationTest() throws Exception {
        LoggedInMember loggedInMember = new LoggedInMember(1L, "testMember", "test@email.com", true);
        Cookie cookie = new Cookie("token", "testToken");
        UserReservationCreateRequest userReservationCreateRequest = new UserReservationCreateRequest(
                LocalDate.of(2024, 8, 20),
                1L,
                1L,
                "paymentKey",
                "orderId",
                BigDecimal.ONE);

        given(authenticationArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(loggedInMember);
        given(reservationPayService.createReservation(any(), any()))
                .willReturn(RESPONSE1);

        ResultActions result = mockMvc.perform(post("/reservations")
                .cookie(cookie)
                .content(mapper.writeValueAsString(userReservationCreateRequest))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(document("reservations/create-reservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("date").description("날짜"),
                                fieldWithPath("themeId").description("테마의 id"),
                                fieldWithPath("timeId").description("시간의 id"),
                                fieldWithPath("paymentKey").description("결제 key"),
                                fieldWithPath("orderId").description("주문 id"),
                                fieldWithPath("amount").description("가격")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약의 id"),
                                fieldWithPath("member.id").description("멤버의 id"),
                                fieldWithPath("member.name").description("테마의 이름"),
                                fieldWithPath("date").description("날짜"),
                                fieldWithPath("time.id").description("테마의 아이디"),
                                fieldWithPath("time.startAt").description("시간"),
                                fieldWithPath("theme.id").description("테마의 id"),
                                fieldWithPath("theme.name").description("테마의 이름"),
                                fieldWithPath("theme.description").description("테마의 설명"),
                                fieldWithPath("theme.thumbnail").description("테마의 썸네일")
                        )
                ));
    }

    @DisplayName("예약에 대한 결제를 한다.")
    @Test
    void methocreatePaymentWithPendingReservationTest() throws Exception {
        LoggedInMember loggedInMember = new LoggedInMember(1L, "testMember", "test@email.com", true);
        Cookie cookie = new Cookie("token", "testToken");
        PendingReservationPaymentRequest request = new PendingReservationPaymentRequest(
                "paymentKey",
                "orderId",
                BigDecimal.ONE);

        MyReservationResponse response = new MyReservationResponse(1L,
                "레벨2 탈출",
                LocalDate.of(2024, 8, 20),
                LocalTime.of(12, 12),
                PaymentStatus.COMPLETED, "예약완료",
                1L);

        given(authenticationArgumentResolver.supportsParameter(any()))
                .willReturn(true);
        given(authenticationArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .willReturn(loggedInMember);
        given(reservationPayService.updateReservationPayment(any(), any(), any()))
                .willReturn(response);

        ResultActions result = mockMvc.perform(post("/reservations/1/payment")
                .cookie(cookie)
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(
                        document("reservations/payment",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("paymentKey").description("결제 key"),
                                        fieldWithPath("orderId").description("주문 id"),
                                        fieldWithPath("amount").description("가격")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("예약의 id"),
                                        fieldWithPath("themeName").description("테마의 이름"),
                                        fieldWithPath("date").description("날짜"),
                                        fieldWithPath("startAt").description("시간"),
                                        fieldWithPath("paymentStatus").description("결제 상태"),
                                        fieldWithPath("status").description("예약 상태"),
                                        fieldWithPath("waitingId").description("대기의 아이디")
                                )
                ));
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void deleteTest() throws Exception {
        doNothing().when(reservationDeleteService).deleteReservation(any());
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/reservations/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent())
                .andDo(
                        document("reservations/delete",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(parameterWithName("id").description("예약의 id"))
                        ));
    }
}



