package roomescape.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record ReservationRequest(
        @Schema(example = "2025-06-05")
        @NotNull @DateTimeFormat(pattern = "yyyy-MM-dd")
        LocalDate date,

        @Schema(example = "1")
        long themeId,

        @Schema(example = "1")
        long timeId,

        @Schema(example = "toss-payment-key")
        @NotBlank
        String paymentKey,

        @Schema(example = "toss-order-id")
        @NotBlank
        String orderId,

        @Schema(example = "10000")
        int amount
) {
}
