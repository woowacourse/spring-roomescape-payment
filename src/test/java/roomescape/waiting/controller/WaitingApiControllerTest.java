package roomescape.waiting.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.AbstractRestDocsTests;
import roomescape.config.WebMvcTestConfig;
import roomescape.global.exception.NotFoundException;
import roomescape.member.controller.response.MemberResponse;
import roomescape.reservation.controller.request.ReserveByUserRequest;
import roomescape.reservation.controller.response.ReservationResponse;
import roomescape.reservation.exception.InAlreadyReservationException;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.controller.response.ThemeResponse;
import roomescape.time.controller.response.ReservationTimeResponse;
import roomescape.waiting.exception.InAlreadyWaitingException;
import roomescape.waiting.service.WaitingService;

@WebMvcTest(controllers = WaitingApiController.class)
@Import({WebMvcTestConfig.class})
class WaitingApiControllerTest extends AbstractRestDocsTests {

    @MockitoBean
    private WaitingService waitingService;

    @MockitoBean
    private ReservationService reservationService;


    @Test
    void 예약_대기를_생성한다() throws Exception {
        MemberResponse memberResponse = new MemberResponse(1L, "user@email.com", "유저1");
        ReserveByUserRequest reserveByUserRequest = new ReserveByUserRequest(LocalDate.of(2024, 6, 1), 1L, 1L);
        ReservationResponse reservationResponse = new ReservationResponse(1L, memberResponse, LocalDate.of(2024, 6, 1),
                new ReservationTimeResponse(1L, java.time.LocalTime.of(10, 0)),
                new ThemeResponse(1L, "공포", "공포테마", "url"));
        given(reservationService.waiting(any())).willReturn(reservationResponse);

        mockMvc.perform(post("/reservations/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveByUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1L))
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("themeId").description("테마 ID"),
                                fieldWithPath("timeId").description("예약 시간 ID")
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
    void 이미_예약한_사람이면_409_상태_코드를_반환한다() throws Exception {
        ReserveByUserRequest reserveByUserRequest = new ReserveByUserRequest(LocalDate.now(), 1L, 1L);
        given(reservationService.waiting(any()))
                .willThrow(new InAlreadyReservationException("이미 예약한 사용자입니다."));

        mockMvc.perform(post("/reservations/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveByUserRequest)))
                .andExpect(status().isConflict())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("themeId").description("테마 ID"),
                                fieldWithPath("timeId").description("예약 시간 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));
    }

    @Test
    void 이미_예약_대기를_했다면_409_상태_코드를_반환한다() throws Exception {
        ReserveByUserRequest reserveByUserRequest = new ReserveByUserRequest(LocalDate.now(), 1L, 1L);
        given(reservationService.waiting(any()))
                .willThrow(new InAlreadyWaitingException("이미 예약 대기를 한 사용자입니다."));

        mockMvc.perform(post("/reservations/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reserveByUserRequest)))
                .andExpect(status().isConflict())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("themeId").description("테마 ID"),
                                fieldWithPath("timeId").description("예약 시간 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("에러 코드"),
                                fieldWithPath("message").description("에러 메시지")
                        )
                ));
    }

    @Test
    void 잘못된_입력값이면_400_상태_코드와_에러코드를_반환한다() throws Exception {
        ReserveByUserRequest reserveByUserRequest = new ReserveByUserRequest(null, 1L, 1L);

        mockMvc.perform(post("/reservations/waiting")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reserveByUserRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("GF002"))
                .andExpect(jsonPath("$.message").value("잘못된 인자입니다."));
    }

    @Test
    void 예약_대기를_취소할_수_있다() throws Exception {
        mockMvc.perform(delete("/reservations/waiting/{id}", 1L))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("id").description("예약 대기 ID")
                        )
                ));
    }

    @Test
    void 존재하지_않는_예약_대기_취소시_예외가_발생한다() throws Exception {
        doThrow(new NotFoundException("예약 대기를 찾을 수 없습니다."))
                .when(waitingService).cancelWaiting(any(), any());

        mockMvc.perform(delete("/reservations/waiting/{id}", 999L))
                .andExpect(status().isNotFound());
    }
}
