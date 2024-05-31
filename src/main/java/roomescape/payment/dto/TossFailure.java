package roomescape.payment.dto;

import java.util.List;

public record TossFailure(String code, String message) {

    private static final List<String> SERVER_FAULT_CODE = List.of(
            "INVALID_API_KEY", "INVALID_AUTHORIZE_AUTH", "UNAUTHORIZED_KEY"
    );

    @Override
    public String message() {
        if (SERVER_FAULT_CODE.contains(code)) {
            return "서버에 오류가 발생했습니다.";
        }
        return message;
    }
}
