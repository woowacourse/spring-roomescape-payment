package roomescape.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.fixture.MemberFixture.MEMBER_ARU;
import static roomescape.fixture.ThemeFixture.TEST_THEME;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.response.ReservationResponse;
import roomescape.domain.payment.Payment;

class ReservationAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("예약을 생성한다.")
    void createReservation() {
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String token = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());
        fixture.mockPurchase(new Payment("orderId", "paymentKey", 10000L));

        CookieDescriptor[] requestCookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("memberId").description("회원 ID").type("Number").optional(),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("timeId").description("예약 시간 ID"),
                fieldWithPath("themeId").description("테마 ID"),
                fieldWithPath("paymentKey").description("결제 키"),
                fieldWithPath("orderId").description("주문 ID")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("id").description("예약 ID"),
                fieldWithPath("member").description("예약자 이름"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("startAt").description("예약 시간"),
                fieldWithPath("theme").description("테마")
        };

        RestDocumentationFilter documentFilter = document(
                "reservation-create",
                requestCookies(requestCookieDescriptors),
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors)
        );

        String request = """
                {
                    "themeId": %d,
                    "date": "2024-12-25",
                    "timeId": %d,
                    "paymentKey": "paymentKey",
                    "orderId": "orderId"
                }
                """.formatted(themeId, timeId);

        givenWithSpec().log().all()
                .cookie("token", token)
                .body(request)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(documentFilter)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("자신의 예약을 조회한다.")
    void findMyReservations() {
        long memberId = fixture.registerMember(MEMBER_ARU.registerRequest()).id();
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        String token = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());
        fixture.createReservation(
                token,
                new ReservationRequest(memberId, themeId, LocalDate.of(2024, 6, 1), timeId)
        );

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].id").description("예약 ID"),
                fieldWithPath("[].date").description("예약 날짜"),
                fieldWithPath("[].theme").description("테마"),
                fieldWithPath("[].startAt").description("예약 시간"),
                fieldWithPath("[].waitingCount").description("대기자 수"),
                fieldWithPath("[].payment.paymentKey").description("결제 키"),
                fieldWithPath("[].payment.amount").description("결제 금액"),
        };

        RestDocumentationFilter documentFilter = document(
                "reservation-find-my",
                requestCookies(cookieDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", token)
                .accept(APPLICATION_JSON_VALUE)
                .filter(documentFilter)
                .when().get("/reservations/me")
                .then().log().all()
                .statusCode(200)
                .body("size()", equalTo(1));
    }

    @Test
    @DisplayName("자신의 예약을 삭제한다.")
    void deleteReservation() {
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String token = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());
        ReservationResponse response = fixture.createReservation(
                token,
                new ReservationRequest(themeId, LocalDate.of(2024, 12, 25), timeId)
        );

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        ParameterDescriptor[] parameterDescriptors = {
                parameterWithName("id").description("예약 ID")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-delete",
                requestCookies(cookieDescriptors),
                pathParameters(parameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", token)
                .pathParam("id", response.id())
                .filter(docsFilter)
                .when().delete("/reservations/{id}")
                .then().log().all()
                .statusCode(204);
    }
}
