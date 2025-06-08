package roomescape.restdocs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.fixture.TestFixture.RESERVATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.fixture.TestFixture;
import roomescape.member.service.MemberService;
import roomescape.reservation.controller.ReservationController;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;
import roomescape.time.controller.ReservationTimeController;
import roomescape.time.dto.AvailableReservationTimeResponse;
import roomescape.time.service.ReservationTimeService;
import roomescape.waiting.controller.ReservationWaitingController;
import roomescape.waiting.domain.ReservationWaiting;
import roomescape.waiting.dto.ReservationWaitingRequest;
import roomescape.waiting.dto.ReservationWaitingResponse;
import roomescape.waiting.service.ReservationWaitingService;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs
@WebMvcTest({ReservationController.class, ReservationTimeController.class, ReservationWaitingController.class})
class ReservationApiDocumentationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ReservationService reservationService;

    @MockitoBean
    MemberService memberService;

    @MockitoBean
    ReservationTimeService reservationTimeService;

    @MockitoBean
    ReservationWaitingService reservationWaitingService;

    @DisplayName("예약 승인을 요청한다.")
    @Test
    void addReservationTest() throws Exception {
        // given
        ReservationPaymentRequest request = new ReservationPaymentRequest(
                LocalDate.now().plusDays(1),
                1L,
                1L,
                "testPaymentKey",
                "testOrderId",
                1000,
                "NORMAL");
        ReservationResponse response = ReservationResponse.from(RESERVATION);
        when(reservationService.addReservation(any(Long.class), any(ReservationPaymentRequest.class)))
                .thenReturn(response);

        this.mockMvc.perform(post("/reservations")
                        .sessionAttr("id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.name").value(response.name()))
                .andExpect(jsonPath("$.date").value(response.date().toString()))
                .andExpect(jsonPath("$.time.id").value(response.time().id()))
                .andExpect(jsonPath("$.time.startAt").value(response.time().startAt().toString()))
                .andExpect(jsonPath("$.theme.id").value(response.theme().id()))
                .andExpect(jsonPath("$.theme.name").value(response.theme().name()))
                .andExpect(jsonPath("$.theme.description").value(response.theme().description()))
                .andExpect(jsonPath("$.theme.thumbnail").value(response.theme().thumbnail()))
                .andDo(document("reservations/post",
                        preprocessRequest(
                                prettyPrint()),
                        preprocessResponse(
                                prettyPrint()),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 ID"),
                                fieldWithPath("themeId").description("테마 ID"),
                                fieldWithPath("paymentKey").description("결제 키"),
                                fieldWithPath("orderId").description("주문 ID"),
                                fieldWithPath("amount").description("결제 금액"),
                                fieldWithPath("paymentType").description("결제 타입")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 ID"),
                                fieldWithPath("name").description("회원 이름"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("time.id").description("예약 시간 ID"),
                                fieldWithPath("time.startAt").description("예약 시작 시간"),
                                fieldWithPath("theme.id").description("테마 ID"),
                                fieldWithPath("theme.name").description("테마 이름"),
                                fieldWithPath("theme.description").description("테마 설명"),
                                fieldWithPath("theme.thumbnail").description("테마 썸네일")
                        )));

    }

    @DisplayName("날짜와 테마 선택 시 예약 시간을 조회한다.")
    @Test
    void getAvailableTimesTest() throws Exception {
        // given
        LocalDate date = LocalDate.now().plusDays(1);
        Long themeId = 1L;
        AvailableReservationTimeResponse time1 = new AvailableReservationTimeResponse(1L, LocalTime.of(10, 0), false);
        AvailableReservationTimeResponse time2 = new AvailableReservationTimeResponse(2L, LocalTime.of(11, 0), true);
        AvailableReservationTimeResponse time3 = new AvailableReservationTimeResponse(3L, LocalTime.of(12, 0), false);

        when(reservationTimeService.getAvailableTimes(any(LocalDate.class), any(Long.class)))
                .thenReturn(List.of(time1, time2, time3));

        this.mockMvc.perform(get("/times/available-times")
                        .param("date", date.toString())
                        .param("themeId", String.valueOf(themeId))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(time1.id()))
                .andExpect(jsonPath("$[0].startAt").value(time1.startAt().toString()))
                .andExpect(jsonPath("$[0].isBooked").value(time1.isBooked()))
                .andExpect(jsonPath("$[1].id").value(time2.id()))
                .andExpect(jsonPath("$[1].startAt").value(time2.startAt().toString()))
                .andExpect(jsonPath("$[1].isBooked").value(time2.isBooked()))
                .andExpect(jsonPath("$[2].id").value(time3.id()))
                .andExpect(jsonPath("$[2].startAt").value(time3.startAt().toString()))
                .andExpect(jsonPath("$[2].isBooked").value(time3.isBooked()))
                .andDo(document("times/available-times",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("date").description("예약 날짜"),
                                parameterWithName("themeId").description("테마 ID")
                        ),
                        responseFields(
                                fieldWithPath("[].id").description("예약 시간 ID"),
                                fieldWithPath("[].startAt").description("예약 시작 시간"),
                                fieldWithPath("[].isBooked").description("예약 여부")
                        )));
    }

    @DisplayName("예약 대기를 신청한다.")
    @Test
    void addReservationWaitingTest() throws Exception {
        // given
        ReservationWaitingRequest request = new ReservationWaitingRequest(
                LocalDate.now().plusDays(1),
                1L,
                1L);
        ReservationWaitingResponse response = ReservationWaitingResponse.from(TestFixture.RESERVATION_WAITING);
        when(reservationWaitingService.addReservationWaiting(any(ReservationWaitingRequest.class), any(Long.class)))
                .thenReturn(response);

        this.mockMvc.perform(post("/reservations-waiting")
                        .sessionAttr("id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.id()))
                .andExpect(jsonPath("$.date").value(response.date().toString()))
                .andExpect(jsonPath("$.time.id").value(response.time().id()))
                .andExpect(jsonPath("$.time.startAt").value(response.time().startAt().toString()))
                .andExpect(jsonPath("$.theme.id").value(response.theme().id()))
                .andExpect(jsonPath("$.theme.name").value(response.theme().name()))
                .andExpect(jsonPath("$.theme.description").value(response.theme().description()))
                .andExpect(jsonPath("$.theme.thumbnail").value(response.theme().thumbnail()))
                .andDo(document("reservations-waiting/post",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("timeId").description("예약 시간 ID"),
                                fieldWithPath("themeId").description("테마 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 대기 ID"),
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("time.id").description("예약 시간 ID"),
                                fieldWithPath("time.startAt").description("예약 시작 시간"),
                                fieldWithPath("theme.id").description("테마 ID"),
                                fieldWithPath("theme.name").description("테마 이름"),
                                fieldWithPath("theme.description").description("테마 설명"),
                                fieldWithPath("theme.thumbnail").description("테마 썸네일")
                        )));
    }

}