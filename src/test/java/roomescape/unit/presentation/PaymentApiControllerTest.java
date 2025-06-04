package roomescape.unit.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.auth.jwt.JJWTJwtUtil;
import roomescape.business.service.PaymentService;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.DuplicatedException;
import roomescape.presentation.api.PaymentApiController;
import roomescape.presentation.dto.request.PaymentRequest;

@WebMvcTest(value = {PaymentApiController.class, JJWTJwtUtil.class})
public class PaymentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void 결제_생성에_성공한다() throws Exception {
        // given
        PaymentRequest request = new PaymentRequest("orderId", 1000L);
        given(paymentService.createPayment(request)).willReturn("paymentId");
        // when & then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    void 중복_결제로_결제_생성에_실패하여_400에러를_응답한다() throws Exception {
        // given
        PaymentRequest request = new PaymentRequest("orderId", 1000L);
        given(paymentService.createPayment(request)).willThrow(new DuplicatedException(ErrorCode.PAYMENT_DUPLICATED));
        // when & then
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
