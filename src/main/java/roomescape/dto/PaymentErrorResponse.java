package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결졔 실패 응답 DTO")
public record PaymentErrorResponse(@Schema(description = "실패 원인에 대한 코드값", example = "REJECT_ACCOUNT_PAYMENT") String code,
                                   @Schema(description = "오류 설명 메시지", example = "잔액부족으로 결제에 실패했습니다.") String message) {
}
