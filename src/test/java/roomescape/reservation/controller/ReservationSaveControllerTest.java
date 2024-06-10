package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.Fixture.JOJO_RESERVATION;
import static roomescape.common.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.common.util.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.common.config.ControllerTest;
import roomescape.common.util.CookieUtils;
import roomescape.payment.domain.Payment;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.controller.dto.request.ReservationPaymentSaveRequest;

@AutoConfigureMockMvc
class ReservationSaveControllerTest extends ControllerTest {

    private static final String ROOT_IDENTIFIER = "reservation";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @DisplayName("회원이 예약을 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveMemberReservation() throws Exception {
        memberJdbcUtil.saveMemberAsKaki();
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();

        ReservationPaymentSaveRequest reservationPaymentSaveRequest = new ReservationPaymentSaveRequest(
                LocalDate.now(),
                1L,
                1L,
                "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                "MC4wODU4ODQwMzg4NDk0",
                1000L
        );

        when(paymentService.confirm(any(), any())).thenReturn(
                new Payment(
                        1L,
                        "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                        "5EnNZRJGvaBX7zk2yd8ydw26XvwXkLrx9POLqKQjmAw4b0e1",
                        1000L,
                        JOJO_RESERVATION
                )
        );

        mockMvc.perform(post("/reservations")
                        .cookie(new Cookie(CookieUtils.TOKEN_KEY, getMemberToken()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationPaymentSaveRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "/reservations/1"))
                .andDo(document(ROOT_IDENTIFIER + "/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestCookies(cookieWithName("token").description("로그인 유저 토큰")),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("themeId").description("테마 식별자"),
                                fieldWithPath("timeId").description("시간 식별자"),
                                fieldWithPath("paymentKey").description("결제 키"),
                                fieldWithPath("orderId").description("주문 식별자"),
                                fieldWithPath("amount").description("결제 금액")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("식별자"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("member.name").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 이미지 url"),
                                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("시간 식별자"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("예약 상태")
                        )
                ));
    }
}
