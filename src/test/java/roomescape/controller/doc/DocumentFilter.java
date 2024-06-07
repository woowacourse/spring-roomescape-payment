package roomescape.controller.doc;

import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public enum DocumentFilter {
    SAVE_PAYMENT("savePayment",
            requestFields(
                    fieldWithPath("reservationId").description("결제할 예약 ID"),
                    fieldWithPath("paymentKey").description("토스 결제 승인을 위한 paymentKey"),
                    fieldWithPath("orderId").description("토스 결제 승인을 위한 orderId"),
                    fieldWithPath("amount").description("결제 금액")),
            responseFields(
                    fieldWithPath("id").description("생성된 결제의 ID"),
                    fieldWithPath("paymentKey").description("생성된 결제의 paymentKey"),
                    fieldWithPath("orderId").description("생성된 결제의 orderId"),
                    fieldWithPath("totalAmount").description("결제 금액의 합계")
            )
    ),
    LOGIN("login",
            requestFields(
                    fieldWithPath("email").description("로그인 할 사용자의 이메일"),
                    fieldWithPath("password").description("로그인 할 사용자의 비밀번호")
            ),
            responseCookies(cookieWithName("token").description("로그인된 사용자의 토큰"))
    ),
    NOT_MEMBER_LOGIN("notMemberLogin",
            requestFields(
                    fieldWithPath("email").description("로그인 할 사용자의 이메일"),
                    fieldWithPath("password").description("로그인 할 사용자의 비밀번호")
            )
    ),
    LOGOUT("logout",
            requestCookies(cookieWithName("token").description("유효한 사용자 토큰")),
            responseCookies(cookieWithName("token").description("토큰 제거를 위한 헤더"))
    ),
    ADMIN_SAVE_RESERVATION("saveReservation",
            requestCookies(cookieWithName("token").description("예약을 생성하는 사용자의 토큰")),
            requestFields(
                    fieldWithPath("date").description("방탈출 예약 날짜"),
                    fieldWithPath("timeId").description("방탈출 예약 시간 ID"),
                    fieldWithPath("themeId").description("방탈출 예약 테마 ID"),
                    fieldWithPath("memberId").description("예약한 사용자 ID"),
                    fieldWithPath("paymentKey").description("예약 결제의 paymentKey"),
                    fieldWithPath("orderId").description("예약 결제의 orderId"),
                    fieldWithPath("amount").description("결제된 금액")
            )
    ),
    USER_SAVE_RESERVATION("saveReservation",
            requestCookies(cookieWithName("token").description("예약을 생성하는 사용자의 토큰")),
            requestFields(
                    fieldWithPath("date").description("방탈출 예약 날짜"),
                    fieldWithPath("timeId").description("방탈출 예약 시간 ID"),
                    fieldWithPath("themeId").description("방탈출 예약 테마 ID"),
                    fieldWithPath("memberId").description("예약한 사용자 ID"),
                    fieldWithPath("paymentKey").description("예약 결제의 paymentKey"),
                    fieldWithPath("orderId").description("예약 결제의 orderId"),
                    fieldWithPath("amount").description("결제된 금액")
            )
    ),
    GET_TIMES("getTimes",
            responseFields(
                    fieldWithPath("[].id").description("방탈출 예약 시간 ID"),
                    fieldWithPath("[].startAt").description("방탈출 예약 시작 시간")
            )
    ),
    GET_THEMES("getThemes",
            responseFields(
                    fieldWithPath("[].id").description("방탈출 테마 ID"),
                    fieldWithPath("[].name").description("방탈출 테마 이름"),
                    fieldWithPath("[].description").description("방탈출 테마 설명"),
                    fieldWithPath("[].thumbnail").description("방탈출 테마 썸네일 URL")
            )
    );

    private final RestDocumentationFilter value;

    DocumentFilter(String identifier, Snippet... snippets) {
        this.value = RestAssuredRestDocumentation.document(identifier, snippets);
    }

    public RestDocumentationFilter getValue() {
        return value;
    }
}
