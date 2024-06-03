package roomescape.advice;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import roomescape.advice.exception.ExceptionTitle;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @DisplayName("NullPointerException이 던져지면 400 에러를 반환한다.")
    @Test
    void handleNullPointerExceptionTest() {
        NullPointerException exception = new NullPointerException();
        ExceptionTitle title = ExceptionTitle.ILLEGAL_USER_REQUEST;
        ProblemDetail problemDetail =
                ProblemDetail.forStatusAndDetail(title.getStatusCode(), "인자 중 null 값이 존재합니다.");
        problemDetail.setTitle(title.getTitle());
        ResponseEntity<ProblemDetail> expected = ResponseEntity.status(400)
                .body(problemDetail);

        ResponseEntity<ProblemDetail> actual = globalExceptionHandler.handleNullPointerException(exception);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("예상치 못한 에러가 발생하면 500 에러를 반환한다.")
    @Test
    void handleUnexpectedExceptionTest() {
        RuntimeException exception = new ArithmeticException();
        ExceptionTitle title = ExceptionTitle.INTERNAL_SERVER_ERROR;
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                title.getStatusCode(), "예상치 못한 예외가 발생했습니다. 관리자에게 문의하세요.");
        problemDetail.setTitle(title.getTitle());
        ResponseEntity<ProblemDetail> expected = ResponseEntity.status(500)
                .body(problemDetail);

        ResponseEntity<ProblemDetail> actual = globalExceptionHandler.handleUnexpectedException(exception);

        assertThat(actual).isEqualTo(expected);
    }
}
