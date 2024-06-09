package roomescape.payment.domain;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum PaymentStatus {
    DONE("DONE"),
    CANCELED("CANCELED")
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
