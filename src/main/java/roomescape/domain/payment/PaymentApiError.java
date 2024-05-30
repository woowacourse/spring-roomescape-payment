package roomescape.domain.payment;

record PaymentApiError(PaymentApiErrorCode code, String message) {
    boolean isNeedToHide() {
        return code.isNeedToHide();
    }
}
