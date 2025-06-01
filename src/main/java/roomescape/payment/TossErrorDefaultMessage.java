package roomescape.payment;

import java.util.Arrays;
import java.util.List;

public enum TossErrorDefaultMessage {

    CARD(List.of(ErrorKeyword.CARD, ErrorKeyword.INSTALLMENT), "카드 정보를 확인해주세요."),
    EXCEED(List.of(ErrorKeyword.EXCEED, ErrorKeyword.MAX, ErrorKeyword.MINIMUM), "결제 한도 또는 금액을 확인해주세요"),
    ACCOUNT(List.of(ErrorKeyword.ACCOUNT, ErrorKeyword.BANK, ErrorKeyword.WITHDRAW), "계좌 정보 또는 은행 서비스 시간을 확인해주세요."),
    REJECT(List.of(ErrorKeyword.REJECT), "카드사 또는 은행에서 결제를 거절했습니다. 다른 결제수단을 이용해주세요."),
    AUTH(List.of(ErrorKeyword.AUTH, ErrorKeyword.UNAUTHORIZED, ErrorKeyword.PASSWORD, ErrorKeyword.KEY, ErrorKeyword.FORBIDDEN),
            "인증 정보를 확인해주세요."),
    NOT_FOUND(List.of(ErrorKeyword.NOT_FOUND), "요청하신 정보를 찾을 수 없습니다."),
    SYSTEM(List.of(ErrorKeyword.INTERNAL, ErrorKeyword.SYSTEM, ErrorKeyword.PROCESSING, ErrorKeyword.PROVIDER,
            ErrorKeyword.UNKNOWN), "일시적인 시스템 오류입니다. 잠시 후 다시 시도해주세요."),
    INVALID(List.of(ErrorKeyword.REGISTERED, ErrorKeyword.NOT_AVAILABLE, ErrorKeyword.ALREADY, ErrorKeyword.REQUEST), "요청 내용을 확인해주세요."),
    SECURITY(List.of(ErrorKeyword.FDS), "본인인증이 필요합니다."),
    POINT(List.of(ErrorKeyword.POINT), "포인트 사용 조건을 확인해주세요."),
    NONE(List.of(), "결제 승인 처리 중 오류가 발생했습니다. 고객센터에 문의해주세요.");


    private final List<ErrorKeyword> keywords;
    private final String message;

    TossErrorDefaultMessage(final List<ErrorKeyword> keywords, final String message) {
        this.keywords = keywords;
        this.message = message;
    }

    public static String getMessageByErrorCode(final String[] splitErrorCodeByDelimiter) {
        return Arrays.stream(TossErrorDefaultMessage.values())
                .filter(tossErrorDefaultMessage -> tossErrorDefaultMessage.keywords.contains(getErrorKeyword(splitErrorCodeByDelimiter)))
                .findAny()
                .map(tossErrorDefaultMessage -> tossErrorDefaultMessage.message)
                .orElse(NONE.message);
    }

    private static ErrorKeyword getErrorKeyword(final String[] splitErrorCodeByDelimiter) {
        return Arrays.stream(splitErrorCodeByDelimiter)
                .map(ErrorKeyword::findByKeyword)
                .filter(keyword -> keyword != ErrorKeyword.NONE)
                .findFirst()
                .orElse(ErrorKeyword.NONE);
    }

    private enum ErrorKeyword {
        CARD, INSTALLMENT,
        EXCEED, MAX, MINIMUM,
        ACCOUNT, BANK, WITHDRAW,
        REJECT,
        AUTH, UNAUTHORIZED, PASSWORD, KEY, FORBIDDEN,
        NOT_FOUND,
        INTERNAL, SYSTEM, PROCESSING, PROVIDER, UNKNOWN,
        REGISTERED, NOT_AVAILABLE, ALREADY, UNAPPROVED, REQUEST,
        FDS,
        POINT,
        NONE;

        private static ErrorKeyword findByKeyword(final String keyword) {
            return Arrays.stream(ErrorKeyword.values())
                    .filter(errorKeyword -> errorKeyword.name().contains(keyword))
                    .findAny()
                    .orElse(NONE);
        }
    }
}
