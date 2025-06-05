package roomescape.payment.controller;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.spec.internal.HttpStatus;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.ReservationPaymentRequest;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.infrastructure.TossRestClient;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@TestPropertySource(properties = "rest-client.toss-payment.base-url=http://localhost:8089")
@Commit
class PaymentControllerTest extends IntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private TossRestClient tossRestClient;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PaymentRepository paymentRepository;

    @Test
    void 결제_정상_승인시_payment_저장_및_reservation_저장() {
        // given
        LocalDate date = DEFAULT_DATE;
        ReservationTime reservationTime = dbHelper.insertTime(createTimeAt_10());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;
        String paymentType = "paymentType";
        ReservationPaymentRequest reservationPaymentRequest = new ReservationPaymentRequest(
                date, theme.getId(), reservationTime.getId(),
                paymentKey, orderId, amount, paymentType);

        Member member = dbHelper.insertMember(createDefaultMember_1());
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

        SoftAssertions.assertSoftly(softly -> {
            assertThat(reservationRepository.findAll()).hasSize(1);
            assertThat(paymentRepository.findAll()).hasSize(1);
        });
    }

    @DisplayName("결제 전 상태 -> 결제 진행 -> 결제 완료 상태로 변경")
    @Test
    void completePaymentForReservation() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        String token = jwtTokenProvider.createToken(createClaims(member));

        Reservation reservation = dbHelper.insertReservation(
                createReservationOf(member, DEFAULT_DATE, createTimeAt_10(), createDefaultTheme()));
        dbHelper.insertNotPaidPayment(reservation);

        String paymentKey = "paymentKey";
        String orderId = "orderId";
        Long amount = 1000L;
        String paymentType = "paymentType";

        PaymentRequest paymentRequest = new PaymentRequest(paymentKey, orderId, amount, paymentType);
        given(tossRestClient.confirm(any()))
                .willReturn(mock(TossPaymentResponse.class));

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(paymentRequest)
                .when().post("payments/confirm/tossPay/" + reservation.getId())
                .then().log().all()
                .statusCode(HttpStatus.CREATED);

        Payment findPayment = paymentRepository.findByPaymentKey(paymentKey).orElseThrow();
        assertThat(findPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
}
