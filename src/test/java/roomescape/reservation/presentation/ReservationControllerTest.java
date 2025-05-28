package roomescape.reservation.presentation;


import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.common.config.ReservationConfig;
import roomescape.common.config.TossPaymentClientConfig;
import roomescape.common.exception.PaymentException;
import roomescape.member.dto.request.LoginMember;
import roomescape.member.service.LoginService;
import roomescape.payment.client.TossPaymentTestConfig;
import roomescape.payment.client.dto.request.TossPaymentConfirmRequest;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.service.ReservationService;

import org.junit.jupiter.api.Test;

@WebMvcTest(ReservationController.class)
@Import(TossPaymentTestConfig.class)
class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private TossPaymentClient tossPaymentClient;

    @MockitoBean
    private LoginService loginService;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private ReservationConfig reservationConfig;

    @Test
    void 결제_승인_실패시_예외가_발생한다() throws Exception {
        // given
        TossPaymentConfirmRequest request = new TossPaymentConfirmRequest(
                "orderId",
                1000L,
                "invalidPaymentKey"
        );
        LoginMember loginMember = new LoginMember(1L, "포라");

        // when
        when(tossPaymentClient.confirmPayment(request))
                .thenThrow(new PaymentException("결제 실패!"));
        String jsonContent = objectMapper.writeValueAsString(request);

        // then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent)
                        .requestAttr("loginMember", loginMember))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(containsString("결제 실패!")));
    }
}
