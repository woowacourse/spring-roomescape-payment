package roomescape.unit.presentation;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.AuthToken;
import roomescape.auth.jwt.JJWTJwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.ReservationService;
import roomescape.exception.reservation.ReservationExistsException;
import roomescape.exception.reservation.ReservationNotFoundException;
import roomescape.presentation.api.ReservationApiController;
import roomescape.presentation.dto.request.AdminReservationRequest;
import roomescape.presentation.dto.request.ReservationCondition;
import roomescape.presentation.dto.response.MemberResponse;
import roomescape.presentation.dto.response.PaymentResponse;
import roomescape.presentation.dto.response.ReservationResponse;
import roomescape.presentation.dto.response.ReservationWithPaymentResponse;
import roomescape.presentation.dto.response.ThemeResponse;
import roomescape.presentation.dto.response.TimeSlotResponse;

@WebMvcTest(value = {ReservationApiController.class, JJWTJwtUtil.class})
@ExtendWith(RestDocumentationExtension.class)
class ReservationApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JJWTJwtUtil jwtUtil;

    @MockitoBean
    private ReservationService reservationService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

    @Test
    void 관리자_권한으로_예약_생성에_성공한다() throws Exception {
        // given
        AdminReservationRequest request = new AdminReservationRequest(
                LocalDate.now().plusDays(1),
                "timeId1",
                "themeId1",
                "memberId1"
        );
        ReservationResponse response = new ReservationResponse(
                "reservationId1",
                new MemberResponse("memberId1", "name", "email1@domain.com"),
                LocalDate.of(2025, 1, 1),
                new TimeSlotResponse("timeSlotId1", LocalTime.of(9, 0)),
                new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1", 1000L)
        );
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        given(reservationService.addAndGetWithoutPayment(request)).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(post("/admin/reservations")
                .cookie(new Cookie("authToken", token.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/reservations/reservationId1"))
                .andExpectAll(
                        jsonPath("$.id").value("reservationId1"),
                        jsonPath("$.date").value("2025-01-01"))
                .andDo(document("reservation/admin-create-reservation/success",
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("themeId").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("userId").type(JsonFieldType.STRING).description("회원 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("theme").type(JsonFieldType.OBJECT).description("테마 정보"),
                                fieldWithPath("theme.id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL"),
                                fieldWithPath("theme.price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("user").type(JsonFieldType.OBJECT).description("예약자 정보"),
                                fieldWithPath("user.id").type(JsonFieldType.STRING).description("예약자 ID"),
                                fieldWithPath("user.name").type(JsonFieldType.STRING).description("예약자 이름"),
                                fieldWithPath("user.email").type(JsonFieldType.STRING).description("예약자 이메일"),
                                fieldWithPath("time").type(JsonFieldType.OBJECT).description("예약 시간 정보"),
                                fieldWithPath("time.id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("예약 시간")
                        )
                ));
    }

    @Test
    void 같은_예약이_존재하는_경우_관리자_예약에_실패한다() throws Exception {
        // given
        AdminReservationRequest request = new AdminReservationRequest(
                LocalDate.now().plusDays(1),
                "timeId1",
                "themeId1",
                "memberId1"
        );
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        given(reservationService.addAndGetWithoutPayment(request)).willThrow(
                new ReservationExistsException());
        // when
        ResultActions result = mockMvc.perform(post("/admin/reservations")
                .cookie(new Cookie("authToken", token.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(
                        jsonPath("$.message").value("예약이 존재합니다."))
                .andDo(document("reservation/admin-create-reservation/error/reservation-exists-exception",
                        responseFields(
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드")
                        )
                ));
    }

    @Test
    void 조건을_만족하는_예약_조회에_성공한다() throws Exception {
        // given
        ReservationCondition condition = new ReservationCondition(
                "themeId1",
                "userId1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 7)
        );
        ReservationResponse reservation1 = new ReservationResponse(
                "reservationId1",
                new MemberResponse("memberId1", "name", "email1@domain.com"),
                LocalDate.of(2025, 1, 1),
                new TimeSlotResponse("timeSlotId1", LocalTime.of(9, 0)),
                new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1", 1000L)
        );
        List<ReservationResponse> response = List.of(reservation1);
        AuthToken token = jwtUtil.createToken(
                Member.create("name", "email1@domain.com", "password1"));
        given(reservationService.findAllReservations(condition)).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/reservations")
                .param("themeId", "themeId1")
                .param("userId", "userId1")
                .param("dateFrom", "2025-01-01")
                .param("dateTo", "2025-01-07")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value("reservationId1"),
                        jsonPath("$[0].date").value("2025-01-01"))
                .andDo(document("find-reservations",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 리스트"),
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("[].theme").type(JsonFieldType.OBJECT).description("테마 정보"),
                                fieldWithPath("[].theme.id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("테마 썸네일 URL"),
                                fieldWithPath("[].theme.price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("[].user").type(JsonFieldType.OBJECT).description("예약자 정보"),
                                fieldWithPath("[].user.id").type(JsonFieldType.STRING).description("예약자 ID"),
                                fieldWithPath("[].user.name").type(JsonFieldType.STRING).description("예약자 이름"),
                                fieldWithPath("[].user.email").type(JsonFieldType.STRING).description("예약자 이메일"),
                                fieldWithPath("[].time").type(JsonFieldType.OBJECT).description("예약 시간 정보"),
                                fieldWithPath("[].time.id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("예약 시간")
                        )
                ));
    }

    @Test
    void 내_예약_조회에_성공한다() throws Exception {
        // given
        ReservationWithPaymentResponse reservation1 = new ReservationWithPaymentResponse(
                "reservationId1",
                new MemberResponse("memberId1", "name", "email1@domain.com"),
                LocalDate.of(2025, 1, 1),
                new TimeSlotResponse("timeSlotId1", LocalTime.of(9, 0)),
                new ThemeResponse("themeId1", "theme1", "description1", "thumbnail1", 1000L),
                new PaymentResponse("paymentId1", 1000L)
        );
        List<ReservationWithPaymentResponse> response = List.of(reservation1);
        AuthToken token = jwtUtil.createToken(
                Member.create("name", "email1@domain.com", "password1"));
        given(reservationService.getMyReservations(anyString())).willReturn(response);
        // when
        ResultActions result = mockMvc.perform(get("/reservations/me")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isOk())
                .andExpectAll(
                        jsonPath("$[0].id").value("reservationId1"),
                        jsonPath("$[0].date").value("2025-01-01"))
                .andDo(document("get-my-reservations",
                        responseFields(
                                fieldWithPath("[]").type(JsonFieldType.ARRAY).description("예약 리스트"),
                                fieldWithPath("[].id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("[].date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("[].theme").type(JsonFieldType.OBJECT).description("테마 정보"),
                                fieldWithPath("[].theme.id").type(JsonFieldType.STRING).description("테마 ID"),
                                fieldWithPath("[].theme.name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].theme.thumbnail").type(JsonFieldType.STRING)
                                        .description("테마 썸네일 URL"),
                                fieldWithPath("[].theme.price").type(JsonFieldType.NUMBER).description("가격"),
                                fieldWithPath("[].user").type(JsonFieldType.OBJECT).description("예약자 정보"),
                                fieldWithPath("[].user.id").type(JsonFieldType.STRING).description("예약자 ID"),
                                fieldWithPath("[].user.name").type(JsonFieldType.STRING).description("예약자 이름"),
                                fieldWithPath("[].user.email").type(JsonFieldType.STRING).description("예약자 이메일"),
                                fieldWithPath("[].time").type(JsonFieldType.OBJECT).description("예약 시간 정보"),
                                fieldWithPath("[].time.id").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("[].time.startAt").type(JsonFieldType.STRING).description("예약 시간"),
                                fieldWithPath("[].payment").type(JsonFieldType.OBJECT).description("결제 정보"),
                                fieldWithPath("[].payment.id").type(JsonFieldType.STRING).description("결제 ID"),
                                fieldWithPath("[].payment.amount").type(JsonFieldType.NUMBER).description("금액")
                        )
                ));
    }

    @Test
    void 예약_삭제에_성공한다() throws Exception {
        // given
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        // when
        ResultActions result = mockMvc.perform(delete("/reservations/1")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isNoContent())
                .andDo(document("reservation/delete-reservation/success"));
    }

    @Test
    void 존재하지_않는_예약을_삭제할_경우_400_에러가_발생한다() throws Exception {
        // given
        AuthToken token = jwtUtil.createToken(
                Member.restore("name", UserRole.ADMIN.name(), "admin", "email1@domain.com", "password1"));
        doThrow(new ReservationNotFoundException()).when(reservationService).cancelReservationAndPromoteWait("1");

        // when
        ResultActions result = mockMvc.perform(delete("/reservations/1")
                .cookie(new Cookie("authToken", token.value())));
        // then
        result.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("존재하지 않는 예약입니다."))
                .andDo(document("reservation/delete-reservation/error/reservation-not-found-exception",
                        responseFields(
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드")
                        )
                ));
    }
}
