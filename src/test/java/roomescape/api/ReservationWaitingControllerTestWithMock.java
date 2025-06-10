package roomescape.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.global.AuthInterceptor;
import roomescape.global.ControllerTest;
import roomescape.global.LoginMemberArgumentResolver;
import roomescape.theme.dto.ReservationThemeResponse;
import roomescape.time.dto.ReservationTimeResponse;
import roomescape.waiting.controller.ReservationWaitingController;
import roomescape.waiting.dto.AdminReservationWaitingResponse;
import roomescape.waiting.dto.ReservationWaitingRequest;
import roomescape.waiting.dto.ReservationWaitingResponse;
import roomescape.waiting.service.ReservationWaitingService;

@WebMvcTest(ReservationWaitingController.class)
@AutoConfigureRestDocs
class ReservationWaitingControllerTestWithMock extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationWaitingService reservationWaitingService;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("예약 대기 생성 API 테스트")
    class WaitingCreateTest {

        @DisplayName("정상적인 예약 대기 생성 요청 시 201 Created를 반환한다")
        @Test
        void createWaitingSuccess() throws Exception {
            // given
            final ReservationWaitingResponse response = new ReservationWaitingResponse(1L,
                    LocalDate.now().plusDays(1),
                    new ReservationTimeResponse(1L, LocalTime.of(12, 0)),
                    new ReservationThemeResponse(1L, "테마이름", "테마설명", "썸네일"));

            final ReservationWaitingRequest request = new ReservationWaitingRequest(
                    LocalDate.now().plusDays(1), 1L, 1L);

            given(reservationWaitingService.addReservationWaiting(any(ReservationWaitingRequest.class), any(Long.class)))
                    .willReturn(response);

            final MockHttpSession session = new MockHttpSession();
            session.setAttribute("id", 1L);

            // when & then
            mockMvc.perform(post("/reservations-waiting")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .session(session))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.date").value(LocalDate.now().plusDays(1).toString()))
                    .andExpect(jsonPath("$.time.id").value(1L))
                    .andExpect(jsonPath("$.time.startAt").value("12:00:00"))
                    .andExpect(jsonPath("$.theme.id").value(1L))
                    .andExpect(jsonPath("$.theme.name").value("테마이름"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("date").type(JsonFieldType.STRING)
                                            .description("예약 대기 날짜 (YYYY-MM-DD 형식)"),
                                    fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("시간 ID")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 대기 ID"),
                                    fieldWithPath("date").type(JsonFieldType.STRING).description("예약 대기 날짜"),
                                    fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                    fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                    fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                    fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("예약 대기 삭제 API 테스트")
    class WaitingDeleteTest {

        @DisplayName("정상적인 예약 대기 삭제 시 204 No Content를 반환한다")
        @Test
        void deleteWaitingSuccess() throws Exception {
            // given
            long waitingId = 1L;

            willDoNothing()
                    .given(reservationWaitingService)
                    .removeReservationWaiting(waitingId);

            // when & then
            mockMvc.perform(delete("/reservations-waiting/{id}", waitingId))
                    .andExpect(status().isNoContent())
                    .andDo(restDocs.document(
                            pathParameters(
                                    parameterWithName("id").description("삭제할 예약 대기 ID")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("관리자 예약 대기 조회 API 테스트")
    class AdminWaitingListTest {

        @DisplayName("관리자가 모든 예약 대기를 조회하면 200 OK를 반환한다")
        @Test
        void getAdminWaitingList() throws Exception {
            // given
            final List<AdminReservationWaitingResponse> responses = List.of(
                    new AdminReservationWaitingResponse(1L, "제프리","테마",LocalDate.now().plusDays(1), LocalTime.of(10, 0)),
                    new AdminReservationWaitingResponse(2L, "윌슨","테마",LocalDate.now().plusDays(2), LocalTime.of(11, 0))
            );

            given(reservationWaitingService.getAllReservationWaiting()).willReturn(responses);

            given(authInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class)))
                    .willReturn(true);

            // when & then
            mockMvc.perform(get("/admin/reservations-waiting"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("제프리"))
                    .andExpect(jsonPath("$[0].theme").value("테마"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].name").value("윌슨"))
                    .andExpect(jsonPath("$[1].theme").value("테마"))
                    .andDo(restDocs.document(
                            responseFields(
                                    fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 대기 목록"),
                                    fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 대기 ID"),
                                    fieldWithPath("[].name").type(JsonFieldType.STRING).description("예약자 이름"),
                                    fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 대기 날짜"),
                                    fieldWithPath("[].startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                    fieldWithPath("[].theme").type(JsonFieldType.STRING).description("테마 이름")
                            )
                    ));
        }

        @DisplayName("관리자가 정상적인 예약 대기 삭제 시 204 No Content를 반환한다")
        @Test
        void deleteAdminWaitingSuccess() throws Exception {
            // given
            long waitingId = 1L;

            willDoNothing()
                    .given(reservationWaitingService)
                    .removeReservationWaiting(waitingId);

            given(authInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class)))
                    .willReturn(true);

            // when & then
            mockMvc.perform(delete("/admin/reservations-waiting/{id}", waitingId))
                    .andExpect(status().isNoContent())
                    .andDo(restDocs.document(
                            pathParameters(
                                    parameterWithName("id").description("삭제할 예약 대기 ID")
                            )
                    ));
        }
    }
}
