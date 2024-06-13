package roomescape.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.application.ReservationTimeService;
import roomescape.application.dto.response.AvailableReservationTimeResponse;
import roomescape.application.dto.response.ReservationTimeResponse;
import roomescape.domain.reservation.detail.ReservationTime;
import roomescape.domain.reservation.dto.AvailableReservationTimeDto;
import roomescape.presentation.api.ReservationTimeController;

@WebMvcTest(ReservationTimeController.class)
class ReservationTimeDocumentTest extends AbstractDocumentTest {

    @MockBean
    private ReservationTimeService reservationTimeService;

    @Test
    @DisplayName("예약 시간 목록을 조회한다.")
    void getAllReservationTimes() throws Exception {
        List<ReservationTimeResponse> responses = List.of(
                ReservationTimeResponse.from(new ReservationTime(1L, LocalTime.of(10, 0))),
                ReservationTimeResponse.from(new ReservationTime(2L, LocalTime.of(11, 0)))
        );

        when(reservationTimeService.getAllReservationTimes())
                .thenReturn(responses);

        mockMvc.perform(
                get("/times")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(responses))
        ).andDo(
                document("times/list",
                        responseFields(
                                fieldWithPath("[].id").description("예약 시간 식별자"),
                                fieldWithPath("[].startAt").description("시작 시간")
                        ))
        );
    }

    @Test
    @DisplayName("이용 가능한 예약 시간들을 조회한다.")
    void getAvailableReservationTimes() throws Exception {
        List<AvailableReservationTimeResponse> responses = List.of(
                AvailableReservationTimeResponse.from(new AvailableReservationTimeDto(
                        1L, LocalTime.of(10, 0), true
                )),
                AvailableReservationTimeResponse.from(new AvailableReservationTimeDto(
                        2L, LocalTime.of(11, 0), false
                ))
        );

        when(reservationTimeService.getAvailableReservationTimes(any(), anyLong()))
                .thenReturn(responses);

        mockMvc.perform(
                get("/times/available")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("date", "2024-04-08")
                        .param("themeId", "1")
        ).andExpectAll(
                status().isOk(),
                content().json(objectMapper.writeValueAsString(responses))
        ).andDo(
                document("times/list/available",
                        queryParameters(
                                parameterWithName("date").description("조회 날짜"),
                                parameterWithName("themeId").description("테마 식별자")
                        ),
                        responseFields(
                                fieldWithPath("[].timeId").description("예약 시간 식별자"),
                                fieldWithPath("[].startAt").description("시작 시간"),
                                fieldWithPath("[].alreadyBooked").description("이미 예약된 시간인지 여부")
                        ))
        );
    }
}
