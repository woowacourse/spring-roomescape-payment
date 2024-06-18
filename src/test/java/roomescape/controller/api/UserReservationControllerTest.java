package roomescape.controller.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.dto.CreateReservationResponse;
import roomescape.controller.dto.CreateUserReservationRequest;
import roomescape.controller.dto.CreateUserReservationStandbyRequest;
import roomescape.controller.dto.FindMyReservationResponse;
import roomescape.controller.dto.PayStandbyRequest;
import roomescape.global.argumentresolver.AuthenticationPrincipalArgumentResolver;
import roomescape.global.auth.CheckRoleInterceptor;
import roomescape.global.auth.CheckUserInterceptor;
import roomescape.service.UserReservationService;

@AutoConfigureRestDocs
@WebMvcTest(UserReservationController.class)
class UserReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserReservationService userReservationService;

    @MockBean
    private AuthenticationPrincipalArgumentResolver argumentResolver;

    @MockBean
    private CheckRoleInterceptor checkRoleInterceptor;

    @MockBean
    private CheckUserInterceptor checkUserInterceptor;

    @BeforeEach
    void auth() {
        given(checkUserInterceptor.preHandle(any(), any(), any()))
            .willReturn(true);
    }

    @DisplayName("유저 예약 추가")
    @Test
    void save() throws Exception {
        given(userReservationService.reserve(any(), any()))
            .willReturn(new CreateReservationResponse(
                1L, "트레", LocalDate.parse("2060-01-01"), LocalTime.parse("10:00"), "방탈출 테마"));

        String request = objectMapper.writeValueAsString(
            new CreateUserReservationRequest(
                LocalDate.parse("2060-01-01"), 1L, 1L, "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "MC4wODU4ODQwMzg4NDk0", 10000));

        mockMvc.perform(post("/reservations")
                .content(request)
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andDo(document("reservations/save",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("date").description("예약할 날짜"),
                    fieldWithPath("themeId").description("테마 ID"),
                    fieldWithPath("timeId").description("예약 시간 ID"),
                    fieldWithPath("paymentKey").description("토스 API 결제 키 값"),
                    fieldWithPath("orderId").description("토스 API 주문번호"),
                    fieldWithPath("amount").description("결제 금액")
                ),
                responseFields(
                    fieldWithPath("id").description("예약 ID"),
                    fieldWithPath("memberName").description("예약을 요청한 회원명"),
                    fieldWithPath("date").description("이용일"),
                    fieldWithPath("time").description("이용 시간"),
                    fieldWithPath("themeName").description("예약 테마명")
                )
            ))
            .andExpect(status().isCreated());
    }

    @DisplayName("유저 예약대기 결제")
    @Test
    void payStandby() throws Exception {
        given(userReservationService.payStandby(any(), any()))
            .willReturn(new CreateReservationResponse(
                1L, "트레", LocalDate.parse("2060-01-01"), LocalTime.parse("10:00"), "방탈출 테마"));

        String request = objectMapper.writeValueAsString(new PayStandbyRequest(
            1L, "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1", "MC4wODU4ODQwMzg4NDk0", 1000));

        mockMvc.perform(post("/reservations/pay")
                .content(request)
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andDo(document("reservations/payStandby",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("reservationId").description("예약할 날짜"),
                    fieldWithPath("paymentKey").description("토스 API 결제 키 값"),
                    fieldWithPath("orderId").description("토스 API 주문번호"),
                    fieldWithPath("amount").description("결제 금액")
                ),
                responseFields(
                    fieldWithPath("id").description("예약 ID"),
                    fieldWithPath("memberName").description("예약을 요청한 회원명"),
                    fieldWithPath("date").description("이용일"),
                    fieldWithPath("time").description("이용 시간"),
                    fieldWithPath("themeName").description("예약 테마명")
                )
            ))
            .andExpect(status().isOk());
    }

    @DisplayName("유저 예약대기 추가")
    @Test
    void standby() throws Exception {
        given(userReservationService.standby(any(), any(), any(), any()))
            .willReturn(new CreateReservationResponse(
                1L, "트레", LocalDate.parse("2060-01-01"), LocalTime.parse("10:00"), "방탈출 테마"));

        String request = objectMapper.writeValueAsString(new CreateUserReservationStandbyRequest(
            LocalDate.parse("2060-01-01"), 1L, 1L));

        mockMvc.perform(post("/reservations/standby")
                .content(request)
                .contentType(APPLICATION_JSON))
            .andDo(print())
            .andDo(document("reservations/standby",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("date").description("예약대기할 날짜"),
                    fieldWithPath("themeId").description("테마 ID"),
                    fieldWithPath("timeId").description("시간 ID")
                ),
                responseFields(
                    fieldWithPath("id").description("예약 ID"),
                    fieldWithPath("memberName").description("예약대기를 요청한 회원명"),
                    fieldWithPath("date").description("이용일"),
                    fieldWithPath("time").description("이용 시간"),
                    fieldWithPath("themeName").description("예약 테마명")
                )))
            .andExpect(status().isCreated());
    }

    @DisplayName("유저 예약대기 삭제")
    @Test
    void deleteStandby() throws Exception {
        doNothing()
            .when(userReservationService)
            .deleteStandby(any(), any());

        mockMvc.perform(delete("/reservations/standby/{id}", 1))
            .andDo(print())
            .andDo(document("reservations/deleteStandby",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                pathParameters(
                    parameterWithName("id").description("삭제할 예약 ID")
                )))
            .andExpect(status().isNoContent());
    }

    @DisplayName("유저 본인의 예약 조회")
    @Test
    void findMyReservations() throws Exception {
        given(userReservationService.findMyReservationsWithRank(any()))
            .willReturn(List.of(
                new FindMyReservationResponse(
                    1L, "루터회관 탈출하기", LocalDate.parse("2060-01-01"), LocalTime.parse("10:00"),
                    "RESERVED", 0L, "tgen_202406081920558t506", 1000L, "간편결제"),
                new FindMyReservationResponse(
                    2L, "우리집 탈출하기", LocalDate.parse("2060-01-02"), LocalTime.parse("11:00"),
                    "RESERVED", 0L, "tgen_202406081921365GeO6", 1000L, "간편결제"),
                new FindMyReservationResponse(
                    3L, "교도소 탈출하기", LocalDate.parse("2060-01-03"), LocalTime.parse("12:00"),
                    "STANDBY", 2L, "tgen_20240608192207aaQt3", 1000L, "간편결제")
            ));

        mockMvc.perform(get("/reservations/mine"))
            .andDo(print())
            .andDo(document("reservations/findMyReservations",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("[].id").description("예약 ID"),
                    fieldWithPath("[].theme").description("테마 이름"),
                    fieldWithPath("[].date").description("이용일"),
                    fieldWithPath("[].time").description("이용 시간"),
                    fieldWithPath("[].status").description("예약 상태"),
                    fieldWithPath("[].rank").description("예약 대기번호"),
                    fieldWithPath("[].paymentKey").description("토스 API 결제 키 값"),
                    fieldWithPath("[].amount").description("결제 금액"),
                    fieldWithPath("[].payMethod").description("결제 수단")
                )
            ))
            .andExpect(status().isOk());
    }
}
