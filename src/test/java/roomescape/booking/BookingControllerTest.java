package roomescape.booking;

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
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.JwtProvider;
import roomescape.auth.TokenBody;
import roomescape.auth.dto.LoginMember;
import roomescape.booking.dto.BookingResponse;
import roomescape.member.MemberRole;
import roomescape.reservationpayment.dto.ReservationPaymentResponse;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.schedule.dto.ScheduleResponse;
import roomescape.theme.dto.ThemeResponse;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JwtProvider jwtProvider;

    @TestConfiguration
    static class TestConfig {
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
    @DisplayName("인증된 사용자의 모든 예약을 조회하면 200과 예약 목록을 응답한다")
    void readAllByMember() throws Exception {
        // given
        LoginMember loginMember = new LoginMember("사용자", "user@example.com", MemberRole.MEMBER);

        Map<String, Object> memberClaims = new HashMap<>();
        memberClaims.put("name", loginMember.name());
        memberClaims.put("email", loginMember.email());
        memberClaims.put("role", loginMember.role().name());
        Claims claims = Jwts.claims();
        claims.putAll(memberClaims);

        given(jwtProvider.isValidToken(any())).willReturn(true);
        given(jwtProvider.extractBody(any())).willReturn(new TokenBody(claims));

        ScheduleResponse scheduleResponse1 = new ScheduleResponse(
                1L,
                LocalDate.now().plusDays(1),
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ThemeResponse(1L, "듄", "사막 행성에서 살아남기", "timothee.jpg"));
        ScheduleResponse scheduleResponse2 = new ScheduleResponse(
                2L,
                LocalDate.now().plusDays(2),
                new ReservationTimeResponse(1L, LocalTime.of(10, 0)),
                new ThemeResponse(1L, "위키드", "감동적인 이야기", "galinda.jpg"));

        ReservationPaymentResponse paymentResponse = new ReservationPaymentResponse("pid-12345", 15000L);

        List<BookingResponse> responses = List.of(
                new BookingResponse(1L, scheduleResponse1, "예약", paymentResponse),
                new BookingResponse(2L, scheduleResponse2, "1번째 예약대기", null)
        );

        given(bookingService.readAllByMember(any(LoginMember.class))).willReturn(responses);

        // when & then
        mockMvc.perform(get("/bookings")
                        .cookie(new Cookie("token", "abc123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].status").value("예약"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].status").value("1번째 예약대기"))
                .andDo(document("read-bookings-of-member",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }

    @Test
    @DisplayName("인증된 사용자의 예약이 없을 경우 빈 목록을 응답한다")
    void readAllByMemberEmpty() throws Exception {
        // given
        LoginMember loginMember = new LoginMember("사용자", "user@example.com", MemberRole.MEMBER);

        Map<String, Object> memberClaims = new HashMap<>();
        memberClaims.put("name", loginMember.name());
        memberClaims.put("email", loginMember.email());
        memberClaims.put("role", loginMember.role().name());
        Claims claims = Jwts.claims();
        claims.putAll(memberClaims);

        given(jwtProvider.isValidToken(any())).willReturn(true);
        given(jwtProvider.extractBody(any())).willReturn(new TokenBody(claims));

        given(bookingService.readAllByMember(any(LoginMember.class))).willReturn(Collections.emptyList());

        // when & then
        mockMvc.perform(get("/bookings")
                        .cookie(new Cookie("token", "abc123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("인증되지 않은 사용자의 예약 조회 요청은 401을 응답한다")
    void readAllByMemberUnauthorized() throws Exception {
        // given
        given(jwtProvider.isValidToken(any())).willReturn(false);

        // when & then
        mockMvc.perform(get("/bookings")
                        .cookie(new Cookie("token", "invalid")))
                .andExpect(status().isUnauthorized())
                .andDo(document("read-bookings-of-member-unauthorized",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint())));
    }
}
