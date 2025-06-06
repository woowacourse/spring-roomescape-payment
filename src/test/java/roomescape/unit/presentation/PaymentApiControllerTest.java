package roomescape.unit.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.auth.jwt.JJWTJwtUtil;
import roomescape.business.service.PaymentService;
import roomescape.exception.ErrorCode;
import roomescape.exception.business.DuplicatedException;
import roomescape.presentation.api.PaymentApiController;
import roomescape.presentation.dto.request.PaymentRequest;

@WebMvcTest(value = {PaymentApiController.class, JJWTJwtUtil.class})
@ExtendWith(RestDocumentationExtension.class)
public class PaymentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PaymentService paymentService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

    @Test
    void 결제_생성에_성공한다() throws Exception {
        // given
        PaymentRequest request = new PaymentRequest("orderId", 1000L);
        given(paymentService.createPayment(request)).willReturn("paymentId");
        // when
        ResultActions result = mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/payments/paymentId"))
                .andDo(document("create-payment",
                        requestFields(
                                fieldWithPath("orderId").type(JsonFieldType.STRING).description("주문 ID"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("금액")
                        )
                ));
    }

    @Test
    void 중복_결제로_결제_생성에_실패하여_400에러를_응답한다() throws Exception {
        // given
        PaymentRequest request = new PaymentRequest("orderId", 1000L);
        given(paymentService.createPayment(request)).willThrow(new DuplicatedException(ErrorCode.PAYMENT_DUPLICATED));
        // when
        ResultActions result = mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exceptionType").value("PAYMENT_DUPLICATED"));
    }
}
