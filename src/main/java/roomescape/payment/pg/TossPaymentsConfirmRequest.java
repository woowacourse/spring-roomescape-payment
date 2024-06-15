package roomescape.payment.pg;

import roomescape.global.exception.ViolationException;
import roomescape.payment.application.ProductPayRequest;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TossPaymentsConfirmRequest {
    private static final Pattern ORDER_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-_]+$");
    private static final int MIN_ORDER_ID_LENGTH = 6;
    private static final int MAX_ORDER_ID_LENGTH = 64;
    private static final int MAX_PAYMENT_KEY_LENGTH = 200;

    private final String paymentKey;
    private final String orderId;
    private final BigDecimal amount;

    public TossPaymentsConfirmRequest(ProductPayRequest request) {
        this(request.paymentKey(), request.orderId(), request.amount());
    }

    public TossPaymentsConfirmRequest(String paymentKey, String orderId, BigDecimal amount) {
        validatePaymentKey(paymentKey);
        validateOrderId(orderId);
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    private void validatePaymentKey(String paymentKey) {
        if (paymentKey.length() > MAX_PAYMENT_KEY_LENGTH) {
            throw new ViolationException("paymentKey는 최대 200자입니다.");
        }
    }

    private void validateOrderId(String orderId) {
        validateOrderIdLength(orderId);
        validateOrderIdPattern(orderId);
    }

    private void validateOrderIdLength(String orderId) {
        if (orderId.length() < MIN_ORDER_ID_LENGTH || orderId.length() > MAX_ORDER_ID_LENGTH) {
            throw new ViolationException("orderId는 6자 이상 64자 이하의 문자열입니다.");
        }
    }

    private void validateOrderIdPattern(String orderId) {
        Matcher matcher = ORDER_ID_PATTERN.matcher(orderId);
        if (!matcher.matches()) {
            throw new ViolationException("orderId는 영문 대소문자, 숫자, 특수문자 -, _로 이루어져야 합니다.");
        }
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }
}
