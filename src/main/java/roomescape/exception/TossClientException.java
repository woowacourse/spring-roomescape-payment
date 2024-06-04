package roomescape.exception;

import roomescape.dto.payment.TossError;

public class TossClientException extends RuntimeException {

    private final TossError tossError;

    public TossClientException(TossError tossError) {
        this.tossError = tossError;
    }

    public TossError getTossError() {
        return tossError;
    }
}
