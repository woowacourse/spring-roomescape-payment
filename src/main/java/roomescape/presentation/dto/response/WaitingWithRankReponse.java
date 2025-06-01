package roomescape.presentation.dto.response;

import roomescape.business.model.entity.Waiting;

public record WaitingWithRankReponse(
        Waiting waiting,
        Long aheadCount
) {
}
