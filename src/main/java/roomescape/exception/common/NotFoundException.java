package roomescape.exception.common;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeExceptionWithLog {

    public NotFoundException(String notFoundEntity, Object key) {
        super(notFoundEntity + "이(가) 존재하지 않습니다.",
                String.format("[NOT_FOUND] 사유 : %s이(가) 존재하지 않습니다, 검색 값 : %s", notFoundEntity, key.toString()),
                HttpStatus.NOT_FOUND
        );
    }

    public NotFoundException(String message) {
        super(message, "[NOT_FOUND] 사유 : " + message, HttpStatus.NOT_FOUND);
    }
}
