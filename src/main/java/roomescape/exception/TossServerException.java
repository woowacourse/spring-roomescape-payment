package roomescape.exception;

import roomescape.dto.payment.TossError;

public class TossServerException  extends RuntimeException{

    private final TossError tossError;

    public TossServerException(TossError tossError) {
        this.tossError = tossError;
    }

    public TossError getTossError() {
        return tossError;
    }
}
