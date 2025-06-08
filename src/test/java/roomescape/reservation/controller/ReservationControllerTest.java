package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
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
import roomescape.auth.application.LoginMember;
import roomescape.global.config.UserAuthBaseTest;
import roomescape.member.dto.MemberResponse;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.service.ReservationService;
import roomescape.reservationtime.dto.AvailableReservationTimeResponse;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.theme.dto.ThemeResponse;

@SpringBootTest
@AutoConfigureRestDocs
@AutoConfigureMockMvc
class ReservationControllerTest extends UserAuthBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @Test
    @DisplayName("예약 조건 검색 API")
    void findReservationsByCriteria() throws Exception {
        // given
        List<ReservationResponse> responses = List.of(
                createReservationResponse(1L, LocalDate.of(2024, 1, 10)),
                createReservationResponse(2L, LocalDate.of(2024, 1, 15))
        );

        given(reservationService.findReservationsByCriteria(any(ReservationSearchRequest.class)))
                .willReturn(responses);

        // when && then
        mockMvc.perform(get("/reservations")
                        .param("themeId", "1")
                        .param("memberId", "1")
                        .param("dateFrom", "2024-01-01")
                        .param("dateTo", "2024-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andDo(document("reservation/find-by-criteria",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("themeId").description("테마 ID").optional(),
                                parameterWithName("memberId").description("회원 ID").optional(),
                                parameterWithName("dateFrom").description("시작 날짜").optional(),
                                parameterWithName("dateTo").description("종료 날짜").optional()
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("예약 ID"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].time.id").description("예약 시간 ID"),
                                fieldWithPath("[].time.startAt").description("시작 시간"),
                                fieldWithPath("[].theme.id").description("테마 ID"),
                                fieldWithPath("[].theme.name").description("테마 이름"),
                                fieldWithPath("[].theme.description").description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").description("테마 썸네일 이미지"),
                                fieldWithPath("[].member.id").description("회원 ID"),
                                fieldWithPath("[].member.name").description("회원 이름")
                        )
                ));
    }

    @Test
    @DisplayName("테마의 사용 가능한 예약 시간 조회 API")
    void findAllAvailableTimes() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 1, 1);
        Long themeId = 1L;

        List<AvailableReservationTimeResponse> responses = List.of(
                new AvailableReservationTimeResponse(1L, LocalTime.of(10, 0), false),
                new AvailableReservationTimeResponse(2L, LocalTime.of(12, 0), true),
                new AvailableReservationTimeResponse(3L, LocalTime.of(14, 0), false)
        );

        given(reservationService.findAllReservationTime(eq(date), eq(themeId))).willReturn(responses);

        // when && then
        mockMvc.perform(get("/reservations/times")
                        .param("date", "2024-01-01")
                        .param("themeId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].startAt").value("10:00"))
                .andExpect(jsonPath("$[0].alreadyBooked").value(false))
                .andExpect(jsonPath("$[1].alreadyBooked").value(true))
                .andDo(document("reservation/find-available-times",
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("date").description("조회할 날짜"),
                                parameterWithName("themeId").description("테마 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("예약 시간 ID"),
                                fieldWithPath("[].startAt").description("시작 시간"),
                                fieldWithPath("[].alreadyBooked").description("이미 예약 여부")
                        )
                ));
    }

    @Test
    @DisplayName("예약 추가 API")
    void saveReservation() throws Exception {
        // given
        LocalDate date = LocalDate.of(2024, 1, 1);
        long timeId = 1L;
        long themeId = 1L;

        ReservationRequest request = new ReservationRequest(
                date, timeId, themeId,
                "payment_key_123", "order_id_123", 10000L
        );

        ReservationResponse response = createReservationResponse(1L, date);

        given(reservationService.saveReservation(any(ReservationRequest.class), any(LoginMember.class)))
                .willReturn(response);

        // when && then
        mockMvc.perform(addAuthCookie(post("/reservations"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.date").value("2024-01-01"))
                .andDo(document("reservation/save",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 ID"),
                                fieldWithPath("themeId").description("테마 ID"),
                                fieldWithPath("paymentKey").description("결제 키"),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("amount").description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 ID"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("time.id").description("예약 시간 ID"),
                                fieldWithPath("time.startAt").description("시작 시간"),
                                fieldWithPath("theme.id").description("테마 ID"),
                                fieldWithPath("theme.name").description("테마 이름"),
                                fieldWithPath("theme.description").description("테마 설명"),
                                fieldWithPath("theme.thumbnail").description("테마 썸네일 이미지"),
                                fieldWithPath("member.id").description("회원 ID"),
                                fieldWithPath("member.name").description("회원 이름")
                        )
                ));
    }

    @Test
    @DisplayName("예약 취소 API")
    void deleteReservation() throws Exception {
        // given
        Long reservationId = 1L;
        doNothing().when(reservationService).deleteReservation(anyLong());

        // when && then
        mockMvc.perform(delete("/reservations/{reservationId}", reservationId))
                .andExpect(status().isNoContent())
                .andDo(document("reservation/delete",
                        preprocessRequest(prettyPrint()),
                        pathParameters(
                                parameterWithName("reservationId").description("삭제할 예약 ID")
                        )
                ));
    }

    @Test
    @DisplayName("본인 예약 목록 조회 API")
    void findMyReservations() throws Exception {
        // given
        List<MyReservationResponse> responses = List.of(
                createMyReservationResponse(1L, LocalDate.of(2024, 1, 10)),
                createMyReservationResponse(2L, LocalDate.of(2024, 1, 15))
        );

        given(reservationService.findMyReservations(any(LoginMember.class)))
                .willReturn(responses);

        // when && then
        mockMvc.perform(addAuthCookie(get("/reservations/mine")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reservationId").value(1L))
                .andExpect(jsonPath("$[1].reservationId").value(2L))
                .andDo(document("reservation/find-mine",
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("[].reservationId").description("예약 ID"),
                                fieldWithPath("[].date").description("예약 날짜"),
                                fieldWithPath("[].time").description("예약 시간"),
                                fieldWithPath("[].theme").description("테마 이름"),
                                fieldWithPath("[].status").description("예약 상태"),
                                fieldWithPath("[].paymentKey").description("결제 key"),
                                fieldWithPath("[].amount").description("결제 금액")
                        )
                ));
    }

    private ReservationResponse createReservationResponse(Long id, LocalDate date) {
        return new ReservationResponse(
                id,
                date,
                new ReservationTimeResponse(1L, LocalTime.of(13, 0)),
                new ThemeResponse(1L, "방탈출 테마1", "테마1 설명", "theme1.jpg"),
                new MemberResponse(1L, "테스트 사용자")
        );
    }

    private MyReservationResponse createMyReservationResponse(Long id, LocalDate date) {
        return new MyReservationResponse(
                id,
                "방탈출 테마1",
                date,
                LocalTime.of(13, 0),
                "예약",
                "payment_key_123",
                10000L
        );
    }
}
