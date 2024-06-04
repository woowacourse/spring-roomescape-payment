package roomescape.exception;

import java.util.List;

public class PaymentFailureException extends RuntimeException {

    private static final List<String> SERVER_FAULT_CODE = List.of(
            "INVALID_API_KEY", "INVALID_AUTHORIZE_AUTH", "UNAUTHORIZED_KEY"
    );


    public PaymentFailureException(String message) {
        super(message);
    }

    public static PaymentFailureException of(String code, String message) {
        if (SERVER_FAULT_CODE.contains(code)) {
            message = "결제를 진행하던 중 서버에 오류가 발생했습니다.";
        }
        return new PaymentFailureException(message);
    }
}
