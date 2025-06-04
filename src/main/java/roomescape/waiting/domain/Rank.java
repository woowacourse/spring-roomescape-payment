package roomescape.waiting.domain;

import roomescape.common.exception.BadRequestException;

public record Rank(long value) {
    public Rank {
        if (value <= 0) {
            throw new BadRequestException("순위는 0 이하가 될 수 없습니다.");
        }
    }
}
