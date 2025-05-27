package roomescape.reservation.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.common.argumentResolver.Login;
import roomescape.common.config.ReservationConfig;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.service.LoginService;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@WebMvcTest(ReservationController.class)
class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private RestTemplateBuilder restTemplateBuilder;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private ReservationConfig reservationConfig;

    @Test
    @DisplayName("paymentKey를 클라이언트에서 획득하지 않은 값으로 요청하면 예약 생성에 실패한다")
    void createReservation_WhenPaymentApiFails_ThrowsException() throws Exception {
        // given
        ReservationRequest request = new ReservationRequest(
                LocalDate.of(2024, 3, 20),
                1L,
                1L,
                "paymentKey123",
                "LISAorderId123",
                10000L
        );

        LoginMember loginMember = new LoginMember(1L, "test@test.com");

        // when & then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .requestAttr(Login.class.getName(), loginMember))
                .andExpect(status().isBadRequest());
    }
}
