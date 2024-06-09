package roomescape.domain.payment;

public enum PaymentStatus {
    PENDING,
    SUCCESS,
    CANCELLED
    ;

    public boolean isSuccess() {
        return this == SUCCESS;
    }

    public PaymentStatus purchase() {
        if (this == PENDING) {
            return SUCCESS;
        }
        throw new IllegalStateException("결제할 수 없습니다.");
    }

    public PaymentStatus cancel() {
        if (this == SUCCESS) {
            return CANCELLED;
        }
        throw new IllegalStateException("취소할 수 없습니다.");
    }
}
