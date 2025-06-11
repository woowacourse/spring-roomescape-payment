package roomescape.domain.reservation.response;

import java.time.LocalDate;
import java.time.LocalTime;
import roomescape.domain.waiting.dto.WaitingWithRank;

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
                waitingWithRank.getThemeName(),
                waitingWithRank.getStartAt(),
                waitingWithRank.getRank(),
                waitingWithRank.getPaymentKey(),
                waitingWithRank.getAmount()
        );
    }
}
