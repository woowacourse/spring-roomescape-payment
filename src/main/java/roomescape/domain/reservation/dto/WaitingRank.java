package roomescape.domain.reservation.dto;

import roomescape.domain.reservation.WaitingMember;

public record WaitingRank(
        WaitingMember waitingMember,
        Long rank
) {
}
