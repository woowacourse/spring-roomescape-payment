package roomescape.documentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import roomescape.controller.api.ReservationTimeController;
import roomescape.controller.dto.request.ReservationTimeRequest;
import roomescape.service.ReservationTimeService;
import roomescape.service.dto.response.AvailableReservationTimeResponse;
import roomescape.service.dto.response.ReservationTimeResponse;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


class ReservationTimeApiDocumentationTest extends BaseDocumentationTest {
    private final ReservationTimeService reservationTimeService = Mockito.mock(ReservationTimeService.class);

    @Test
    @DisplayName("모든 예약 시간을 조회한다")
    void getAllReservationTimes() throws Exception {
        when(reservationTimeService.getAllReservationTimes())
                .thenReturn(List.of(
                        new ReservationTimeResponse(1L, LocalTime.parse("10:00")),
                        new ReservationTimeResponse(2L, LocalTime.parse("11:00")),
                        new ReservationTimeResponse(3L, LocalTime.parse("12:00"))
                ));

        mockMvc.perform(get("/times")
                        .cookie(adminCookie)
                )
                .andExpect(status().isOk())
                .andDo(document("time/findAll",
                        responseFields(
                                fieldWithPath("list.[].id").description("예약 시간 id"),
                                fieldWithPath("list.[].startAt").description("예약 시간")
                        )
                ));
    }

    @Test
    @DisplayName("예약 시간을 추가한다")
    void addReservationTime() throws Exception {
        ReservationTimeResponse response = new ReservationTimeResponse(1L, LocalTime.parse("10:00"));
        when(reservationTimeService.addReservationTime(any()))
                .thenReturn(response);
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.parse("10:00"));
        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(adminCookie)
                        .content(content)
                )
                .andExpect(status().isCreated())
                .andDo(document("time/create",
                        requestFields(
                                fieldWithPath("startAt").description("예약 시간")
                        ),
                        responseHeaders(
                                headerWithName("Location").description("생성된 예약 시간의 URI")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 시간 id"),
                                fieldWithPath("startAt").description("예약 시간")
                        )
                ));
    }

    @Test
    @DisplayName("예약 시간을 삭제한다")
    void deleteReservationTime() throws Exception {
        doNothing().when(reservationTimeService).deleteReservationTimeById(any());

        mockMvc.perform(delete("/times/{id}", 1)
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNoContent())
                .andDo(document("time/delete",
                        pathParameters(
                                parameterWithName("id").description("예약 시간 id")
                        )
                ));
    }

    @Test
    @DisplayName("예약 가능한 시간을 조회한다")
    void getAvailableReservationTimes() throws Exception {
        when(reservationTimeService.getAvailableReservationTimes(any(), any()))
                .thenReturn(List.of(
                        new AvailableReservationTimeResponse(1L, LocalTime.parse("10:00"), true),
                        new AvailableReservationTimeResponse(2L, LocalTime.parse("11:00"), false),
                        new AvailableReservationTimeResponse(3L, LocalTime.parse("12:00"), true)
                ));

        mockMvc.perform(get("/times/available")
                        .cookie(adminCookie)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", "2024-06-10")
                        .param("themeId", "1")
                )
                .andExpect(status().isOk())
                .andDo(document("time/available",
                        queryParameters(
                                parameterWithName("date").description("예약 날짜"),
                                parameterWithName("themeId").description("테마 id")
                        ),
                        responseFields(
                                fieldWithPath("list.[].timeId").description("예약 시간 id"),
                                fieldWithPath("list.[].startAt").description("예약 시간"),
                                fieldWithPath("list.[].alreadyBooked").description("예약 여부")
                        )
                ));
    }

    @Override
    Object controller() {
        return new ReservationTimeController(reservationTimeService);
    }
}
