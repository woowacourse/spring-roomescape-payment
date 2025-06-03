package roomescape.presentation.response;

import java.time.LocalDate;
import java.util.List;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.waiting.WaitingWithRank;

public record UserReservationRecordsResponse(long id, ThemeResponse theme, LocalDate date, TimeSlotResponse time,
                                             String status, PaymentResponse payment) {

    public static List<UserReservationRecordsResponse> fromReservations(final List<Reserved> reservations) {
        return reservations.stream().map(UserReservationRecordsResponse::fromReserved).toList();
    }

    private static UserReservationRecordsResponse fromReserved(final Reserved reserved) {
        return new UserReservationRecordsResponse(reserved.getId(), ThemeResponse.fromTheme(reserved.getTheme()),
                reserved.getDate(), TimeSlotResponse.fromTimeSlot(reserved.getTimeSlot()), "예약",
                PaymentResponse.from(reserved.getPayment()));
    }

    public static List<UserReservationRecordsResponse> fromWaitingsWithRank(
            final List<WaitingWithRank> waitingsWithRanks) {
        return waitingsWithRanks.stream().map(UserReservationRecordsResponse::fromWaitingWithRank).toList();
    }

    private static UserReservationRecordsResponse fromWaitingWithRank(final WaitingWithRank waitingWithRank) {
        return new UserReservationRecordsResponse(waitingWithRank.getWaiting().getId(),
                ThemeResponse.fromTheme(waitingWithRank.getWaiting().getTheme()),
                waitingWithRank.getWaiting().getDate(),
                TimeSlotResponse.fromTimeSlot(waitingWithRank.getWaiting().getTimeSlot()),
                waitingWithRank.getRank() + "번째 예약대기", null);
    }
}
