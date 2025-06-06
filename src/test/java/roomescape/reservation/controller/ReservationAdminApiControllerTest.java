package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.AbstractRestDocsTests;
import roomescape.global.exception.NotFoundException;
import roomescape.member.controller.response.MemberResponse;
import roomescape.reservation.controller.request.ReserveByAdminRequest;
import roomescape.reservation.controller.response.ReservationResponse;
import roomescape.reservation.exception.InAlreadyReservationException;
import roomescape.reservation.exception.PastReservationException;
import roomescape.reservation.service.ReservationQueryService;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.controller.response.ThemeResponse;
import roomescape.time.controller.response.ReservationTimeResponse;

@WebMvcTest(controllers = ReservationAdminApiController.class)
class ReservationAdminApiControllerTest extends AbstractRestDocsTests {

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private ReservationQueryService reservationQueryService;

    @Test
    void 어드민이_예약을_생성할_수_있다() throws Exception {
        ReserveByAdminRequest request = new ReserveByAdminRequest(
                LocalDate.of(2024, 6, 1), 1L, 1L, 1L
        );
        ReservationResponse response = new ReservationResponse(
                1L,
                new MemberResponse(1L, "user@email.com", "유저1"),
                LocalDate.of(2024, 6, 1),
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ThemeResponse(1L, "공포", "공포테마", "url")
        );
        given(reservationService.reserve(any())).willReturn(response);

        mockMvc.perform(post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("themeId").description("테마 ID"),
                                fieldWithPath("timeId").description("예약 시간 ID"),
                                fieldWithPath("memberId").description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.id").description("예약 ID"),
                                fieldWithPath("data.member.id").description("회원 ID"),
                                fieldWithPath("data.member.email").description("회원 이메일"),
                                fieldWithPath("data.member.name").description("회원 이름"),
                                fieldWithPath("data.date").description("예약 날짜"),
                                fieldWithPath("data.time.id").description("예약 시간 ID"),
                                fieldWithPath("data.time.startAt").description("예약 시작 시간"),
                                fieldWithPath("data.theme.id").description("테마 ID"),
                                fieldWithPath("data.theme.name").description("테마 이름"),
                                fieldWithPath("data.theme.description").description("테마 설명"),
                                fieldWithPath("data.theme.thumbnail").description("테마 썸네일")
                        )
                ));
    }

    @Test
    void 지난_날짜_예약시_예외가_발생한다() throws Exception {
        ReserveByAdminRequest request = new ReserveByAdminRequest(
                LocalDate.of(2020, 1, 1), 1L, 1L, 1L
        );
        given(reservationService.reserve(any())).willThrow(new PastReservationException());

        mockMvc.perform(post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.code").value("RSF001"))
                .andExpect(jsonPath("$.message").value("지난 날짜에는 예약할 수 없습니다."));
    }

    @Test
    void 이미_예약된_시간이면_예외가_발생한다() throws Exception {
        ReserveByAdminRequest request = new ReserveByAdminRequest(
                LocalDate.of(2024, 6, 1), 1L, 1L, 1L
        );
        given(reservationService.reserve(any())).willThrow(new InAlreadyReservationException("이미 예약된 시간입니다."));

        mockMvc.perform(post("/admin/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("RSF002"))
                .andExpect(jsonPath("$.message").value("이미 예약된 상태입니다."));
    }

    @Test
    void 어드민이_예약_목록을_조회할_수_있다() throws Exception {
        ReservationResponse response = new ReservationResponse(
                1L,
                new MemberResponse(1L, "user@email.com", "유저1"),
                LocalDate.of(2024, 6, 1),
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ThemeResponse(1L, "공포", "공포테마", "url")
        );

        given(reservationQueryService.getFilteredReserved(any(), any(), any(), any(), any()))
                .willReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/admin/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("data.content.size()").value(1))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.content[].id").description("예약 ID"),
                                fieldWithPath("data.content[].member.id").description("회원 ID"),
                                fieldWithPath("data.content[].member.email").description("회원 이메일"),
                                fieldWithPath("data.content[].member.name").description("회원 이름"),
                                fieldWithPath("data.content[].date").description("예약 날짜"),
                                fieldWithPath("data.content[].time.id").description("예약 시간 ID"),
                                fieldWithPath("data.content[].time.startAt").description("예약 시작 시간"),
                                fieldWithPath("data.content[].theme.id").description("테마 ID"),
                                fieldWithPath("data.content[].theme.name").description("테마 이름"),
                                fieldWithPath("data.content[].theme.description").description("테마 설명"),
                                fieldWithPath("data.content[].theme.thumbnail").description("테마 썸네일"),
                                fieldWithPath("data.page").description("페이지 번호"),
                                fieldWithPath("data.size").description("페이지 크기"),
                                fieldWithPath("data.totalPages").description("전체 페이지 수"),
                                fieldWithPath("data.totalElements").description("전체 예약 수")
                        )
                ));
    }

    @Test
    void 어드민이_예약을_삭제할_수_있다() throws Exception {
        mockMvc.perform(delete("/admin/reservations/{id}", 1L))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("id").description("예약 ID")
                        )
                ));
    }

    @Test
    void 존재하지_않는_예약_삭제시_예외가_발생한다() throws Exception {
        doThrow(new NotFoundException("예약을 찾을 수 없습니다."))
                .when(reservationService).delete(any());

        mockMvc.perform(delete("/admin/reservations/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
