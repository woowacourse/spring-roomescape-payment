package roomescape.unit.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
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
import roomescape.auth.AuthToken;
import roomescape.auth.LoginInfo;
import roomescape.auth.jwt.JJWTJwtUtil;
import roomescape.auth.jwt.JwtUtil;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.UserRole;
import roomescape.business.service.PaymentService;
import roomescape.exception.reservation.ReservationExistsException;
import roomescape.presentation.api.PaymentApiController;
import roomescape.presentation.api.PaymentApproveRequest;
import roomescape.presentation.dto.request.PaymentAndReservationRequest;
import roomescape.presentation.dto.response.PaymentResponse;

@WebMvcTest(value = {PaymentApiController.class, JJWTJwtUtil.class})
@ExtendWith(RestDocumentationExtension.class)
public class PaymentApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

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
        Member member = Member.restore("memberId1", UserRole.USER.name(), "name", "email1@domain.com", "password1");
        PaymentAndReservationRequest request = new PaymentAndReservationRequest(LocalDate.now().plusDays(1),
                "timeSlotId1", "themeId1");
        LoginInfo loginInfo = new LoginInfo(member.getId().id(), member.getRole());
        PaymentResponse response = new PaymentResponse("paymentId", 1000L);
        given(paymentService.createPaymentAndReservation(loginInfo, request)).willReturn(response);
        AuthToken token = jwtUtil.createToken(member);
        // when
        ResultActions result = mockMvc.perform(post("/payments")
                .cookie(new Cookie("authToken", token.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isCreated())
                .andExpect(header().string("Location", "/payments/paymentId"))
                .andDo(document("payment/create-payment/success",
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("themeId").type(JsonFieldType.STRING).description("테마 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.STRING).description("결제 ID"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액")
                        )
                ));
    }

    @Test
    void 중복_예약이면_결제_생성에_실패하여_400에러를_응답한다() throws Exception {
        // given
        Member member = Member.restore("memberId1", UserRole.USER.name(), "name", "email1@domain.com", "password1");
        PaymentAndReservationRequest request = new PaymentAndReservationRequest(LocalDate.now().plusDays(1),
                "timeSlotId1", "themeId1");
        LoginInfo loginInfo = new LoginInfo(member.getId().id(), member.getRole());
        AuthToken token = jwtUtil.createToken(member);
        given(paymentService.createPaymentAndReservation(loginInfo, request)).willThrow(
                new ReservationExistsException());
        // when
        ResultActions result = mockMvc.perform(post("/payments")
                .cookie(new Cookie("authToken", token.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("예약이 존재합니다."))
                .andDo(document("payment/create-payment/error/reservation-exists",
                        requestFields(
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("timeId").type(JsonFieldType.STRING).description("예약 시간 ID"),
                                fieldWithPath("themeId").type(JsonFieldType.STRING).description("테마 ID")
                        ),
                        responseFields(
                                fieldWithPath("timestamp").type(JsonFieldType.STRING).description("타임스탬프"),
                                fieldWithPath("message").type(JsonFieldType.STRING).description("에러 메시지"),
                                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드")
                        )
                ));
    }

    @Test
    void 결제_승인에_성공한다() throws Exception {
        // given
        Member member = Member.restore("memberId1", UserRole.USER.name(), "name", "email1@domain.com", "password1");
        PaymentApproveRequest request = new PaymentApproveRequest("paymentKey", 1000L);
        AuthToken token = jwtUtil.createToken(member);
        // when
        ResultActions result = mockMvc.perform(patch("/payments/{paymentId}", "paymentId1")
                .cookie(new Cookie("authToken", token.value()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));
        // then
        result.andExpect(status().isNoContent())
                .andDo(document("payment/approve-payment/success",
                        requestFields(
                                fieldWithPath("paymentKey").type(JsonFieldType.STRING).description("paymentKey"),
                                fieldWithPath("amount").type(JsonFieldType.NUMBER).description("결제 금액")
                        )
                ));
    }
}
