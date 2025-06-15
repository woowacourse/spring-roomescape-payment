package roomescape.exception;

import roomescape.exception.common.BadRequestException;

public class DuplicateContentException extends BadRequestException {

    public DuplicateContentException(String message, String duplicatedInput) {
        super(message, String.format("[중복 리소스 생성] 중복 사유 : %s, 중복 리소스 : %s", message, duplicatedInput));
    }
}
