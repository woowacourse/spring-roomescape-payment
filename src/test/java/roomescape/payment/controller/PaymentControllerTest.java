package roomescape.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;

@Sql("/data.sql")
@TestPropertySource(properties = "rest-client.toss-payment.base-url=http://localhost:8089")
class PaymentControllerTest extends IntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private TossRestClient tossRestClient;

    @Test
    void 결제_정상_승인시_payment_저장_및_reservation_저장() {
        // given - data.sql
        LocalDate date = LocalDate.of(2999,5,5);
        Long themeId = 1L;
        Long timeId = 1L;
        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;
        String paymentType = "paymentType";
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(date, themeId, timeId,
                paymentKey, orderId, amount, paymentType);

        Claims claims = Jwts.claims()
                .subject("1")
                .build();
        String token = jwtTokenProvider.createToken(claims);

        given(tossRestClient.confirm(any()))
                .willReturn(mock(TossPaymentResponse.class));

        // when
        // then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(reservationPaymentRequest)
                .when().post("payments/confirm/tossPay")
                .then().log().all()
                .statusCode(HttpStatus.CREATED);
    }
}
