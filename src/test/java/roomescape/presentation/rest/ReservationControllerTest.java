package roomescape.presentation.rest;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.application.request.PaymentInfo;
import roomescape.application.response.PaymentClientResponse;
import roomescape.infrastructure.payment.PaymentClient;

class ReservationControllerTest {

    @Nested
    @Import(TestConfig.class)
    @DisplayName("사용자의 권한으로 예약을 추가한다.")
    class CreateReservationWithUserPrivileges extends RestDocsTestBase {

        @Autowired
        PaymentClient paymentClient;

        @Test
        @DisplayName("예약 추가 요청시, id를 포함한 예약 내용과 CREATED를 응답한다")
        void createReservationWithUserPrivileges() {

            Map<String, String> reservationBody = Map.of(
                    "date", "3000-03-17",
                    "timeId", "1",
                    "themeId", "1",
                    "memberId", "2",
                    "paymentKey", "paymentKey",
                    "orderId", "orderId",
                    "orderName", "orderName",
                    "amount", "1000"
            );

            PaymentInfo paymentInfo = new PaymentInfo("payment_key_1", "order_id_1", "order_name_1", 10000L);
            PaymentClientResponse response = new PaymentClientResponse(
                    "paymentKey",
                    "orderId",
                    "테스트 방탈출 예약 결제 1건",
                    1000
            );

            given(paymentClient.confirmPayment(paymentInfo)).willReturn(response);

            String token = getUserToken();

            RestAssured.given(spec).filter(createReservationWithUserPrivileges_Document()).log().all().contentType(
                            ContentType.JSON)
                    .cookie("token", token).body(reservationBody)
                    .when().post("/reservations")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value()).body("date", Matchers.equalTo("3000-03-17"));
        }

        @EnumSource(TossPaymentErrorCode.class)
        @ParameterizedTest
        @DisplayName("예약 추가 요청시, 결제에 실패하면 실패 에러 응답 코드를 반환한다")
        void createReservationWithUserPrivileges_WhenPaymentFailed(TossPaymentErrorCode errorCode) {

            Map<String, String> reservationBody = Map.of(
                    "date", "3000-03-17",
                    "timeId", "1",
                    "themeId", "1",
                    "memberId", "2",
                    "paymentKey", "paymentKey",
                    "orderId", "orderId",
                    "amount", "1000"
            );

            PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "orderId", 1000);

            given(paymentClient.confirmPayment(paymentInfo)).willThrow(new TossPaymentException(errorCode));

            String token = getUserToken();

            RestAssured.given().log().all().contentType(ContentType.JSON).cookie("token", token).body(reservationBody)
                    .when().post("/reservations")
                    .then().log().all().statusCode(errorCode.getHttpStatus().value()).body(
                            "message",
                            Matchers.equalTo(errorCode.getMessage())
                    );
        }

        RestDocumentationFilter createReservationWithUserPrivileges_Document() {
            FieldDescriptor[] requestFields = {
                    fieldWithPath("date").description("예약 날짜 (YYYY-MM-DD)"),
                    fieldWithPath("timeId").description("시간 ID"),
                    fieldWithPath("themeId").description("테마 ID"),
                    fieldWithPath("memberId").description("사용자 ID"),
                    fieldWithPath("paymentKey").description("결제 요청 key"),
                    fieldWithPath("orderId").description("주문 ID"),
                    fieldWithPath("orderName").description("주문 명"),
                    fieldWithPath("amount").description("결제 금액")
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("id").description("예약 ID"),
                    fieldWithPath("user").description("예약 사용자 정보"),
                    fieldWithPath("user.id").description("사용자 ID"),
                    fieldWithPath("user.name").description("사용자 이름"),
                    fieldWithPath("date").description("예약 날짜"),
                    fieldWithPath("time").description("시간 정보"),
                    fieldWithPath("time.id").description("예약 시간 ID"),
                    fieldWithPath("time.startAt").description("방탈출 예약 시간"),
                    fieldWithPath("theme").description("테마 정보"),
                    fieldWithPath("theme.id").description("테마 정보"),
                    fieldWithPath("theme.name").description("테마 정보"),
                    fieldWithPath("theme.description").description("테마 정보"),
                    fieldWithPath("theme.thumbnail").description("테마 정보")
            };

            CookieDescriptor[] cookieFields = {cookieWithName("token").description("사용자 인증 토큰")};

            return document(
                    "reservation-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    responseFields(responseFields),
                    requestCookies(cookieFields)
            );
        }
    }

    @Nested
    @DisplayName("예약을 조회한다.")
    class FindReservation extends RestDocsTestBase {

        @Test
        @DisplayName("예약 조회 요청시, 존재하는 모든 예약과 OK를 응답한다")
        void findReservations() {

            RestAssured.given(spec).filter(findReservations_Document()).log().all()
                    .when().get("/reservations")
                    .then()
                    .log().all().statusCode(HttpStatus.OK.value()).body("size()", Matchers.is(3));
        }

        RestDocumentationFilter findReservations_Document() {

            ParameterDescriptor[] requestParameters = {
                    parameterWithName("themeId").description("조회 조건 테마 ID").optional(),
                    parameterWithName("userId").description("조회 조건 사용자의 ID").optional(),
                    parameterWithName("dateFrom").description("조회 시작 날짜").optional(),
                    parameterWithName("dateTo").description("조회 마지막 날짜").optional()
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("[]").description("조회된 예약 목록"),
                    fieldWithPath("[].id").description("예약 ID"),
                    fieldWithPath("[].user").description("예약 사용자 정보"),
                    fieldWithPath("[].user.id").description("사용자 ID"),
                    fieldWithPath("[].user.name").description("사용자 이름"),
                    fieldWithPath("[].date").description("예약 날짜"),
                    fieldWithPath("[].time").description("시간 정보"),
                    fieldWithPath("[].time.id").description("예약 시간 ID"),
                    fieldWithPath("[].time.startAt").description("방탈출 예약 시간"),
                    fieldWithPath("[].theme").description("테마 정보"),
                    fieldWithPath("[].theme.id").description("테마 정보"),
                    fieldWithPath("[].theme.name").description("테마 정보"),
                    fieldWithPath("[].theme.description").description("테마 정보"),
                    fieldWithPath("[].theme.thumbnail").description("테마 정보")
            };

            return document(
                    "reservation-find-all",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    queryParameters(requestParameters),
                    responseFields(responseFields)
            );
        }
    }

    @Nested
    @DisplayName("예약을 삭제한다.")
    class DeleteReservation extends RestDocsTestBase {

        @Test
        @DisplayName("주어진 아이디에 해당하는 예약이 없다면 NOT FOUND를 응답한다.")
        void deleteReservation_WhenReservationDoesNotExisted() {
            Long reservationId = 1000L;
            RestAssured.given(spec).filter(deleteReservation_WhenReservationDoesNotExisted_Document()).log().all()
                    .when()
                    .delete("/reservations/{id}", reservationId)
                    .then().log().all().statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("주어진 아이디에 해당하는 예약이 있다면 삭제하고 NO CONTENT를 응답한다.")
        void deleteReservation() {
            Long reservationId = 1L;
            RestAssured.given(spec).filter(deleteReservation_Document()).log().all()
                    .when().delete("/reservations/{id}", reservationId)
                    .then().log().all()
                    .statusCode(HttpStatus.NO_CONTENT.value());
        }

        RestDocumentationFilter deleteReservation_WhenReservationDoesNotExisted_Document() {

            ParameterDescriptor[] pathParameters = {parameterWithName("id").description("예약 ID")};

            FieldDescriptor[] responseFields = getErrorFieldDescriptors();

            return document(
                    "reservation-remove-not-found",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(pathParameters),
                    responseFields(responseFields)
            );
        }

        RestDocumentationFilter deleteReservation_Document() {

            ParameterDescriptor[] pathParameters = {parameterWithName("id").description("예약 ID")};

            return document(
                    "reservation-remove",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(pathParameters)
            );
        }
    }

    @TestConfiguration
    static class TestConfig {

        @Bean
        public PaymentClient paymentClient() {
            return Mockito.mock(PaymentClient.class);
        }
    }
}
