package roomescape.presentation.response;

import java.time.LocalDate;
import java.util.List;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.waiting.WaitingWithRank;

public record UserReservedRecordsResponse(
        long id,
        ThemeResponse theme,
        LocalDate date,
        TimeSlotResponse time,
        String status
) {

    public static List<UserReservedRecordsResponse> fromReservations(
            final List<Reservation> reservations
    ) {
        return reservations.stream()
                .map(UserReservedRecordsResponse::fromReservation)
                .toList();
    }

    private static UserReservedRecordsResponse fromReservation(
            final Reservation reservation
    ) {
        return new UserReservedRecordsResponse(
                reservation.getId(),
                ThemeResponse.fromTheme(reservation.getTheme()),
                reservation.getDate(),
                TimeSlotResponse.fromTimeSlot(reservation.getTimeSlot()),
                "예약"
        );
    }

    public static List<UserReservedRecordsResponse> fromWaitingsWithRank(
            final List<WaitingWithRank> waitingsWithRanks
    ) {
        return waitingsWithRanks.stream()
                .map(UserReservedRecordsResponse::fromWaitingWithRank)
                .toList();
    }

    private static UserReservedRecordsResponse fromWaitingWithRank(
            final WaitingWithRank waitingWithRank
    ) {
        return new UserReservedRecordsResponse(
                waitingWithRank.getWaiting().getId(),
                ThemeResponse.fromTheme(waitingWithRank.getWaiting().getTheme()),
                waitingWithRank.getWaiting().getDate(),
                TimeSlotResponse.fromTimeSlot(waitingWithRank.getWaiting().getTimeSlot()),
                waitingWithRank.getRank() + "번째 예약대기"
        );
    }
}
