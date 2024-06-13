package roomescape.acceptance;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import roomescape.dto.payment.PaymentRequest;
import roomescape.dto.reservation.MemberReservationSaveRequest;
import roomescape.dto.reservation.ReservationSaveRequest;
import roomescape.exception.ExternalApiException;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.FieldDescriptorFixture.adminReservationFieldDescriptor;
import static roomescape.FieldDescriptorFixture.adminReservationListFieldDescriptor;
import static roomescape.FieldDescriptorFixture.adminReservationSaveFieldDescriptor;
import static roomescape.FieldDescriptorFixture.errorFieldDescriptor;
import static roomescape.FieldDescriptorFixture.memberReservationFieldDescriptor;
import static roomescape.FieldDescriptorFixture.memberReservationSaveFieldDescriptor;
import static roomescape.FieldDescriptorFixture.myReservationFieldDescriptor;
import static roomescape.FieldDescriptorFixture.myReservationListFieldDescriptor;
import static roomescape.FieldDescriptorFixture.payFieldDescriptor;
import static roomescape.FieldDescriptorFixture.reservationParameterDescriptor;
import static roomescape.FieldDescriptorFixture.tokenCookieDescriptor;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.TestFixture.DUMMY_PAYMENT_RESPONSE;
import static roomescape.TestFixture.MEMBER_CAT_EMAIL;
import static roomescape.TestFixture.ORDER_ID;
import static roomescape.TestFixture.PAYMENT_KEY;

class ReservationAcceptanceTest extends AcceptanceTest {

    @Nested
    @DisplayName("예약 조회")
    class Get {

        @Test
        @DisplayName("[관리자] 테마, 사용자, 예약 날짜로 예약 목록을 성공적으로 조회하면 200을 응답한다.")
        void respondOkWhenFilteredFindReservations() {
            final String accessToken = getAccessToken(ADMIN_EMAIL);

            given(spec)
                    .filter(document("reservation/admin/findByCondition",
                            requestCookies(tokenCookieDescriptor),
                            queryParameters(reservationParameterDescriptor),
                            responseFields(adminReservationListFieldDescriptor)))
                    .queryParam("themeId", 1L)
                    .queryParam("memberId", 2L)
                    .queryParam("dateFrom", "2034-05-08")
                    .queryParam("dateTo", "2034-05-10")
                    .cookie("token", accessToken)
                    .when().get("/admin/reservations")
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("[공통] 예약 목록을 성공적으로 조회하면 200을 응답한다.")
        void respondOkWhenFindReservations() {
            given(spec)
                    .filter(document("reservation/findAll",
                            responseFields(adminReservationListFieldDescriptor)))
                    .when()
                    .get("/reservations")
                    .then()
                    .statusCode(200);
        }

        @Test
        @DisplayName("[공통] 내 예약을 성공적으로 조회하면 200을 응답한다.")
        void respondOkWhenMemberFindMyReservation() {
            final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);

            given(spec)
                    .filter(document("reservation/findMine",
                            requestCookies(tokenCookieDescriptor),
                            responseFields(myReservationListFieldDescriptor)
                    ))
                    .cookie("token", accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when().get("/reservations/mine")
                    .then()
                    .statusCode(200);
        }

    }

    @Nested
    @DisplayName("예약 생성")
    class Create {

        final Long timeId = saveReservationTime();
        final Long themeId = saveTheme();

        @Test
        @DisplayName("[사용자] 예약을 성공적으로 생성하면 201을 응답한다.")
        void respondCreatedWhenCreateReservation() {
            final MemberReservationSaveRequest request = new MemberReservationSaveRequest(DATE_MAY_EIGHTH, timeId, themeId, null, null, null);
            final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);

            given(spec)
                    .filter(document("reservation/create/success",
                            requestCookies(tokenCookieDescriptor),
                            requestFields(memberReservationSaveFieldDescriptor),
                            responseFields(memberReservationFieldDescriptor)))
                    .cookie("token", accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/reservations")
                    .then()
                    .statusCode(201);
        }

        @Test
        @DisplayName("[사용자] 결제가 실패하면 예약이 생성되지 않는다.")
        void throwExceptionWhenFailPayment() {
            // given
            LocalDate now = LocalDate.now();
            final MemberReservationSaveRequest request = new MemberReservationSaveRequest(now, 1L, 1L, null, null, null);
            given(paymentClient.pay(any())).willThrow(new ExternalApiException("결제 승인 서버에 문제가 있습니다."));

            // when
            given(spec)
                    .filter(document("reservation/create/fail/pay",
                            requestFields(memberReservationSaveFieldDescriptor),
                            responseFields(errorFieldDescriptor)
                    ))
                    .cookie("token", getAccessToken(MEMBER_CAT_EMAIL))
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when()
                    .post("/reservations")
                    .then()
                    .body(containsString("Error"));
        }

        @Test
        @DisplayName("[관리자] 예약을 성공적으로 생성하면 201을 응답한다.")
        void respondCreatedWhenAdminCreateReservation() {
            final ReservationSaveRequest request = new ReservationSaveRequest(1L, DATE_MAY_EIGHTH, timeId, themeId);
            final String accessToken = getAccessToken(ADMIN_EMAIL);

            given(spec)
                    .filter(document("reservation/admin/create/success",
                            requestCookies(tokenCookieDescriptor),
                            requestFields(adminReservationSaveFieldDescriptor),
                            responseFields(adminReservationFieldDescriptor)
                    ))
                    .cookie("token", accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/reservations")
                    .then()
                    .statusCode(201);
        }

        @Test
        @DisplayName("[공통] 존재하지 않는 예약 시간으로 예약 생성 시 400을 응답한다.")
        void respondBadRequestWhenNotExistingReservationTime() {
            final MemberReservationSaveRequest request = new MemberReservationSaveRequest(DATE_MAY_EIGHTH, 0L, themeId, null, null, null);
            final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);

            given(spec)
                    .filter(document("reservation/create/fail/time",
                            requestCookies(tokenCookieDescriptor),
                            requestFields(memberReservationSaveFieldDescriptor),
                            responseFields(errorFieldDescriptor)
                    ))
                    .cookie("token", accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/reservations")
                    .then()
                    .statusCode(400);
        }

        @Test
        @DisplayName("[공통] 존재하지 않는 테마로 예약 생성 시 400을 응답한다.")
        void respondBadRequestWhenNotExistingTheme() {
            final MemberReservationSaveRequest request = new MemberReservationSaveRequest(DATE_MAY_EIGHTH, timeId, 0L, null, null, null);
            final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);

            given(spec)
                    .filter(document("reservation/create/fail/theme",
                            requestCookies(tokenCookieDescriptor),
                            requestFields(memberReservationSaveFieldDescriptor),
                            responseFields(errorFieldDescriptor)
                    ))
                    .cookie("token", accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(request)
                    .when()
                    .post("/reservations")
                    .then()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("예약 수정")
    class Update {

        @Test
        @DisplayName("예약 결제에 성공하면 200을 응답한다.")
        void responseOkWhenPayReservation() {
            Long reservationId = saveReservation();
            final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);
            given(paymentClient.pay(any())).willReturn(DUMMY_PAYMENT_RESPONSE());

            given(spec)
                    .filter(document("reservation/update/pay",
                            requestCookies(tokenCookieDescriptor),
                            pathParameters(parameterWithName("id").description("예약 아이디")),
                            requestFields(payFieldDescriptor),
                            responseFields(myReservationFieldDescriptor)))
                    .cookie("token", accessToken)
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .body(new PaymentRequest(ORDER_ID, 1000, PAYMENT_KEY))
                    .when()
                    .patch("/reservations/{id}", reservationId)
                    .then()
                    .statusCode(200);
        }
    }

    @Nested
    @DisplayName("예약 삭제")
    class Delete {

        @Test
        @DisplayName("[공통] 예약을 성공적으로 삭제하면 204를 응답한다.")
        void respondNoContentWhenDeleteReservation() {
            final Long reservationId = saveReservation();

            given(spec)
                    .filter(document("reservation/delete/success",
                            pathParameters(parameterWithName("id").description("예약 아이디"))
                    ))
                    .when()
                    .delete("/reservations/{id}", reservationId)
                    .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("[공통] 존재하지 않는 예약을 삭제하면 400을 응답한다.")
        void respondBadRequestWhenDeleteNotExistingReservation() {
            given(spec)
                    .filter(document("reservation/delete/fail",
                            pathParameters(parameterWithName("id").description("예약 아이디")),
                            responseFields(errorFieldDescriptor)
                    ))
                    .when()
                    .delete("/reservations/{id}", 0)
                    .then()
                    .statusCode(400);
        }
    }
}
