package roomescape.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.AuthAdminInterceptor;
import roomescape.controller.AuthArgumentResolver;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.reservation.AdminReservationCreateRequestDto;
import roomescape.dto.reservation.ReservationResponseDto;
import roomescape.repository.JpaMemberRepository;
import roomescape.repository.JpaReservationRepository;
import roomescape.service.command.ReservationCommandService;
import roomescape.service.dto.ReservationCreateDto;
import roomescape.service.query.ReservationQueryService;
import roomescape.util.JwtTokenProvider;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminReservationController.class)
class AdminReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    JpaMemberRepository memberRepository;

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    ReservationQueryService reservationQueryService;

    @MockitoBean
    ReservationCommandService reservationCommandService;

    @MockitoBean
    AuthArgumentResolver authArgumentResolver;

    @MockitoBean
    AuthAdminInterceptor authAdminInterceptor;

    @MockitoBean
    JpaReservationRepository reservationRepository;

    String loginToken;

    @BeforeEach
    void setUp() {
        Member admin = new Member(2L, "moda", "moda@woowa.com", Role.ADMIN, "password");
        when(memberRepository.save(any(Member.class))).thenReturn(admin);
        when(jwtTokenProvider.createToken(any(Member.class))).thenReturn("mockToken");

        loginToken = "mockToken";

        when(authArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(new roomescape.dto.auth.LoginInfo(admin));

        when(authAdminInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Nested
    class AdminAddReservationTest {

        @DisplayName("어드민 예약 추가 테스트")
        @Test
        void addReservationTest() throws Exception {
            AdminReservationCreateRequestDto dto = new AdminReservationCreateRequestDto(
                    LocalDate.now().plusDays(1), 1L, 1L, 1L);
            ReservationCreateDto createDto = new ReservationCreateDto(dto.date(), dto.timeId(), dto.themeId(), dto.memberId());
            ReservationResponseDto responseDto = new ReservationResponseDto(1L, null, null, null, null, null);
            when(reservationCommandService.bookReservation(createDto)).thenReturn(responseDto);
            mockMvc.perform(post("/admin/reservations")
                    .cookie(new Cookie("token", "mockToken"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated());
        }
    }

    @DisplayName("특정 기간 내 예약을 조회할 수 있다")
    @Test
    void searchAdminReservationTest() throws Exception {
        when(reservationQueryService.searchReservationsBy(1L, 1L, java.time.LocalDate.parse("2025-05-01"), java.time.LocalDate.parse("2025-12-31")))
                .thenReturn(List.of(new ReservationResponseDto(1L, null, null, null, null, null)));
        mockMvc.perform(get("/admin/reservations/search")
                        .cookie(new Cookie("token", "mockToken"))
                        .param("themeId", "1")
                        .param("memberId", "1")
                        .param("dateFrom", "2025-05-01")
                        .param("dateTo", "2025-12-31"))
                .andExpect(status().isOk());
    }
}
