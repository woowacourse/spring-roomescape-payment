package roomescape.controller.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.format.annotation.DateTimeFormat;

public record CreateUserReservationRequest(
        @NotNull(message = "null일 수 없습니다.")
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        @FutureOrPresent(message = "과거 날짜로는 예약할 수 없습니다.")
        LocalDate date,

        @NotNull(message = "null일 수 없습니다.")
        @Positive(message = "양수만 입력할 수 있습니다.")
        Long themeId,

        @NotNull(message = "null일 수 없습니다.")
        @Positive(message = "양수만 입력할 수 있습니다.")
        Long timeId,

        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        String paymentKey,

        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        String orderId,

        @Positive(message = "양수만 입력할 수 있습니다.")
        long amount,

        @NotBlank(message = "null이거나 비어있을 수 없습니다.")
        String paymentType
) {
}
