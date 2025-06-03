package roomescape.presentation.response;

import java.time.LocalDate;
import java.util.List;
import roomescape.domain.reservation.waiting.Waiting;

public record WaitingResponse(
        long id,
        UserResponse user,
        LocalDate date,
        TimeSlotResponse time,
        ThemeResponse theme
) {

    public static List<WaitingResponse> fromWaitings(
            final List<Waiting> waitings
    ) {
        return waitings.stream()
                .map(WaitingResponse::fromWaiting)
                .toList();
    }

    public static WaitingResponse fromWaiting(
            final Waiting waiting
    ) {
        return new WaitingResponse(
                waiting.getId(),
                UserResponse.fromUser(waiting.getUser()),
                waiting.getDate(),
                TimeSlotResponse.fromTimeSlot(waiting.getTimeSlot()),
                ThemeResponse.fromTheme(waiting.getTheme())
        );
    }
}
