package roomescape.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
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
import roomescape.reservation.controller.ReservationController;
import roomescape.reservation.dto.AdminReservationPaymentRequest;
import roomescape.reservation.dto.MyPageReservationResponse;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.dto.ReservationThemeResponse;
import roomescape.time.dto.ReservationTimeResponse;

@WebMvcTest(ReservationController.class)
@AutoConfigureRestDocs
class ReservationControllerTestWithMock extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("예약 생성 API 테스트")
    class ReservationCreateTest {

        @DisplayName("정상적인 예약 생성 요청 시 201 Created를 반환한다")
        @Test
        void createReservationSuccess() throws Exception {
            // given
            final ReservationResponse response = new ReservationResponse(1L, "제프리",
                    LocalDate.now().plusDays(1),
                    new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                    new ReservationThemeResponse(1L, "공포 테마", "무서운 테마입니다", "thumbnail.jpg"));

            given(reservationService.addReservation(any(Long.class), any(ReservationPaymentRequest.class)))
                    .willReturn(response);

            final Map<String, Object> request = Map.of(
                    "memberId", 1,
                    "date", LocalDate.now().plusDays(1).toString(),
                    "themeId", 1,
                    "timeId", 1,
                    "paymentKey", "test_payment_key",
                    "orderId", "test_order_id",
                    "amount", 10000,
                    "paymentType", "NORMAL"
            );

            final MockHttpSession session = new MockHttpSession();
            session.setAttribute("id", 1L);

            // when & then
            mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .session(session))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.date").value(LocalDate.now().plusDays(1).toString()))
                    .andExpect(jsonPath("$.time.id").value(1))
                    .andExpect(jsonPath("$.time.startAt").value("10:00:00"))
                    .andExpect(jsonPath("$.theme.id").value(1))
                    .andExpect(jsonPath("$.theme.name").value("공포 테마"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                    fieldWithPath("date").type(JsonFieldType.STRING)
                                            .description("예약 날짜 (YYYY-MM-DD 형식)"),
                                    fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키"),
                                    fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 ID"),
                                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                    fieldWithPath("paymentType").type(JsonFieldType.STRING)
                                            .description("결제 유형 (NORMAL)")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 ID"),
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("예약자 이름"),
                                    fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                    fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                    fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                    fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                    fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
                            )
                    ));
        }

        @DisplayName("잘못된 날짜 형식으로 요청 시 400 Bad Request를 반환한다")
        @Test
        void createReservationWithInvalidDateFormat() throws Exception {
            // given
            final Map<String, Object> request = Map.of(
                    "memberId", 1,
                    "date", "2023/08/05",
                    "themeId", 1,
                    "timeId", 1,
                    "paymentKey", "test_payment_key",
                    "orderId", "test_order_id",
                    "amount", 10000,
                    "paymentType", "NORMAL"
            );

            // when & then
            mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                    fieldWithPath("date").type(JsonFieldType.STRING).description("잘못된 형식의 예약 날짜"),
                                    fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키"),
                                    fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 ID"),
                                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                    fieldWithPath("paymentType").type(JsonFieldType.STRING).description("결제 유형")
                            )
                    ));
        }

        @DisplayName("과거 날짜로 예약 요청 시 400 Bad Request를 반환한다")
        @Test
        void createReservationWithPastDate() throws Exception {
            // given
            final Map<String, Object> request = Map.of(
                    "memberId", 1,
                    "date", LocalDate.now().minusDays(1).toString(),
                    "themeId", 1,
                    "timeId", 1,
                    "paymentKey", "test_payment_key",
                    "orderId", "test_order_id",
                    "amount", 10000,
                    "paymentType", "NORMAL"
            );

            willThrow(new IllegalArgumentException())
                    .given(reservationService)
                    .addReservation(any(Long.class), any(ReservationPaymentRequest.class));

            final MockHttpSession session = new MockHttpSession();
            session.setAttribute("id", 1L);

            // when & then
            mockMvc.perform(post("/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                            .session(session))
                    .andExpect(status().isBadRequest())
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID"),
                                    fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜 (과거 날짜)"),
                                    fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키"),
                                    fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 ID"),
                                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                    fieldWithPath("paymentType").type(JsonFieldType.STRING).description("결제 유형")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("예약 삭제 API 테스트")
    class ReservationDeleteTest {

        @DisplayName("존재하지 않는 예약 삭제 시 404 Not Found를 반환한다")
        @Test
        void deleteNonExistentReservation() throws Exception {
            // given
            long nonExistentId = 999L;

            willThrow(new NoSuchElementException("[ERROR] 존재하지 않는 예약 입니다."))
                    .given(reservationService)
                    .removeReservation(any(Long.class));

            // when & then
            mockMvc.perform(delete("/reservations/{id}", nonExistentId))
                    .andExpect(status().isNotFound())
                    .andDo(restDocs.document(
                            pathParameters(
                                    parameterWithName("id").description("삭제할 예약 ID (존재하지 않음)")
                            )
                    ));
        }

        @DisplayName("정상적인 예약 삭제 시 204 No Content를 반환한다")
        @Test
        void deleteReservationSuccess() throws Exception {
            // given
            long existingId = 1L;

            willDoNothing()
                    .given(reservationService)
                    .removeReservation(any(Long.class));

            // when & then
            mockMvc.perform(delete("/reservations/{id}", existingId))
                    .andExpect(status().isNoContent())
                    .andDo(restDocs.document(
                            pathParameters(
                                    parameterWithName("id").description("삭제할 예약 ID")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("예약 조회 API 테스트")
    class ReservationListTest {

        @DisplayName("모든 예약을 조회하면 200 OK를 반환한다")
        @Test
        void getAllReservations() throws Exception {
            // given
            final List<ReservationResponse> responses = List.of(
                    new ReservationResponse(1L, "제프리",
                            LocalDate.now().plusDays(1),
                            new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                            new ReservationThemeResponse(1L, "공포 테마", "무서운 테마입니다", "thumbnail.jpg")),
                    new ReservationResponse(2L, "안나",
                            LocalDate.now().plusDays(2),
                            new ReservationTimeResponse(2L, LocalTime.of(14, 0)),
                            new ReservationThemeResponse(2L, "모험 테마", "모험이 가득한 테마입니다", "adventure.jpg"))
            );

            given(reservationService.getAllReservations()).willReturn(responses);

            // when & then
            mockMvc.perform(get("/reservations"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("제프리"))
                    .andExpect(jsonPath("$[0].theme.name").value("공포 테마"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].name").value("안나"))
                    .andExpect(jsonPath("$[1].theme.name").value("모험 테마"))
                    .andDo(restDocs.document(
                            responseFields(
                                    fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 목록"),
                                    fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 ID"),
                                    fieldWithPath("[].name").type(JsonFieldType.STRING).description("예약자 이름"),
                                    fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                                    fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                    fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                    fieldWithPath("[].theme.description").type(JsonFieldType.STRING)
                                            .description("테마 설명"),
                                    fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("관리자 예약 생성 API 테스트")
    class AdminReservationCreateTest {

        @DisplayName("관리자가 정상적인 예약 생성 요청 시 201 Created를 반환한다")
        @Test
        void createAdminReservationSuccess() throws Exception {
            // given
            final ReservationResponse response = new ReservationResponse(1L, "제프리",
                    LocalDate.now().plusDays(1),
                    new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                    new ReservationThemeResponse(1L, "공포 테마", "무서운 테마입니다", "thumbnail.jpg"));

            given(reservationService.addReservation(any(Long.class), any(ReservationPaymentRequest.class)))
                    .willReturn(response);

            given(authInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class)))
                    .willReturn(true);

            final AdminReservationPaymentRequest request = new AdminReservationPaymentRequest(
                    LocalDate.now().plusDays(1),
                    1L,
                    1L,
                    "admin_payment_key",
                    "admin_order_id",
                    15000L,
                    "NORMAL",
                    1L
            );

            // when & then
            mockMvc.perform(post("/admin/reservations")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("제프리"))
                    .andExpect(jsonPath("$.date").value(LocalDate.now().plusDays(1).toString()))
                    .andExpect(jsonPath("$.time.id").value(1))
                    .andExpect(jsonPath("$.theme.id").value(1))
                    .andExpect(jsonPath("$.theme.name").value("공포 테마"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("date").type(JsonFieldType.STRING)
                                            .description("예약 날짜 (YYYY-MM-DD 형식)"),
                                    fieldWithPath("themeId").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("timeId").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("결제 키"),
                                    fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 ID"),
                                    fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액"),
                                    fieldWithPath("paymentType").type(JsonFieldType.STRING)
                                            .description("결제 유형 (NORMAL)"),
                                    fieldWithPath("memberId").type(JsonFieldType.NUMBER).description("회원 ID")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("예약 ID"),
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("예약자 이름"),
                                    fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                    fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("시간 ID"),
                                    fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                    fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                    fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                    fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
                            )
                    ));
        }

        @Nested
        @DisplayName("관리자 예약 조회 API 테스트")
        class AdminReservationListTest {

            @DisplayName("모든 필터로 관리자 예약 조회하면 200 OK를 반환한다")
            @Test
            void getAdminReservationsWithAllFilters() throws Exception {
                // given
                final LocalDate dateFrom = LocalDate.now();
                final LocalDate dateTo = LocalDate.now().plusDays(7);
                final List<ReservationResponse> responses = List.of(
                        new ReservationResponse(1L, "제프리",
                                LocalDate.now().plusDays(1),
                                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                                new ReservationThemeResponse(1L, "공포 테마", "무서운 테마입니다", "thumbnail.jpg"))
                );

                given(reservationService.getFilteredReservations(1L, 1L, dateFrom, dateTo))
                        .willReturn(responses);

                given(authInterceptor.preHandle(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Object.class)))
                        .willReturn(true);

                // when & then
                mockMvc.perform(get("/admin/reservations")
                                .param("memberId", "1")
                                .param("themeId", "1")
                                .param("dateFrom", dateFrom.toString())
                                .param("dateTo", dateTo.toString()))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].id").value(1))
                        .andExpect(jsonPath("$[0].name").value("제프리"))
                        .andDo(restDocs.document(
                                queryParameters(
                                        parameterWithName("memberId").description("회원 ID 필터").optional(),
                                        parameterWithName("themeId").description("테마 ID 필터").optional(),
                                        parameterWithName("dateFrom").description("예약 날짜 시작 범위 (YYYY-MM-DD)")
                                                .optional(),
                                        parameterWithName("dateTo").description("예약 날짜 종료 범위 (YYYY-MM-DD)").optional()
                                ),
                                responseFields(
                                        fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 목록"),
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 ID"),
                                        fieldWithPath("[].name").type(JsonFieldType.STRING).description("예약자 이름"),
                                        fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                                        fieldWithPath("[].time.id").type(JsonFieldType.NUMBER).description("시간 ID"),
                                        fieldWithPath("[].time.startAt").type(JsonFieldType.STRING)
                                                .description("시작 시간"),
                                        fieldWithPath("[].theme.id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                        fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                        fieldWithPath("[].theme.description").type(JsonFieldType.STRING)
                                                .description("테마 설명"),
                                        fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                                .description("테마 썸네일")
                                )
                        ));
            }
        }

        @Nested
        @DisplayName("내 예약 조회 API 테스트")
        class MyReservationListTest {

            @DisplayName("로그인된 회원의 예약을 조회하면 200 OK를 반환한다")
            @Test
            void getMyReservations() throws Exception {
                // given
                final List<MyPageReservationResponse> responses = List.of(
                        new MyPageReservationResponse(1L, "공포 테마",
                                LocalDate.now().plusDays(1), LocalTime.of(10, 0), "예약", "payment_key_1", 10000),
                        new MyPageReservationResponse(2L, "모험 테마",
                                LocalDate.now().plusDays(2), LocalTime.of(14, 0), "1번째 예약대기", "", 0)
                );

                given(reservationService.getReservationsByMemberId(1L)).willReturn(responses);

                final MockHttpSession session = new MockHttpSession();
                session.setAttribute("id", 1L);

                // when & then
                mockMvc.perform(get("/members/reservations")
                                .session(session))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$").isArray())
                        .andExpect(jsonPath("$[0].id").value(1))
                        .andExpect(jsonPath("$[0].theme").value("공포 테마"))
                        .andExpect(jsonPath("$[0].status").value("예약"))
                        .andExpect(jsonPath("$[0].paymentKey").value("payment_key_1"))
                        .andExpect(jsonPath("$[0].amount").value(10000))
                        .andExpect(jsonPath("$[1].id").value(2))
                        .andExpect(jsonPath("$[1].theme").value("모험 테마"))
                        .andExpect(jsonPath("$[1].status").value("1번째 예약대기"))
                        .andExpect(jsonPath("$[1].paymentKey").value(""))
                        .andExpect(jsonPath("$[1].amount").value(0))
                        .andDo(restDocs.document(
                                responseFields(
                                        fieldWithPath("[]").type(JsonFieldType.ARRAY).description("내 예약 목록"),
                                        fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("예약 ID"),
                                        fieldWithPath("[].theme").type(JsonFieldType.STRING).description("테마 이름"),
                                        fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                                        fieldWithPath("[].time").type(JsonFieldType.STRING).description("예약 시간"),
                                        fieldWithPath("[].status").type(JsonFieldType.STRING)
                                                .description("예약 상태 (예약/N번째 예약대기)"),
                                        fieldWithPath("[].paymentKey").type(JsonFieldType.STRING)
                                                .description("결제 키 (예약대기시 빈 문자열)"),
                                        fieldWithPath("[].amount").type(JsonFieldType.NUMBER)
                                                .description("결제 금액 (예약대기시 0)")
                                )
                        ));
            }
        }
    }
}
