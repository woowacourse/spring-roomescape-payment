package roomescape.reservation.waiting.dto.response;

import jakarta.validation.constraints.NotNull;
import roomescape.reservation.waiting.domain.Waiting;

public record WaitingWithRank(
        @NotNull Waiting waiting,
        @NotNull long rank
) {
}
