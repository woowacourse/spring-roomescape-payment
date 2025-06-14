package roomescape.payment.exception;

import roomescape.common.domain.DomainTerm;

public class PaymentMismatchException extends RuntimeException {

    public PaymentMismatchException(final DomainTerm domainTerm) {
        super("요청한 결제정보와 실제 " + domainTerm.label() + "이 일치하지 않습니다.");
    }
}
