package roomescape.presentation.dto.response;

import java.time.LocalDate;
import lombok.Getter;
import roomescape.business.model.entity.Waiting;

@Getter
public class WaitingWithRankResponse {

    private final String id;
    private final MemberResponse member;
    private final LocalDate date;
    private final TimeSlotResponse time;
    private final ThemeResponse theme;
    private final Long aheadCount;

    public WaitingWithRankResponse(Waiting waiting, Long aheadCount) {
        this.id = waiting.getId().value();
        this.member = MemberResponse.from(waiting.getMember());
        this.date = waiting.getDate().value();
        this.time = TimeSlotResponse.from(waiting.getTime());
        this.theme = ThemeResponse.from(waiting.getTheme());
        this.aheadCount = aheadCount;
    }
}
