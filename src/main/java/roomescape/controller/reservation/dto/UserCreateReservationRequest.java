package roomescape.controller.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserCreateReservationRequest(
        @NotNull
        LocalDate date,

        @NotNull
        Long timeId,

        @NotNull
        Long themeId,

        @NotBlank
        String paymentKey,

        @NotBlank
        String orderId,

        @NotNull
        Long amount) {
}
