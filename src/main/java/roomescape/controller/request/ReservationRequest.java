package roomescape.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReservationRequest(
        @NotNull
        @DateTimeFormat
        @Schema(description = "예약 날짜", example = "2024-06-11")
        LocalDate date,
        @NotNull
        @Positive(message = "[ERROR] timeId의 값이 1보다 작을 수 없습니다.")
        @Schema(description = "예약 시간 ID", example = "1")
        Long timeId,
        @NotNull
        @Positive(message = "[ERROR] themeId의 값이 1보다 작을 수 없습니다.")
        @Schema(description = "예약 테마 ID", example = "1")
        Long themeId,
        @NotEmpty
        @Schema(description = "결제 ID", example = "MOVINPOKE1533")
        String orderId,
        @NotEmpty
        @Schema(description = "결제 Key", example = "tgen_20240611152237Z1il1")
        String paymentKey,
        @NotNull
        @Schema(description = "결제 금액", example = "1999999")
        Long amount) {
}
