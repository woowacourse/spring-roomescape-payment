package roomescape.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PayStandbyRequest(
    @NotNull(message = "null일 수 없습니다.")
    @Positive(message = "양수만 입력할 수 있습니다.")
    Long reservationId,

    @NotBlank(message = "null이거나 비어있을 수 없습니다.")
    String paymentKey,

    @NotBlank(message = "null이거나 비어있을 수 없습니다.")
    String orderId,

    @Positive(message = "양수만 입력할 수 있습니다.")
    long amount
) { }
