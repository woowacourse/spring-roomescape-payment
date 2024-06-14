package roomescape.document;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import roomescape.controller.ReservationTimeController;
import roomescape.document.config.RestDocsSupport;
import roomescape.dto.AvailableTimeResponse;
import roomescape.dto.ReservationTimeRequest;
import roomescape.dto.ReservationTimeResponse;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.service.AvailableTimeService;
import roomescape.service.ReservationTimeService;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationTimeController.class)
public class ReservationTimeRestDocsTest extends RestDocsSupport {

    @MockBean
    private ReservationTimeService reservationTimeService;

    @MockBean
    private AvailableTimeService availableTimeService;

    @Test
    public void save() throws Exception {
        ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(8, 30));
        given(reservationTimeService.save(any()))
                .willReturn(ReservationTimeResponse.from(ReservationTimeFixture.DEFAULT_TIME));

        mockMvc.perform(post("/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("startAt").description("예약 시간")
                        ),
                        responseFields(
                                fieldWithPath("id").description("시간 id"),
                                fieldWithPath("startAt").description("예약 시간")
                        )
                ));
    }

    @Test
    public void findAll() throws Exception {
        List<ReservationTimeResponse> response = List.of(
                ReservationTimeResponse.from(ReservationTimeFixture.DEFAULT_TIME),
                ReservationTimeResponse.from(ReservationTimeFixture.DEFAULT_TIME)
        );
        given(reservationTimeService.findAll())
                .willReturn(response);

        mockMvc.perform(get("/times"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].id").description("시간 id"),
                                fieldWithPath("[].startAt").description("예약 시간")
                        )
                ));
    }

    @Test
    public void findByThemeAndDate() throws Exception {

        List<AvailableTimeResponse> response = List.of(
                AvailableTimeResponse.of(ReservationTimeFixture.DEFAULT_TIME, true),
                AvailableTimeResponse.of(ReservationTimeFixture.DEFAULT_TIME, false)
        );
        given(availableTimeService.findByThemeAndDate(any(), anyLong()))
                .willReturn(response);

        mockMvc.perform(get("/times/book-able")
                        .param("date", "2024-06-10")
                        .param("themeId", "1"))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        queryParameters(
                                parameterWithName("date").description("예약 신청 날짜"),
                                parameterWithName("themeId").description("예약 신청 테마 id")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("시간 id"),
                                fieldWithPath("[].startAt").description("예약 시간"),
                                fieldWithPath("[].isBooked").description("선행 예약 존재 여부")
                        )
                ));
    }

    @Test
    public void delete() throws Exception {
        doNothing().when(reservationTimeService)
                .delete(anyLong());

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/times/{id}", ReservationTimeFixture.DEFAULT_TIME.getId()))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("id").description("삭제할 시간의 id")
                        )
                ));
    }
}
