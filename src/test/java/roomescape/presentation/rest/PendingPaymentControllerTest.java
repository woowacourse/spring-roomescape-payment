package roomescape.presentation.rest;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.application.PaymentService;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.presentation.response.ReservedResponse;


class PendingPaymentControllerTest {

    @Nested
    @Import(TestConfig.class)
    @DisplayName("결재 대기 상태의 예약의 결제를 진행한다.")
    class ConfirmPayment extends RestDocsTestBase {

        @Autowired
        private PaymentService paymentService;

        @Test
        @DisplayName("예약 ID가 존재하지 않으면 404를 반환한다.")
        void confirmPayment_WhenPendingPaymentNotExists_ThenReturn404Status() {
            // given
            var pendingPaymentId = 10000L;
            PaymentInfo paymentInfo = new PaymentInfo("payment_key_1", "order_id_1", "order_name_1", 10000L);

            // when & then
            RestAssured.given(spec).filter(confirmPayment_WhenPendingPaymentNotExists_ThenReturn404Status_Document())
                    .log().all().contentType(ContentType.JSON).body(paymentInfo)
                    .when()
                    .patch("/pending-payments/{id}/payment", pendingPaymentId)
                    .then().log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("정상적으로 결제를 진행하면 200을 반환한다.")
        void confirmPayment() {
            // given
            var pendingPaymentId = 1L;
            PaymentInfo paymentInfo = new PaymentInfo("payment_key_1", "order_id_1", "order_name_1", 10000L);

            // when & then
            ReservedResponse response = RestAssured.given(spec).filter(confirmPayment_Document()).log().all()
                    .contentType(ContentType.JSON).body(paymentInfo)
                    .when().patch("/pending-payments/{id}/payment", pendingPaymentId)
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value()).extract().body().as(ReservedResponse.class);

            assertAll(
                    () -> assertThat(response.date()).isEqualTo(LocalDate.now().plusDays(2)),
                    () -> assertThat(response.theme().id()).isEqualTo(1),
                    () -> assertThat(response.time().id()).isEqualTo(1),
                    () -> assertThat(response.user().id()).isEqualTo(2),
                    () -> verify(paymentService).requestPayment(any(Reserved.class), any(PaymentInfo.class))
            );

        }

        RestDocumentationFilter confirmPayment_WhenPendingPaymentNotExists_ThenReturn404Status_Document() {
            FieldDescriptor[] responseFields = getErrorFieldDescriptors();

            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("결제 대기 상태 예약 ID")
            };

            FieldDescriptor[] requestFields = {
                    fieldWithPath("paymentKey").description("결제 요청 key"),
                    fieldWithPath("orderId").description("주문 ID"),
                    fieldWithPath("orderName").description("주문 명"),
                    fieldWithPath("amount").description("결제 금액")
            };

            return document(
                    "pending-payment-confirm-not-found",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    pathParameters(pathParameters),
                    responseFields(responseFields)
            );
        }


        RestDocumentationFilter confirmPayment_Document() {
            ParameterDescriptor[] pathParameters = {
                    parameterWithName("id").description("결제 대기 상태 예약 ID")
            };

            FieldDescriptor[] requestFields = {
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

            return document(
                    "pending-payment-confirm",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    pathParameters(pathParameters),
                    responseFields(responseFields)
            );
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PaymentService paymentService() {
            return mock(PaymentService.class);
        }
    }
}
