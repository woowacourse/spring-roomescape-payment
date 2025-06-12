package roomescape.payment.infraStructure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roomescape.common.exception.PaymentClientException;

public class PaymentClientSelector {

    private static final Logger log = LoggerFactory.getLogger(PaymentClientSelector.class);
    private static String TOSS_TYPE = "TOSS";

    private final PaymentGatewayClient tossPaymentClient;

    public PaymentClientSelector(final PaymentGatewayClient tossPaymentClient ) {
        this.tossPaymentClient = tossPaymentClient;
    }

    public PaymentGatewayClient getPaymentGatewayBySelector(final String pgType) {
        if (TOSS_TYPE.equalsIgnoreCase(pgType)) {
            return tossPaymentClient;
        }

        log.error("[getPaymentGatewayBySelector/PG사 선택 에러] pgType: {}",pgType);
        throw new PaymentClientException("결제 요청중 오류가 발생했습니다.");
    }
}
