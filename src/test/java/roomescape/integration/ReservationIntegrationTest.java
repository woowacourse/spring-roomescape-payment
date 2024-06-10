package roomescape.integration;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.domain.member.Member;
import roomescape.domain.payment.PaymentStatus;
import roomescape.domain.payment.PaymentType;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.exception.payment.PaymentConfirmErrorCode;
import roomescape.exception.payment.PaymentConfirmException;
import roomescape.service.payment.PaymentClient;
import roomescape.service.payment.dto.PaymentConfirmOutput;

class ReservationIntegrationTest extends IntegrationTest {
    @MockBean
    private PaymentClient paymentClient;

    @Nested
    @DisplayName("예약 목록 조회 API")
    class FindAllReservation {
        List<ParameterDescriptor> reservationFindAllQueryParameterDescriptors = List.of(
                parameterWithName("memberId").description("회원 id").optional(),
                parameterWithName("themeId").description("테마 id").optional(),
                parameterWithName("dateFrom").description("시작 날짜").optional(),
                parameterWithName("dateTo").description("종료 날짜").optional()
        );
        List<FieldDescriptor> reservationFindAllResponseDescriptors = List.of(
                fieldWithPath("reservations.[].id").description("예약 id"),
                fieldWithPath("reservations.[].member.id").description("회원 id"),
                fieldWithPath("reservations.[].member.name").description("회원 이름"),
                fieldWithPath("reservations.[].member.email").description("회원 이메일"),
                fieldWithPath("reservations.[].date").description("날짜"),
                fieldWithPath("reservations.[].time.id").description("예약 시간 id"),
                fieldWithPath("reservations.[].time.startAt").description("예약 시작 시간"),
                fieldWithPath("reservations.[].theme.id").description("테마 id"),
                fieldWithPath("reservations.[].theme.name").description("테마 이름"),
                fieldWithPath("reservations.[].theme.description").description("테마 설명"),
                fieldWithPath("reservations.[].theme.thumbnail").description("테마 썸네일 url")
        );
        Theme firstTheme;
        Member user;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            firstTheme = themeFixture.createFirstTheme();
            user = memberFixture.createUserMember();
            Theme secondTheme = themeFixture.createSecondTheme();
            Member admin = memberFixture.createAdminMember();
            reservationFixture.createPastReservation(reservationTime, firstTheme, user);
            reservationFixture.createFutureReservation(reservationTime, firstTheme, admin);
            reservationFixture.createPastReservation(reservationTime, secondTheme, user);
            reservationFixture.createFutureReservation(reservationTime, secondTheme, admin);
        }

        @Test
        void 예약_목록을_예약자와_테마와_기간별로_필터링해_조회할_수_있다() {
            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-find-all-success",
                            queryParameters(reservationFindAllQueryParameterDescriptors),
                            responseFields(reservationFindAllResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .when()
                    .get("/reservations?memberId={memberId}&themeId={themeId}&dateFrom=2000-04-01&dateTo=2000-04-07",
                            user.getId(), firstTheme.getId())
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(1));
        }

        @Test
        void 필터링_없이_전체_예약_목록을_조회할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations")
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(4));
        }

        @Test
        void 예약_목록을_예약자별로_필터링해_조회할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations?memberId={memberId}", user.getId())
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(2));
        }

        @Test
        void 예약_목록을_테마별로_필터링해_조회할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations?themeId={themeId}", firstTheme.getId())
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(2));
        }

        @Test
        void 예약_목록을_기간별로_필터링해_조회할_수_있다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations?dateFrom={dateFrom}&dateTo={dateTo}", "2000-04-01", "2000-04-07")
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(2));
        }
    }

    @Nested
    @DisplayName("내 예약 목록 조회 API")
    class FindMyReservation {
        List<FieldDescriptor> reservationFindMineResponseDescriptors = List.of(
                fieldWithPath("reservations.[].reservationId").description("예약 id"),
                fieldWithPath("reservations.[].theme").description("테마 이름"),
                fieldWithPath("reservations.[].date").description("예약 날짜"),
                fieldWithPath("reservations.[].time").description("예약 시작 시간"),
                fieldWithPath("reservations.[].status").description("예약 상태: 예약, 대기"),
                fieldWithPath("reservations.[].paymentKey").description("결제 키").optional(),
                fieldWithPath("reservations.[].totalAmount").description("결제 금액").optional()
        );

        @Test
        void 내_예약_목록을_조회할_수_있다() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Theme firstTheme = themeFixture.createFirstTheme();
            Theme secondTheme = themeFixture.createSecondTheme();
            Member user = memberFixture.createUserMember();
            Member admin = memberFixture.createAdminMember();
            Reservation firstReservation = reservationFixture.createFutureReservation(
                    reservationTime, firstTheme, user);
            Reservation secondReservation = reservationFixture.createFutureReservation(
                    reservationTime, secondTheme, admin);
            waitingFixture.createWaiting(secondReservation, user);
            reservationPaymentFixture.createReservationPayment(firstReservation);

            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-find-mine-success",
                            responseFields(reservationFindMineResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createUserCookies())
                    .when().get("/reservations/mine")
                    .then().log().all()
                    .statusCode(200)
                    .body("reservations.size()", is(2));
        }
    }

    @Nested
    @DisplayName("사용자 예약 추가 API")
    class SaveReservation {
        List<FieldDescriptor> reservationSaveRequestDescriptors = List.of(
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("themeId").description("테마 id"),
                fieldWithPath("timeId").description("예약 시간 id"),
                fieldWithPath("paymentKey").description("결제 키"),
                fieldWithPath("orderId").description("주문 id"),
                fieldWithPath("amount").description("결제 금액")
        );
        List<FieldDescriptor> reservationSaveResponseDescriptors = List.of(
                fieldWithPath("id").description("예약 id"),
                fieldWithPath("member.id").description("회원 id"),
                fieldWithPath("member.name").description("회원 이름"),
                fieldWithPath("member.email").description("회원 이메일"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("time.id").description("예약 시간 id"),
                fieldWithPath("time.startAt").description("예약 시작 시간"),
                fieldWithPath("theme.id").description("테마 id"),
                fieldWithPath("theme.name").description("테마 이름"),
                fieldWithPath("theme.description").description("테마 설명"),
                fieldWithPath("theme.thumbnail").description("테마 썸네일 url")
        );
        ReservationTime reservationTime;
        Theme theme;
        Member member;
        Map<String, String> params = new HashMap<>();

        @BeforeEach
        void setUp() {
            theme = themeFixture.createFirstTheme();
            reservationTime = reservationTimeFixture.createFutureReservationTime();
            member = memberFixture.createUserMember();
            params.put("themeId", theme.getId().toString());
            params.put("timeId", reservationTime.getId().toString());
            params.put("paymentKey", "testPaymentKey");
            params.put("orderId", "testOrderId");
            params.put("amount", "1000");
        }

        @Test
        void 결제_성공_시_예약을_추가할_수_있다() {
            params.put("date", "2000-04-07");
            PaymentConfirmOutput paymentConfirmOutput = new PaymentConfirmOutput(
                    "paymentKey", PaymentType.NORMAL, "orderId", "orderName",
                    "KRW", "간편결제", 1000L, PaymentStatus.DONE);
            given(paymentClient.confirmPayment(any()))
                    .willReturn(paymentConfirmOutput);

            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-save-success",
                            requestFields(reservationSaveRequestDescriptors),
                            responseFields(reservationSaveResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(201)
                    .header("Location", "/reservations/1")
                    .body("id", is(1));
        }

        @Test
        void 결제_실패_시_예약을_추가할_수_없다() {
            params.put("date", "2000-04-07");
            given(paymentClient.confirmPayment(any()))
                    .willThrow(new PaymentConfirmException(PaymentConfirmErrorCode.UNKNOWN_PAYMENT_ERROR));

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(500);
        }

        @Test
        void 필드가_빈_값이면_예약을_추가할_수_없다() {
            params.put("date", null);

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(400);
        }

        @Test
        void 날짜의_형식이_다르면_예약을_추가할_수_없다() {
            params.put("date", "2000-13-07");

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(400);
        }

        @Test
        void 시간대와_테마가_똑같은_중복된_예약은_추가할_수_없다() {
            Reservation reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
            params.put("date", reservation.getDate().toString());

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-save-duplicate"))
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(409);
        }

        @Test
        void 지나간_날짜와_시간에_대한_예약은_추가할_수_없다() {
            params.put("date", "2000-04-06");

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-save-past"))
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("관리자 예약 추가 API")
    class SaveAdminReservation {
        List<FieldDescriptor> reservationAdminSaveRequestDescriptors = List.of(
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("themeId").description("테마 id"),
                fieldWithPath("timeId").description("예약 시간 id"),
                fieldWithPath("memberId").description("회원 id")
        );
        List<FieldDescriptor> reservationAdminSaveResponseDescriptors = List.of(
                fieldWithPath("id").description("예약 id"),
                fieldWithPath("member.id").description("회원 id"),
                fieldWithPath("member.name").description("회원 이름"),
                fieldWithPath("member.email").description("회원 이메일"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("time.id").description("예약 시간 id"),
                fieldWithPath("time.startAt").description("예약 시작 시간"),
                fieldWithPath("theme.id").description("테마 id"),
                fieldWithPath("theme.name").description("테마 이름"),
                fieldWithPath("theme.description").description("테마 설명"),
                fieldWithPath("theme.thumbnail").description("테마 썸네일 url")
        );
        Map<String, String> params = new HashMap<>();

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Theme theme = themeFixture.createFirstTheme();
            Member member = memberFixture.createUserMember();
            memberFixture.createAdminMember();
            params.put("themeId", theme.getId().toString());
            params.put("timeId", reservationTime.getId().toString());
            params.put("memberId", member.getId().toString());
            params.put("date", "2000-04-07");
        }

        @Test
        void 관리자는_선택한_사용자_id로_예약을_추가할_수_있다() {
            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-admin-save-success",
                            requestFields(reservationAdminSaveRequestDescriptors),
                            responseFields(reservationAdminSaveResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(201)
                    .header("Location", "/reservations/1")
                    .body("id", is(1));
        }

        @Test
        void 관리자가_아닌_일반_사용자가_사용시_예외가_발생한다() {
            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("예약 삭제 API")
    class DeleteReservation {
        List<ParameterDescriptor> reservationDeletePathParameterDescriptors = List.of(
                parameterWithName("reservationId").description("예약 id")
        );
        List<ParameterDescriptor> reservationDeleteQueryParameterDescriptors = List.of(
                parameterWithName("memberId").description("회원 id")
        );
        Member member;
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Theme theme = themeFixture.createFirstTheme();
            member = memberFixture.createUserMember();
            reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
            memberFixture.createAdminMember();
        }

        @Test
        void 예약_id와_예약자_id로_예약을_삭제할_수_있다() {
            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-delete-success",
                            pathParameters(reservationDeletePathParameterDescriptors),
                            queryParameters(reservationDeleteQueryParameterDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/reservations/{reservationId}?memberId={memberId}",
                            reservation.getId(), member.getId())
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        void 존재하지_않는_예약_id로_예약을_삭제할_수_없다() {
            long wrongReservationId = 10L;

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-delete-not-found"))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/reservations/{reservationId}?memberId={memberId}",
                            wrongReservationId, member.getId())
                    .then().log().all()
                    .statusCode(404);
        }

        @Test
        void 예약자가_아닌_사용자_id로_예약을_삭제할_수_없다() {
            long wrongMemberId = 10L;

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-delete-bad-request"))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/reservations/{reservationId}?memberId={memberId}",
                            reservation.getId(), wrongMemberId)
                    .then().log().all()
                    .statusCode(400);
        }
    }
}
