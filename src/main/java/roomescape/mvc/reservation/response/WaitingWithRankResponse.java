package roomescape.mvc.reservation.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.mvc.waiting.dto.WaitingWithRank;

public record WaitingWithRankResponse(
        Long id,
        LocalDate date,
        String themeName,
        LocalTime startAt,
        Long rank,
        String paymentKey,
        Long amount
) {

    public WaitingWithRankResponse(WaitingWithRank waitingWithRank) {
        this(
                waitingWithRank.getId(),
                waitingWithRank.getDate(),
                waitingWithRank.getTheme().getName(),
                waitingWithRank.getTime().getStartAt(),
                waitingWithRank.getRank(),
                waitingWithRank.getPaymentKey(),
                waitingWithRank.getAmount()
        );
    }
}
