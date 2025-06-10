package roomescape.time.controller;

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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.AbstractRestDocsTests;
import roomescape.global.exception.NotFoundException;
import roomescape.time.controller.request.ReservationTimeCreateRequest;
import roomescape.time.controller.response.AvailableReservationTimeResponse;
import roomescape.time.controller.response.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@WebMvcTest(controllers = ReservationTimeApiController.class)
class ReservationTimeApiControllerTest extends AbstractRestDocsTests {

    @MockitoBean
    private ReservationTimeService reservationTimeService;

    @Test
    void 예약_시간_목록을_조회할_수_있다() throws Exception {
        ReservationTimeResponse response = new ReservationTimeResponse(1L, LocalTime.of(10, 0));
        given(reservationTimeService.getAll()).willReturn(List.of(response));

        mockMvc.perform(get("/times"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data[].id").description("예약 시간 ID"),
                                fieldWithPath("data[].startAt").description("예약 시작 시간")
                        )
                ));
    }

    @Test
    void 예약_시간을_생성할_수_있다() throws Exception {
        ReservationTimeCreateRequest request = new ReservationTimeCreateRequest(LocalTime.of(10, 0));
        ReservationTimeResponse response = new ReservationTimeResponse(1L, LocalTime.of(10, 0));
        given(reservationTimeService.open(any())).willReturn(response);

        mockMvc.perform(post("/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("startAt").description("예약 시작 시간")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data.id").description("예약 시간 ID"),
                                fieldWithPath("data.startAt").description("예약 시작 시간")
                        )
                ));
    }

    @Test
    void 예약_시간_생성시_유효성_예외가_발생한다() throws Exception {
        ReservationTimeCreateRequest request = new ReservationTimeCreateRequest(null);

        mockMvc.perform(post("/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 예약_시간을_삭제할_수_있다() throws Exception {
        mockMvc.perform(delete("/times/{id}", 1L))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("id").description("예약 시간 ID")
                        )
                ));
    }

    @Test
    void 존재하지_않는_예약_시간_삭제시_예외가_발생한다() throws Exception {
        doThrow(new NotFoundException("예약 시간을 찾을 수 없습니다."))
                .when(reservationTimeService).deleteById(any());

        mockMvc.perform(delete("/times/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    void 예약_가능한_시간_목록을_조회할_수_있다() throws Exception {
        AvailableReservationTimeResponse response = new AvailableReservationTimeResponse(1L, LocalTime.of(10, 0),
                false);
        given(reservationTimeService.getAvailableReservationTimes(any())).willReturn(List.of(response));

        mockMvc.perform(get("/times/available")
                        .param("date", "2024-06-01")
                        .param("themeId", "1"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메시지"),
                                fieldWithPath("data[].id").description("예약 시간 ID"),
                                fieldWithPath("data[].startAt").description("예약 시작 시간"),
                                fieldWithPath("data[].isReserved").description("예약 여부")
                        )
                ));
    }
}
