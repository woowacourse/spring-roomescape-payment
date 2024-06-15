package roomescape.documentaion;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.reservation.application.ReservationTimeService;
import roomescape.reservation.application.ThemeService;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.dto.request.ReservationTimeSaveRequest;
import roomescape.reservation.dto.response.AvailableReservationTimeResponse;
import roomescape.reservation.presentation.ReservationTimeController;

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
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;

class ReservationTimeApiDocumentTest extends DocumentTest {
    private final ReservationTimeService reservationTimeService = Mockito.mock(ReservationTimeService.class);
    private final ThemeService themeService = Mockito.mock(ThemeService.class);

    @Test
    @DisplayName("예약 시간 생성 API")
    void createReservationTime() throws Exception {
        ReservationTimeSaveRequest request = new ReservationTimeSaveRequest(MIA_RESERVATION_TIME);
        ReservationTime expectedReservationTime = new ReservationTime(1L, MIA_RESERVATION_TIME);

        BDDMockito.given(reservationTimeService.create(any()))
                .willReturn(expectedReservationTime);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andDo(document(
                                "time-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("startAt").type(JsonFieldType.STRING).description("예약 시간(10분 단위) ex) 13:00")
                                ),
                                responseFields(
                                        fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                        fieldWithPath("startAt").type(JsonFieldType.STRING)
                                                .description("예약 시간(10분 단위) ex) 13:00")
                                )
                        )
                );
    }

    @Test
    @DisplayName("예약 시간 목록 조회 API")
    void findReservationTimes() throws Exception {
        BDDMockito.given(reservationTimeService.findAll())
                .willReturn(List.of(new ReservationTime(1L, MIA_RESERVATION_TIME)));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/times").contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "time-find",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                        fieldWithPath("[].startAt").type(JsonFieldType.STRING)
                                                .description("예약 시간(10분 단위) ex) 13:00")
                                )
                        )
                );
    }

    @Test
    @DisplayName("예약 시간 삭제 API")
    void deleteReservationTime() throws Exception {
        BDDMockito.willDoNothing()
                .given(reservationTimeService)
                .delete(anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/times/{id}", anyLong())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent())
                .andDo(document(
                                "time-delete",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("삭제 대상 예약 시간 식별자")
                                )
                        )
                );
    }

    @Test
    @DisplayName("에약 가능 시간 목록 조회 API")
    void findAllByDateAndThemeId() throws Exception {
        long themeId = 1L;
        BDDMockito.given(reservationTimeService.findAvailableReservationTimes(any(), any()))
                .willReturn(List.of(AvailableReservationTimeResponse.of(new ReservationTime(1L, MIA_RESERVATION_TIME), true)));

        mockMvc.perform(RestDocumentationRequestBuilders.get("/times/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", MIA_RESERVATION_DATE.toString())
                        .param("themeId", Long.toString(themeId)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(
                                "time-find-available",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                queryParameters(
                                        parameterWithName("date").description("예약 가능 시간 조회 시 필요한 기준 날짜 ex) 2024-05-01"),
                                        parameterWithName("themeId").description("예약 가능 시간 조회 시 필요한 기준 테마 식별자")
                                ),
                                responseFields(
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 시간 식별자"),
                                        fieldWithPath("[].startAt").type(JsonFieldType.STRING)
                                                .description("예약 시간(10분 단위) ex) 13:00"),
                                        fieldWithPath("[].isReserved").type(JsonFieldType.BOOLEAN).description("예약 여부")
                                )
                        )
                );
    }

    @Override
    protected Object initController() {
        return new ReservationTimeController(reservationTimeService, themeService);
    }
}
