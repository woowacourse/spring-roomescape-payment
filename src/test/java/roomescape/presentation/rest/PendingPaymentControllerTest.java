package roomescape.presentation.rest;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static roomescape.fixture.PaymentFixture.CREATE_PAYMENT_OF;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import roomescape.application.PaymentService;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.presentation.response.ReservedResponse;
import roomescape.presentation.rest.PendingPaymentControllerTest.TestConfig;


@Import(TestConfig.class)
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PendingPaymentControllerTest {

    @Autowired
    private PaymentService paymentService;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Nested
    @DisplayName("결재 대기 상태의 예약의 결제를 진행한다.")
    class ConfirmPayment {

        @Test
        @DisplayName("예약 ID가 존재하지 않으면 404를 반환한다.")
        void confirmPayment_WhenPendingPaymentNotExists_ThenReturn404Status() {
            // given
            var pendingPaymentId = 10000L;
            PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "orderId", 1000);

            // when & then
            RestAssured.given().log().all().contentType(ContentType.JSON).body(paymentInfo).when()
                    .patch("/pending-payments/" + pendingPaymentId + "/payment").then().log().all()
                    .statusCode(HttpStatus.NOT_FOUND.value());
        }

        @Test
        @DisplayName("정상적으로 결제를 진행하면 200을 반환한다.")
        void confirmPayment_WhenPendingPaymentExists_ThenReturn200Status() {
            // given
            Payment payment = CREATE_PAYMENT_OF(null);
            var pendingPaymentId = 6L;
            PaymentInfo paymentInfo = new PaymentInfo("paymentKey", "orderId", 1000);

            Mockito.when(paymentService.savePayment(paymentInfo)).thenReturn(payment);

            // when & then
            ReservedResponse response = RestAssured.given().log().all().contentType(ContentType.JSON).body(paymentInfo)
                    .when().patch("/pending-payments/" + pendingPaymentId + "/payment").then().log().all()
                    .statusCode(HttpStatus.OK.value()).extract().body().as(ReservedResponse.class);

            assertAll(() -> assertThat(response.date()).isEqualTo(LocalDate.now().plusDays(2)),
                    () -> assertThat(response.theme().id()).isEqualTo(1),
                    () -> assertThat(response.time().id()).isEqualTo(1),
                    () -> assertThat(response.user().id()).isEqualTo(2),
                    () -> verify(paymentService).savePayment(any(PaymentInfo.class)));

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
