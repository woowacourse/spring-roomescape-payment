package roomescape.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.global.AuthInterceptor;
import roomescape.global.ControllerTest;
import roomescape.time.controller.ReservationTimeController;
import roomescape.time.dto.AvailableReservationTimeResponse;
import roomescape.time.dto.ReservationTimeRequest;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;

@WebMvcTest(ReservationTimeController.class)
@AutoConfigureRestDocs
class ReservationTimeControllerTestWithMock extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationTimeService reservationTimeService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("예약 시간 조회 API 테스트")
    class TimeListTest {

        @DisplayName("모든 예약 시간을 조회하면 200 OK를 반환한다")
        @Test
        void getAllTimes() throws Exception {
            // given
            final List<ReservationTimeResponse> responses = List.of(
                    new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                    new ReservationTimeResponse(2L, LocalTime.of(14, 0))
            );

            given(reservationTimeService.findReservationTimes()).willReturn(responses);

            // when & then
            mockMvc.perform(get("/times"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].startAt").value("10:00:00"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].startAt").value("14:00:00"))
                    .andDo(restDocs.document(
                            responseFields(
                                    fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 시간 목록"),
                                    fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("시작 시간")
                            )
                    ));
        }

        @DisplayName("특정 날짜와 테마의 예약 가능한 시간을 조회하면 200 OK를 반환한다")
        @Test
        void getAvailableTimes() throws Exception {
            // given
            final LocalDate date = LocalDate.now().plusDays(1);
            final long themeId = 1L;
            final List<AvailableReservationTimeResponse> responses = List.of(
                    new AvailableReservationTimeResponse(1L, LocalTime.of(10, 0), true),
                    new AvailableReservationTimeResponse(2L, LocalTime.of(14, 0), false)
            );

            given(reservationTimeService.getAvailableTimes(date, themeId)).willReturn(responses);

            // when & then
            mockMvc.perform(get("/times/available-times")
                            .param("date", date.toString())
                            .param("themeId", String.valueOf(themeId)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].startAt").value("10:00"))
                    .andExpect(jsonPath("$[0].isBooked").value(true))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].startAt").value("14:00"))
                    .andExpect(jsonPath("$[1].isBooked").value(false))
                    .andDo(restDocs.document(
                            queryParameters(
                                    parameterWithName("date").description("예약 날짜 (YYYY-MM-DD 형식)"),
                                    parameterWithName("themeId").description("테마 ID")
                            ),
                            responseFields(
                                    fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 가능한 시간 목록"),
                                    fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                    fieldWithPath("[].isBooked").type(JsonFieldType.BOOLEAN)
                                            .description("예약 여부 (true: 예약됨, false: 예약 가능)")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("예약 시간 생성 API 테스트")
    class TimeCreateTest {

        @DisplayName("정상적인 예약 시간 생성 요청 시 201 Created를 반환한다")
        @Test
        void createTimeSuccess() throws Exception {
            // given
            final ReservationTimeResponse response = new ReservationTimeResponse(1L, LocalTime.of(10, 0));
            final ReservationTimeRequest request = new ReservationTimeRequest(LocalTime.of(10, 0));

            given(reservationTimeService.addReservationTime(any(ReservationTimeRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/times")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.startAt").value("10:00:00"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("startAt").type(JsonFieldType.STRING).description("시작 시간 (HH:mm 형식)")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 시간 ID"),
                                    fieldWithPath("startAt").type(JsonFieldType.STRING).description("시작 시간")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("예약 시간 삭제 API 테스트")
    class TimeDeleteTest {

        @DisplayName("정상적인 예약 시간 삭제 시 204 No Content를 반환한다")
        @Test
        void deleteTimeSuccess() throws Exception {
            // given
            long timeId = 1L;

            willDoNothing()
                    .given(reservationTimeService)
                    .removeReservationTime(timeId);

            // when & then
            mockMvc.perform(delete("/times/{id}", timeId))
                    .andExpect(status().isNoContent())
                    .andDo(restDocs.document(
                            pathParameters(
                                    parameterWithName("id").description("삭제할 시간 ID")
                            )
                    ));
        }
    }
}
