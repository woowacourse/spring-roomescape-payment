package roomescape.booking.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.JwtProvider;
import roomescape.auth.TokenBody;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.BookingService;
import roomescape.booking.reservation.dto.ReservationRequest;
import roomescape.booking.reservation.dto.ReservationResponse;
import roomescape.member.MemberRole;
import roomescape.member.dto.MemberResponse;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.schedule.dto.ScheduleResponse;
import roomescape.theme.dto.ThemeResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationCreateService reservationCreateService;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtProvider jwtProvider;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ReservationService reservationService() {
            return mock(ReservationService.class);
        }

        @Bean
        public ReservationCreateService reservationCreateService() {
            return mock(ReservationCreateService.class);
        }

        @Bean
        public BookingService bookingService() {
            return mock(BookingService.class);
        }

        @Bean
        public JwtProvider jwtProvider() {
            return mock(JwtProvider.class);
        }
    }

    @Test
    @DisplayName("예약 생성 요청에 성공할 경우 201을 응답한다")
    void create() throws Exception {
        // given
        ReservationRequest request = new ReservationRequest(LocalDate.now(), 1L, 1L, "pid_12345", "SURFMAY_12345", 1000L, "NORMAL");
        LoginMember loginMember = new LoginMember("사용자", "user@example.com", MemberRole.MEMBER);

        Map<String, Object> memberClaims = new HashMap<>();
        memberClaims.put("name", loginMember.name());
        memberClaims.put("email", loginMember.email());
        memberClaims.put("role", loginMember.role().name());
        Claims claims = Jwts.claims();
        claims.putAll(memberClaims);

        given(jwtProvider.isValidToken(any())).willReturn(true);
        given(jwtProvider.extractBody(any())).willReturn(new TokenBody(claims));

        MemberResponse memberResponse = new MemberResponse(1L, loginMember.name());
        ReservationTimeResponse timeResponse = new ReservationTimeResponse(1L, LocalTime.of(10, 0));
        ThemeResponse themeResponse = new ThemeResponse(1L, "듄2", "사막 행성에서 살아남기", "timothee.jpg");
        ScheduleResponse scheduleResponse = new ScheduleResponse(1L, LocalDate.now(), timeResponse, themeResponse);
        ReservationResponse response = new ReservationResponse(1L, scheduleResponse, memberResponse);

        given(reservationCreateService.create(any(ReservationRequest.class), any(LoginMember.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/reservations")
                        .cookie(new Cookie("token", "abc123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("create-reservation",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("themeId").description("예약 테마 id"),
                                fieldWithPath("timeId").description("예약 시간 id"),
                                fieldWithPath("paymentKey").description("토스 결제 요청 api로부터 응답받은 결제 Key"),
                                fieldWithPath("orderId").description("클라이언트에서 생성한 주문에 대한 식별값"),
                                fieldWithPath("amount").description("방탈출 금액"),
                                fieldWithPath("paymentType").description("토스 결제 상태")
                        ),
                        responseFields(
                                fieldWithPath("id").description("예약 ID"),

                                fieldWithPath("schedule").description("예약 스케줄 정보"),
                                fieldWithPath("member").description("예약자 정보"),

                                fieldWithPath("schedule.id").description("스케줄 ID"),
                                fieldWithPath("schedule.date").description("예약 날짜"),
                                fieldWithPath("schedule.time").description("예약 시간 정보"),
                                fieldWithPath("schedule.theme").description("예약 테마 정보"),

                                fieldWithPath("schedule.time.id").description("시간 ID"),
                                fieldWithPath("schedule.time.startAt").description("시작 시간"),

                                fieldWithPath("schedule.theme.id").description("테마 ID"),
                                fieldWithPath("schedule.theme.name").description("테마 이름"),
                                fieldWithPath("schedule.theme.description").description("테마 설명"),
                                fieldWithPath("schedule.theme.thumbnail").description("테마 썸네일 이미지 파일명"),

                                fieldWithPath("member.id").description("회원 ID"),
                                fieldWithPath("member.name").description("회원 이름")
                        )
                ));
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 예약 생성 요청은 401을 응답한다")
    void createUnauthorized() throws Exception {
        // given
        ReservationRequest request = new ReservationRequest(LocalDate.now(), 1L, 1L, "pid_12345", "SURFMAY_12345", 1000L, "NORMAL");
        given(jwtProvider.isValidToken(any())).willReturn(false);

        // when & then
        mockMvc.perform(post("/reservations")
                        .cookie(new Cookie("token", "invalid"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andDo(document("create-reservation-unauthorized",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("themeId").description("예약 테마 id"),
                                fieldWithPath("timeId").description("예약 시간 id"),
                                fieldWithPath("paymentKey").description("토스 결제 요청 api로부터 응답받은 결제 Key"),
                                fieldWithPath("orderId").description("클라이언트에서 생성한 주문에 대한 식별값"),
                                fieldWithPath("amount").description("방탈출 금액"),
                                fieldWithPath("paymentType").description("토스 결제 상태")
                        )));
    }

    @Test
    @DisplayName("예약 삭제 요청에 성공할 경우 204를 응답한다")
    void deleteById() throws Exception {
        // when & then
        mockMvc.perform(delete("/reservations/1"))
                .andExpect(status().isNoContent())
                .andDo(document("delete-reservation",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));

        verify(bookingService).deleteReservationById(1L);
    }
}
