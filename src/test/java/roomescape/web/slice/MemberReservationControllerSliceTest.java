package roomescape.web.slice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.web.ApiDocumentUtils.getDocumentRequest;
import static roomescape.web.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import roomescape.application.CancelService;
import roomescape.application.ReservationService;
import roomescape.application.dto.request.reservation.ReservationPaymentRequest;
import roomescape.application.dto.request.reservation.UserReservationRequest;
import roomescape.application.dto.response.member.MemberResponse;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.application.dto.response.reservation.UserReservationResponse;
import roomescape.application.dto.response.theme.ThemeResponse;
import roomescape.application.dto.response.time.ReservationTimeResponse;
import roomescape.domain.reservation.Status;
import roomescape.support.mock.MockLoginMemberArgumentResolver;
import roomescape.web.api.MemberReservationController;
import roomescape.web.config.AdminHandlerInterceptor;
import roomescape.web.config.LoginMemberArgumentResolver;

@AutoConfigureRestDocs(outputDir = "build/generated-snippets", uriScheme = "https", uriHost = "docs.api.com")
@ExtendWith(RestDocumentationExtension.class)
@WebMvcTest(MemberReservationController.class)
class MemberReservationControllerSliceTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private CancelService cancelService;

    @MockBean
    private AdminHandlerInterceptor interceptor;

    @MockBean
    private LoginMemberArgumentResolver resolver;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

    @BeforeEach
    public void setMockMvc(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(new MemberReservationController(reservationService, cancelService))
                .setCustomArgumentResolvers(new MockLoginMemberArgumentResolver())
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    private final ReservationTimeResponse time1 = new ReservationTimeResponse(1L, LocalTime.of(11, 0));
    private final ReservationTimeResponse time2 = new ReservationTimeResponse(2L, LocalTime.of(21, 0));
    private final ThemeResponse theme1 = new ThemeResponse(1L, "테마 이름", "설명", "썸네일.jpg");
    private final MemberResponse member1 = new MemberResponse(1L, "재즈");
    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    @DisplayName("예약 생성")
    @Test
    void makeReservation() throws Exception {
        UserReservationRequest request = new UserReservationRequest(
                tomorrow, 1L, 1L, 1000, "orderId", "paymentKey"
        );
        ReservationResponse response = new ReservationResponse(1L, tomorrow, time1, theme1, member1, Status.RESERVED);

        given(reservationService.saveReservation(any(), anyLong())).willReturn(response);

        ResultActions result = mockMvc.perform(post("/reservations")
                .header(HttpHeaders.COOKIE, "token=jwtToken")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
        );

        result.andExpect(status().isCreated())
                .andDo(document("/member/makeReservation",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 아이디"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING)
                                        .description("예약 시작 시간 (HH:mm)"),
                                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("member.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("예약 상태")
                        ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("등록된 리소스 URI")
                        ))
                );
    }

    @DisplayName("결제 요청")
    @Test
    void paymentForPending() throws Exception {
        ReservationPaymentRequest request = new ReservationPaymentRequest(
                1L, 1000, "orderId", "paymentKey");

        ReservationResponse response = new ReservationResponse(1L, tomorrow, time1, theme1, member1, Status.RESERVED);

        given(reservationService.paymentForPending(any(), anyLong())).willReturn(response);

        ResultActions result = mockMvc.perform(post("/reservations/payment")
                .header(HttpHeaders.COOKIE, "token=jwtToken")
                .contentType("application/json")
                .content(objectMapper.writeValueAsBytes(request)));

        result.andExpect(status().isOk())
                .andDo(document("/member/paymentForPending",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        requestFields(
                                fieldWithPath("reservationId").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 아이디"),
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("예약 시간 아이디"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING)
                                        .description("예약 시작 시간 (HH:mm)"),
                                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 아이디"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("회원 아이디"),
                                fieldWithPath("member.name").type(JsonFieldType.STRING).description("회원 이름"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("예약 상태")
                        ))
                );
    }

    @DisplayName("내 예약 조회")
    @Test
    void findAllMyReservations() throws Exception {
        List<UserReservationResponse> response = List.of(
                new UserReservationResponse(
                        1L, theme1.name(), tomorrow, time1.startAt(), "RESERVED",
                        "paymentKey", 1000, 1),
                new UserReservationResponse(
                        5L, theme1.name(), tomorrow, time2.startAt(), "WAITING",
                        "paymentKEy", 1000, 3)
        );

        given(reservationService.findAllWithRank(anyLong())).willReturn(response);

        ResultActions result = mockMvc.perform(get("/reservations-mine")
                .header(HttpHeaders.COOKIE, "token=jwtToken"));

        result.andExpect(status().isOk())
                .andDo(document("/member/findAllMyReservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        responseFields(
                                fieldWithPath("[].reservationId").type(JsonFieldType.NUMBER).description("예약 아이디"),
                                fieldWithPath("[].themeName").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("[].time").type(JsonFieldType.STRING).description("예약 시작 시간 (HH:mm)"),
                                fieldWithPath("[].status").type(JsonFieldType.STRING).description("예약 상태"),
                                fieldWithPath("[].paymentKey").type(JsonFieldType.STRING).description("결제 키"),
                                fieldWithPath("[].amount").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("[].waitingRank").type(JsonFieldType.NUMBER).description("대기 순위")
                        ))
                );
    }

    @DisplayName("대기 취소")
    @Test
    void cancelWaiting() throws Exception {
        Long waitingId = 1L;

        ResultActions result = mockMvc.perform(delete("/waitings/{idWaiting}", waitingId)
                .header(HttpHeaders.COOKIE, "token=jwtToken"));

        result.andExpect(status().isNoContent())
                .andDo(document("/member/cancelWaiting",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("idWaiting").description("예약 아이디")
                        ))
                );
    }

    @DisplayName("예약 취소")
    @Test
    void cancelReservation() throws Exception {
        Long reservationId = 1L;

        ResultActions result = mockMvc.perform(delete("/reservations/{idReservation}", reservationId)
                .header(HttpHeaders.COOKIE, "token=jwtToken"));

        result.andExpect(status().isNoContent())
                .andDo(document("/member/cancelReservation",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName(HttpHeaders.COOKIE).description("JWT 토큰")
                        ),
                        pathParameters(
                                parameterWithName("idReservation").description("예약 아이디")
                        ))
                );
    }
}
