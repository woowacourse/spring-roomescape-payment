package roomescape.exception;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "토스 결제 예외 응답", description = "토스 api가 반환하는 예외 객체는 상태 코드와 메세지를 갖는다.")
public record TossPaymentExceptionResponse(String code, String message) {
}
