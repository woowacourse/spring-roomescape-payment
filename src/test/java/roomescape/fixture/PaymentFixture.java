package roomescape.fixture;

import org.springframework.test.util.ReflectionTestUtils;
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;

public class PaymentFixture {

    public static Payment CREATE_PAYMENT_1() {
        Payment payment = Payment.register(
                "payment_key_1",
                "order_id_1",
                "방탈출 예약 1건",
                10000L
        );
        ReflectionTestUtils.setField(payment, "id", 1L);
        return payment;
    }

    public static Payment CREATE_PAYMENT_2() {
        Payment payment = Payment.register(
                "payment_key_2",
                "order_id_2",
                "방탈출 예약 2건",
                20000L
        );
        ReflectionTestUtils.setField(payment, "id", 2L);
        return payment;
    }

    public static Payment CREATE_PAYMENT_3() {
        Payment payment = Payment.register(
                "payment_key_3",
                "order_id_3",
                "방탈출 예약 3건",
                30000L
        );
        ReflectionTestUtils.setField(payment, "id", 3L);
        return payment;
    }

    public static Payment CREATE_PAYMENT_4() {
        Payment payment = Payment.register(
                "payment_key_4",
                "order_id_4",
                "방탈출 패키지 1건",
                50000L
        );
        ReflectionTestUtils.setField(payment, "id", 4L);
        return payment;
    }

    public static Payment CREATE_PAYMENT_OF(Long id) {
        Payment payment = Payment.register(
                "payment_key_9999",
                "order_id_9999",
                "방탈출 패키지 1건",
                99999L
        );
        ReflectionTestUtils.setField(payment, "id", id);
        return payment;
    }

    public static Payment CREATE_PAYMENT_OF(Long id, PaymentInfo paymentInfo) {
        Payment payment = Payment.register(
                paymentInfo.paymentKey(),
                paymentInfo.orderId(),
                paymentInfo.orderName(),
                paymentInfo.amount()
        );
        ReflectionTestUtils.setField(payment, "id", id);
        return payment;
    }
}
