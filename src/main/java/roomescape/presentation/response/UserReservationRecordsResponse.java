package roomescape.presentation.response;

import java.time.LocalDate;
import java.util.List;
import roomescape.domain.reservation.pendingpayment.PendingPayment;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.waiting.WaitingWithRank;

public record UserReservationRecordsResponse(Long id, ThemeResponse theme, LocalDate date, TimeSlotResponse time,
                                             String status, PaymentResponse payment) {

    private static final String RESERVED_DESCRIPTION = "예약";
    private static final String WAITING_DESCRIPTION_FORMAT = "%d번째 예약대기";
    private static final String PENDING_PAYMENT_DESCRIPTION = "결제 대기";


    public static List<UserReservationRecordsResponse> fromReserves(final List<Reserved> reserveds) {
        return reserveds.stream().map(UserReservationRecordsResponse::fromReserved).toList();
    }

    private static UserReservationRecordsResponse fromReserved(final Reserved reserved) {
        return new UserReservationRecordsResponse(reserved.getId(), ThemeResponse.fromTheme(reserved.getTheme()),
                reserved.getDate(), TimeSlotResponse.fromTimeSlot(reserved.getTimeSlot()),
                RESERVED_DESCRIPTION,
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
                String.format(WAITING_DESCRIPTION_FORMAT, waitingWithRank.getRank()),
                null);
    }

    public static List<UserReservationRecordsResponse> fromPendingPayment(final List<PendingPayment> pendingPayments) {
        return pendingPayments.stream().map(UserReservationRecordsResponse::fromPendingPayment).toList();
    }

    private static UserReservationRecordsResponse fromPendingPayment(final PendingPayment pendingPayment) {
        return new UserReservationRecordsResponse(pendingPayment.getId(),
                ThemeResponse.fromTheme(pendingPayment.getTheme()), pendingPayment.getDate(),
                TimeSlotResponse.fromTimeSlot(pendingPayment.getTimeSlot()),
                PENDING_PAYMENT_DESCRIPTION,
                null);
    }
}
