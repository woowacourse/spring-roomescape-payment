package roomescape.reservation.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import roomescape.admin.AdminHandlerInterceptor;
import roomescape.login.LoginMemberArgumentResolver;
import roomescape.login.dto.Accessor;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.fixture.MemberReservationResponseFixture;
import roomescape.reservation.service.ReservationService;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReservationService reservationService;

    @MockBean
    private LoginMemberArgumentResolver loginMemberArgumentResolver;

    @MockBean
    private AdminHandlerInterceptor adminHandlerInterceptor;

    @DisplayName("로그인한 특정 유저의 예약 목록을 읽는 요청을 처리할 수 있다")
    @Test
    void should_handle_read_my_reservations_request_when_requested() throws Exception {
        List<MyReservationResponse> responses = List.of(
                MemberReservationResponseFixture.RESERVATION_1_WAITING_1,
                MemberReservationResponseFixture.RESERVATION_2_WAITING_1
        );

        when(loginMemberArgumentResolver.supportsParameter(any(MethodParameter.class))).thenReturn(true);
        when(loginMemberArgumentResolver.resolveArgument(
                any(MethodParameter.class),
                any(ModelAndViewContainer.class),
                any(NativeWebRequest.class),
                any(WebDataBinderFactory.class)
        )).thenReturn(new Accessor(1L));

        when(reservationService.findMyReservations(any(Long.class))).thenReturn(responses);

        mockMvc.perform(get("/reservations/my")
                        .cookie(new Cookie("mockCookie", "mockValue")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(responses)));
    }

    @DisplayName("예약 삭제 요청을 처리할 수 있다")
    @Test
    void should_handle_delete_reservation_when_requested() throws Exception {
        mockMvc.perform(delete("/reservations/{id}", 1))
                .andExpect(status().isNoContent());
    }
}
