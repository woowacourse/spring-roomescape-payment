package roomescape.reservationtime.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.service.ReservationTimeService;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class ReservationTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationTimeService reservationTimeService;

    @Test
    @DisplayName("예약 시간 생성 API")
    void saveTime() throws Exception {
        // given
        LocalTime startTime = LocalTime.of(10, 0);
        ReservationTimeRequest request = new ReservationTimeRequest(startTime);
        ReservationTimeResponse response = new ReservationTimeResponse(1L, startTime);

        given(reservationTimeService.saveTime(any(ReservationTimeRequest.class))).willReturn(response);

        // when && then
        mockMvc.perform(post("/times")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.startAt").value("10:00"))
                .andDo(document("reservationTime/save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("startAt").description("예약 시간")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 시간 ID"),
                                fieldWithPath("startAt").description("예약 시간")
                        )
                ));
    }

    @Test
    @DisplayName("모든 예약 시간 조회 API")
    void findAll() throws Exception {
        // given
        List<ReservationTimeResponse> responses = List.of(
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ReservationTimeResponse(2L, LocalTime.of(12, 0))
        );

        given(reservationTimeService.findAll()).willReturn(responses);

        // when && then
        mockMvc.perform(get("/times"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].startAt").value("10:00"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].startAt").value("12:00"))
                .andDo(document("reservationTime/find-all",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].id").description("예약 시간 ID"),
                                fieldWithPath("[].startAt").description("예약 시간")
                        )
                ));
    }

    @Test
    @DisplayName("예약 시간 삭제 API")
    void deleteTime() throws Exception {
        // given
        Long timeId = 1L;
        doNothing().when(reservationTimeService).delete(anyLong());

        // when && then
        mockMvc.perform(delete("/times/{timeId}", timeId))
                .andExpect(status().isNoContent())
                .andDo(document("reservationTime/delete",
                        preprocessRequest(prettyPrint()),
                        pathParameters(
                                parameterWithName("timeId").description("삭제할 시간 ID")
                        )
                ));
    }
}
