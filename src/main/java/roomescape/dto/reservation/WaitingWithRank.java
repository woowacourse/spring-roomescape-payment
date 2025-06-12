package roomescape.dto.reservation;

import roomescape.domain.reservation.Waiting;

public record WaitingWithRank(Waiting waiting, Long rank) {
}
