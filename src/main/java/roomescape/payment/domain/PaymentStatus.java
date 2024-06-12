package roomescape.payment.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PaymentStatus {
    READY("READY"),
    DONE("DONE"),
    CANCELED("CANCELED"),
    REJECTED("ABORTED")
    ;

    private static final Map<String, PaymentStatus> TOSS_PAY_STATUS =
            Arrays.stream(PaymentStatus.values())
                    .collect(Collectors.toMap(status -> status.tossPayStatus, status -> status));

    private final String tossPayStatus;

    PaymentStatus(String tossPayStatus) {
        this.tossPayStatus = tossPayStatus;
    }

    public static PaymentStatus fromTossPayStatus(String tossPayStatus) {
        return TOSS_PAY_STATUS.get(tossPayStatus);
    }
}
