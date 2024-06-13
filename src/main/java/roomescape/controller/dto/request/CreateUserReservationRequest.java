package roomescape.controller.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateUserReservationRequest(
        @Schema(description = "예약 날짜", example = "2024-06-08")
        @NotNull(message = "null일 수 없습니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent(message = "과거 날짜로는 예약할 수 없습니다.")
        LocalDate date,

        @Schema(description = "테마 고유 번호", example = "1")
        @NotNull(message = "null일 수 없습니다.")
        @Positive(message = "양수만 입력할 수 있습니다.")
        Long themeId,

        @Schema(description = "시간 고유 번호", example = "1")
        @NotNull(message = "null일 수 없습니다.")
        @Positive(message = "양수만 입력할 수 있습니다.")
        Long timeId,

        @Schema(description = "결제 키", example = "dsfoie351lksa033")
        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        String paymentKey,

        @Schema(description = "주문 번호", example = "ORDERde4332mdberk24f")
        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        String orderId,

        @Schema(description = "가격", example = "10000")
        @Positive(message = "양수만 입력할 수 있습니다.")
        long amount,

        @Schema(description = "결제 키", example = "dsfoie351lksa033")
        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        String paymentType
) {
}
