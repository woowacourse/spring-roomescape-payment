package roomescape.fixture;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import roomescape.payment.domain.PaymentInfo;
import roomescape.payment.dto.EasyPayTypeDetail;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentResponse;

public class PaymentFixture {
    public static final PaymentRequest PAYMENT_REQUEST = new PaymentRequest("paymentKey", "orderId", BigDecimal.valueOf(1000));
    public static final PaymentInfo PAYMENT_INFO = new PaymentInfo("orderName", "paymentKey",
            LocalDateTime.now().toString(),
            LocalDateTime.now().toString(),
            new EasyPayTypeDetail("토스페이"),
            "currency",
            BigDecimal.valueOf(10000));
    public static final PaymentResponse PAYMENT_RESPONSE = PaymentResponse.from(PAYMENT_INFO);
}
