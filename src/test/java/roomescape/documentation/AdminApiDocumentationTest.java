package roomescape.documentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import roomescape.controller.api.AdminController;
import roomescape.controller.dto.request.AdminReservationRequest;
import roomescape.controller.dto.request.WaitingToReservationRequest;
import roomescape.service.ReservationManageService;
import roomescape.service.ReservationWaitingService;
import roomescape.service.dto.response.ReservationResponse;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminApiDocumentationTest extends BaseDocumentationTest {
    private final ReservationManageService reservationManageService = Mockito.mock(ReservationManageService.class);
    private final ReservationWaitingService reservationWaitingService = Mockito.mock(ReservationWaitingService.class);

    @Test
    @DisplayName("예약을 추가한다")
    void addAdminReservation() throws Exception {
        ReservationResponse response = new ReservationResponse(1L, LocalDate.parse("2024-06-10"), "prin", LocalTime.parse("10:00"), "테마명");
        when(reservationManageService.addReservationByAdmin(any()))
                .thenReturn(response);
        AdminReservationRequest request = new AdminReservationRequest(LocalDate.parse("2024-06-10"), 1L, 1L, 1L);
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("admin/reservation/create",
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 id"),
                                fieldWithPath("themeId").description("테마 id"),
                                fieldWithPath("memberId").description("결제 금액")
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
    @DisplayName("예약 대기를 승인한다")
    void acceptReservationWaiting() throws Exception {
        ReservationResponse response = new ReservationResponse(1L, LocalDate.parse("2024-06-10"), "prin", LocalTime.parse("10:00"), "테마명");
        when(reservationWaitingService.approveReservationWaiting(any()))
                .thenReturn(response);
        WaitingToReservationRequest request = new WaitingToReservationRequest("111-2222-3333", "프린", "국민은행", BigDecimal.valueOf(10000));
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/admin/waitings/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("admin/waiting/approve",
                        pathParameters(
                                parameterWithName("id").description("예약 대기 id")
                        ),
                        requestFields(
                                fieldWithPath("accountNumber").description("계좌번호"),
                                fieldWithPath("accountHolder").description("예금주"),
                                fieldWithPath("bankName").description("은행명"),
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
    @DisplayName("예약 대기 목록을 조회한다")
    void getReservationWaitings() throws Exception {
        when(reservationWaitingService.getReservationWaitings())
                .thenReturn(List.of(
                        new ReservationResponse(1L, LocalDate.parse("2024-06-10"), "prin", LocalTime.parse("10:00"), "테마명1"),
                        new ReservationResponse(2L, LocalDate.parse("2024-06-11"), "prin", LocalTime.parse("11:00"), "테마명2")
                ));

        mockMvc.perform(get("/admin/waitings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie)
                )
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("admin/waiting/findAll",
                        responseFields(
                                fieldWithPath("list.[].id").description("예약 대기 id"),
                                fieldWithPath("list.[].date").description("예약 날짜"),
                                fieldWithPath("list.[].name").description("예약 대기자명"),
                                fieldWithPath("list.[].startAt").description("예약 시간"),
                                fieldWithPath("list.[].theme").description("테마명")
                        )
                ));
    }

    @Test
    @DisplayName("예약 대기를 거절한다")
    void deleteReservationWaiting() throws Exception {
        doNothing().when(reservationWaitingService)
                .deleteReservationWaiting(anyLong(), any());

        mockMvc.perform(delete("/admin/waitings/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie)
                )
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("admin/waiting/deny",
                        pathParameters(
                                parameterWithName("id").description("예약 대기 id")
                        )
                ));
    }

    @Override
    Object controller() {
        return new AdminController(reservationManageService, reservationWaitingService);
    }
}
