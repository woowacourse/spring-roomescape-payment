package roomescape.payment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createTimeAt_10;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@TestPropertySource(properties = "rest-client.toss-payment.base-url=http://localhost:8089")
class PaymentControllerTest extends IntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private TossRestClient tossRestClient;

    @Test
    void 결제_정상_승인시_payment_저장_및_reservation_저장() {
        // given
        ReservationTime reservationTime = createTimeAt_10();
        Theme theme = createDefaultTheme();
        dbHelper.insertTime(reservationTime);
        dbHelper.insertTheme(theme);

        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;
        String paymentType = "paymentType";
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(DEFAULT_DATE, theme.getId(), reservationTime.getId(),
                paymentKey, orderId, amount, paymentType);

        Member member = createDefaultMember_1();
        dbHelper.insertMember(member);
        String token = jwtTokenProvider.createToken(createClaims(member));

        given(tossRestClient.confirm(any()))
                .willReturn(mock(TossPaymentResponse.class));

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(reservationPaymentRequest)
                .when().post("payments/confirm/tossPay")
                .then().log().all()
                .statusCode(HttpStatus.CREATED);
    }
}
