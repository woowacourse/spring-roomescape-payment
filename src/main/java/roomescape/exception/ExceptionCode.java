package roomescape.exception;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;

@Tag(name = "예외 코드 인터페이스", description = "예외 코드 객체는 status, message를 반환하는 메서드를 구현해야 한다.")
public interface ExceptionCode {

    HttpStatus getHttpStatus();

    String getMessage();
}
