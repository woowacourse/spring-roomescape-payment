package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt_10;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.auth.dto.LoginMember;
import roomescape.exception.NotFoundException;
import roomescape.member.domain.Member;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.exception.TossPaymentException;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import({NotPaidPaymentProcessor.class, DBHelper.class})
class NotPaidPaymentProcessorTest {

    @Autowired
    private NotPaidPaymentProcessor notPaidPaymentProcessor;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private DBHelper dbHelper;

    @DisplayName("예약 소유자가 맞고, 상태가 결제 전인 경우 PENDING으로 정상 변경된다")
    @Test
    void prepareNotPaidToPending_success() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertNotPaidPayment(reservation);

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, "CARD");

        // when
        Payment updatedPayment = notPaidPaymentProcessor.prepareNotPaidToPending(
                reservation.getId(), request, LoginMember.from(member)
        );

        // then
        SoftAssertions.assertSoftly(softly -> {
            assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.PENDING);
            assertThat(updatedPayment.getPaymentKey()).isEqualTo(paymentKey);
            assertThat(updatedPayment.getOrderId()).isEqualTo(orderId);
            assertThat(updatedPayment.getAmount()).isEqualTo(amount);
        });
    }

    @DisplayName("예약 ID에 해당하는 Reservation이 없으면 예외 발생")
    @Test
    void prepareNotPaidToPending_notFoundReservation() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, "CARD");

        // when & then
        long nonExistsId = 999L;
        assertThatThrownBy(() -> {
            notPaidPaymentProcessor.prepareNotPaidToPending(
                    nonExistsId, request, LoginMember.from(member)
            );
        }).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("존재하지 않는 예약입니다");
    }

    @DisplayName("예약은 존재하지만 로그인한 사용자가 소유자가 아닌 경우 예외 발생")
    @Test
    void prepareNotPaidToPending_notOwner() {
        // given
        Member owner = dbHelper.insertMember(createDefaultMember_1());
        Member otherMember = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(owner, DEFAULT_DATE, time, theme));
        dbHelper.insertNotPaidPayment(reservation);

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, "CARD");

        // when & then
        assertThatThrownBy(() -> notPaidPaymentProcessor.prepareNotPaidToPending(
                reservation.getId(), request, LoginMember.from(otherMember)
        )).isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("본인의 예약만 결제할 수 있습니다");
    }

    @DisplayName("결제 정보가 존재하지 않는 경우 예외 발생")
    @Test
    void prepareNotPaidToPending_notFoundPayment() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        //Payment 저장 생략 -> 존재하지 않음

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, "CARD");

        // when & then
        assertThatThrownBy(() -> notPaidPaymentProcessor.prepareNotPaidToPending(
                reservation.getId(), request, LoginMember.from(member)
        )).isInstanceOf(NotFoundException.class)
                .hasMessageContaining("reservationId에 해당하는 결제를 찾을 수 없습니다");
    }

    @DisplayName("결제 상태가 NOT_PAID가 아닌 경우 예외 발생")
    @Test
    void prepareNotPaidToPending_invalidStatus() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        Reservation reservation = dbHelper.insertReservation(createReservationOf(member, DEFAULT_DATE, time, theme));
        Payment payment = dbHelper.insertCompletedPayment(reservation);

        String paymentKey = "test-payment-key";
        String orderId = "test-order-id";
        Long amount = 10000L;
        PaymentRequest request = new PaymentRequest(paymentKey, orderId, amount, "CARD");

        // when & then
        assertThatThrownBy(() -> notPaidPaymentProcessor.prepareNotPaidToPending(
                reservation.getId(), request, LoginMember.from(member)
        )).isInstanceOf(TossPaymentException.class)
                .hasMessageContaining("결제를 시작할 수 있는 상태가 아닙니다");
    }
}
