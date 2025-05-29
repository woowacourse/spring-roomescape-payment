package roomescape.presentation.api.reservation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.application.reservation.command.dto.CreateReservationWithPaymentCommand;

import java.time.LocalDate;

public record CreateReservationWithPaymentRequest(
        @NotNull(message = "date는 필수입니다.")
        LocalDate date,
        @NotNull(message = "timeId는 필수입니다.")
        Long timeId,
        @NotNull(message = "themeId는 필수입니다.")
        Long themeId,
        @NotBlank(message = "paymentKey는 필수입니다.")
        String paymentKey,
        @NotBlank(message = "orderId는 필수입니다.")
        String orderId,
        @NotNull(message = "amount는 필수입니다.")
        Long amount,
        @NotBlank(message = "paymentType은 필수입니다.")
        String paymentType
) {

    public CreateReservationWithPaymentCommand toCreateCommand(final Long memberId) {
        return new CreateReservationWithPaymentCommand(
                date,
                timeId,
                themeId,
                memberId,
                paymentKey,
                orderId,
                amount,
                paymentType
        );
    }
}
