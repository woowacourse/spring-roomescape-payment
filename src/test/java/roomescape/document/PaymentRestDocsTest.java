package roomescape.document;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.controller.PaymentController;
import roomescape.document.config.RestDocsSupport;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.service.PaymentService;

@WebMvcTest(PaymentController.class)
public class PaymentRestDocsTest extends RestDocsSupport {

    @MockBean
    private PaymentService paymentService;

    @Test
    void savePayment() throws Exception {
        long reservationId = 1L;
        long paymentId = 1L;

        PaymentRequest request = new PaymentRequest(
                reservationId,
                "paymentKey",
                "orderId",
                BigDecimal.valueOf(1000)
        );
        PaymentResponse response = new PaymentResponse(
                paymentId,
                "paymentKey",
                BigDecimal.valueOf(1000)
        );

        given(paymentService.savePayment(any()))
                .willReturn(response);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestFields(
                                fieldWithPath("reservationId").description("결제할 예약의 id"),
                                fieldWithPath("paymentKey").description("paymentKey"),
                                fieldWithPath("orderId").description("주문 id"),
                                fieldWithPath("amount").description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("id").description("결제 id"),
                                fieldWithPath("paymentKey").description("paymentKey"),
                                fieldWithPath("amount").description("결제 금액")
                        )
                ));
    }
}
