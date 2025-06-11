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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.controller.AuthAdminInterceptor;
import roomescape.controller.AuthArgumentResolver;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.time.ReservationTimeCreateRequestDto;
import roomescape.dto.time.ReservationTimeResponseDto;
import roomescape.service.command.ReservationTimeCommandService;
import roomescape.service.query.ReservationTimeQueryService;
import roomescape.util.JwtTokenProvider;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReservationTimeController.class)
@ActiveProfiles("test")
class ReservationTimeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    ReservationTimeQueryService reservationTimeQueryService;

    @MockitoBean
    ReservationTimeCommandService reservationTimeCommandService;

    @MockitoBean
    AuthArgumentResolver authArgumentResolver;

    @MockitoBean
    AuthAdminInterceptor authAdminInterceptor;

    @BeforeEach
    void setUp() {
        when(authArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(authArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenReturn(new roomescape.dto.auth.LoginInfo(new Member(1L, "가이온", "hello@woowa.com", Role.ADMIN, "password")));
        when(authAdminInterceptor.preHandle(any(), any(), any())).thenReturn(true);
    }

    @Test
    void timesTest() throws Exception {
        when(reservationTimeQueryService.findAllReservationTimes()).thenReturn(List.of(new ReservationTimeResponseDto(1L, LocalTime.of(10, 0))));
        mockMvc.perform(get("/times")
                .cookie(new Cookie("token", "mockToken")))
                .andExpect(status().isOk());
    }

    @Nested
    @DisplayName("예약시간 생성")
    class ReservationTimePostTest {

        @DisplayName("Time 입력 테스트")
        @Test
        void addReservationTimeTest() throws Exception {
            LocalTime reservationTime = LocalTime.of(15, 30);
            ReservationTimeCreateRequestDto requestTime = new ReservationTimeCreateRequestDto(reservationTime);
            mockMvc.perform(post("/times")
                    .cookie(new Cookie("token", "mockToken"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestTime)))
                    .andExpect(status().isCreated());
        }

        @DisplayName("올바른 시간의 포멧만 요청 가능하다.")
        @Test
        void invalidRequestTimeTest1() throws Exception {
            Map<String, String> params = new HashMap<>();
            params.put("startAt", "15:40:00");
            mockMvc.perform(post("/times")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params)))
                    .andExpect(status().isInternalServerError());
        }

        @DisplayName("유효한 시간만 생성 가능하다")
        @Test
        void invalidRequestTimeTest2() throws Exception {
            Map<String, String> params = new HashMap<>();
            params.put("startAt", "25:40");
            mockMvc.perform(post("/times")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(params)))
                    .andExpect(status().isInternalServerError());
        }
    }

    @Nested
    @DisplayName("예약시간 삭제")
    class DeleteReservationTimeTest {

        @DisplayName("저장된 Id 제거 테스트")
        @Test
        void deleteTimeTest() throws Exception {
            mockMvc.perform(delete("/times/2")
                    .cookie(new Cookie("token", "mockToken")))
                    .andExpect(status().isNoContent());
        }

        @DisplayName("존재하지 않는 Id의 Time 을 삭제할 수 없다")
        @Test
        void invalidTimeIdTest() throws Exception {
            doThrow(new roomescape.exception.NotFoundException("존재하지 않는 예약 시간입니다."))
                .when(reservationTimeCommandService).deleteReservationTimeById(5L);
            mockMvc.perform(delete("/times/5")
                    .cookie(new Cookie("token", "mockToken")))
                    .andExpect(status().isNotFound());
        }
    }
}
