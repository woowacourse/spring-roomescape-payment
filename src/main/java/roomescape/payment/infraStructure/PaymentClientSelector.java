package roomescape.payment.infraStructure;

import roomescape.common.exception.PaymentClientException;

public class PaymentClientSelector {

    private static String TOSS_TYPE = "TOSS";

    private final PaymentGatewayClient tossPaymentClient;

    public PaymentClientSelector(final PaymentGatewayClient tossPaymentClient ) {
        this.tossPaymentClient = tossPaymentClient;
    }

    public PaymentGatewayClient getPaymentGatewayBySelector(final String pgType) {
        if (TOSS_TYPE.equalsIgnoreCase(pgType)) {
            return tossPaymentClient;
        }
        throw new PaymentClientException("결제 요청중 오류가 발생했습니다.");
    }
}
