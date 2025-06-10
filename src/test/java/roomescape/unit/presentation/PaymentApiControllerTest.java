package roomescape.unit.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.ResultActions;
import roomescape.auth.AuthToken;
import roomescape.auth.LoginInfo;
import roomescape.business.model.entity.Member;
import roomescape.business.model.vo.UserRole;
import roomescape.exception.reservation.ReservationExistsException;
import roomescape.presentation.api.PaymentApproveRequest;
import roomescape.presentation.dto.request.PaymentAndReservationRequest;
import roomescape.presentation.dto.response.PaymentResponse;

public class PaymentApiControllerTest extends ControllerTest {

    private static long startTime;

    @BeforeAll
    static void startTimer() {
        startTime = System.currentTimeMillis();
    }

    @AfterAll
    static void endTimer() {
        long endTime = System.currentTimeMillis();
        System.out.println("🕖전체 테스트 실행 시간 = " + (endTime - startTime) + "ms");
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
