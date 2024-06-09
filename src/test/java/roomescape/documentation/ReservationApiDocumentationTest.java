package roomescape.documentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import roomescape.controller.api.ReservationController;
import roomescape.controller.dto.request.ReservationRequest;
import roomescape.service.ReservationPaymentFacadeService;
import roomescape.service.ReservationQueryService;
import roomescape.service.dto.response.PersonalReservationResponse;
import roomescape.service.dto.response.ReservationResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ReservationApiDocumentationTest extends BaseDocumentationTest {

    private final ReservationPaymentFacadeService reservationPaymentFacadeService = Mockito.mock(ReservationPaymentFacadeService.class);
    private final ReservationQueryService reservationQueryService = Mockito.mock(ReservationQueryService.class);

    @Test
    @DisplayName("조건을 필터링한 예약들을 조회한다.")
    void getReservationsByConditions() throws Exception {
        when(reservationQueryService.getReservationsByConditions(any(), any(), any(), any()))
                .thenReturn(List.of(
                        new ReservationResponse(1L, LocalDate.parse("2024-06-10"), "프린", LocalTime.parse("10:00"), "테마명1"),
                        new ReservationResponse(2L, LocalDate.parse("2024-06-11"), "프린", LocalTime.parse("11:00"), "테마명1"),
                        new ReservationResponse(3L, LocalDate.parse("2024-06-12"), "프린", LocalTime.parse("12:00"), "테마명1")
                ));

        mockMvc.perform(get("/reservations")
                        .cookie(adminCookie)
                        .param("memberId", "1")
                        .param("themeId", "1")
                        .param("dateFrom", "2024-06-10")
                        .param("dateTo", "2024-06-12")
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reservation/conditions",
                        queryParameters(
                                parameterWithName("memberId").description("회원 id").optional(),
                                parameterWithName("themeId").description("테마 id").optional(),
                                parameterWithName("dateFrom").description("조회 시작 날짜").optional(),
                                parameterWithName("dateTo").description("조회 종료 날짜").optional()
                        ),
                        responseFields(
                                fieldWithPath("list.[].id").description("예약 id"),
                                fieldWithPath("list.[].date").description("예약 날짜"),
                                fieldWithPath("list.[].name").description("예약자명"),
                                fieldWithPath("list.[].startAt").description("예약 시간"),
                                fieldWithPath("list.[].theme").description("테마명")
                        )

                ));
    }

    @Test
    @DisplayName("나의 예약을 조회한다.")
    void getMyReservations() throws Exception {
        when(reservationQueryService.getMyReservations(any()))
                .thenReturn(List.of(
                        new PersonalReservationResponse(1L, LocalDate.parse("2024-06-10"), LocalTime.parse("10:00"), "테마명1", "예약", "testPaymentKey", BigDecimal.valueOf(10000), "토스페이", null, null, null),
                        new PersonalReservationResponse(2L, LocalDate.parse("2024-06-11"), LocalTime.parse("11:00"), "테마명2", "예약", null, BigDecimal.valueOf(20000), "계좌이체", "1111-2222-333", "프린", "우아한은행"),
                        new PersonalReservationResponse(1L, LocalDate.parse("2024-06-12"), LocalTime.parse("12:00"), "테마명3", "예약대기", null, null, null, null, null, null)
                ));

        mockMvc.perform(get("/reservations/mine")
                        .cookie(memberCookie)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("reservation/mine",
                        responseFields(
                                fieldWithPath("list.[].id").description("예약 id 또는 예약 대기 id"),
                                fieldWithPath("list.[].date").description("예약 날짜"),
                                fieldWithPath("list.[].time").description("예약 시간"),
                                fieldWithPath("list.[].theme").description("테마명"),
                                fieldWithPath("list.[].status").description("예약 상태 (예약 or 예약대기)"),
                                fieldWithPath("list.[].paymentKey").description("결제 키 (결제 수단이 계좌이체가 아닐 경우 필수)").optional(),
                                fieldWithPath("list.[].amount").description("결제 금액 (예약 상태가 예약일 경우 필수)").optional(),
                                fieldWithPath("list.[].paymentType").description("결제 수단 (예약 상태가 예약일 경우 필수)").optional(),
                                fieldWithPath("list.[].accountNumber").description("계좌번호 (결제 수단이 계좌이체일 경우 필수)").optional(),
                                fieldWithPath("list.[].accountHolder").description("예금주 (결제 수단이 계좌이체일 경우 필수)").optional(),
                                fieldWithPath("list.[].bankName").description("은행명 (결제 수단이 계좌이체일 경우 필수)").optional()
                        )
                ));
    }

    @Test
    @DisplayName("예약을 생성한다.")
    void addReservation() throws Exception {
        ReservationResponse response = new ReservationResponse(1L, LocalDate.parse("2024-06-10"), "프린", LocalTime.parse("10:00"), "테마명");
        when(reservationPaymentFacadeService.addReservation(any()))
                .thenReturn(response);

        ReservationRequest request = new ReservationRequest(LocalDate.parse("2024-06-10"), 1L, 1L, "testPaymentKey", "orderId", BigDecimal.valueOf(10000));
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(memberCookie)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("reservation/create",
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 id"),
                                fieldWithPath("themeId").description("테마 id"),
                                fieldWithPath("paymentKey").description("결제 키"),
                                fieldWithPath("orderId").description("주문 id"),
                                fieldWithPath("amount").description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 id"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("name").description("예약자명"),
                                fieldWithPath("startAt").description("예약 시간"),
                                fieldWithPath("theme").description("테마명")
                        )
                ));
    }

    @Test
    @DisplayName("예약을 취소한다.")
    void cancelReservation() throws Exception {
        doNothing().when(reservationPaymentFacadeService)
                .cancelReservation(any());

        mockMvc.perform(delete("/reservations/{id}", 1)
                        .cookie(memberCookie)
                        .content(MediaType.APPLICATION_JSON_VALUE)
                        .param("reason", "cancel reason")
                )
                .andExpect(status().isNoContent())
                .andDo(document("reservation/cancel",
                        queryParameters(
                                parameterWithName("reason").description("취소 사유")
                        ),
                        pathParameters(
                                parameterWithName("id").description("예약 id")
                        )
                ));
    }

    @Override
    Object controller() {
        return new ReservationController(reservationPaymentFacadeService, reservationQueryService);
    }
}
