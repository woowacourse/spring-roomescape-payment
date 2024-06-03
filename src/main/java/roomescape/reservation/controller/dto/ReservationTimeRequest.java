package roomescape.reservation.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ReservationTimeRequest(
        @NotBlank(message = "시간을 입력하지 않았습니다.")
        @Pattern(regexp = "^(?:[01]\\d|2[0-4]):(?:[0-5]\\d|60)$")
        String startAt
) {
}
