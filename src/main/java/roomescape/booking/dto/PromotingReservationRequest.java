package roomescape.booking.dto;

import jakarta.validation.constraints.NotNull;
import roomescape.booking.waiting.Waiting;
import roomescape.member.Member;
import roomescape.schedule.Schedule;

public record PromotingReservationRequest(
        @NotNull Member member,
        @NotNull Schedule schedule
) {

    public static PromotingReservationRequest from(Waiting waiting) {
        return new PromotingReservationRequest(waiting.getMember(), waiting.getSchedule());
    }
}
