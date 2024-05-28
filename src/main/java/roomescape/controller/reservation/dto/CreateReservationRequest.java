package roomescape.controller.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateReservationRequest(
        @NotNull
        Long memberId,

        @NotNull
        Long themeId,

        @NotNull
        LocalDate date,

        @NotNull
        Long timeId,

        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId,

        @NotNull
        Long amount) {
}
