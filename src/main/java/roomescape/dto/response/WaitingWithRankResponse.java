package roomescape.dto.response;

import java.time.LocalDate;
import roomescape.dto.business.WaitingWithRank;

public record WaitingWithRankResponse(
        Long id,
        LocalDate date,
        ThemeProfileResponse theme,
        ReservationTimeResponse time,
        MemberProfileResponse member,
        Long rank,
        PaymentResultResponse paymentResultResponse
) {

    public WaitingWithRankResponse(WaitingWithRank waitingWithRank) {
        this(
                waitingWithRank.getId(),
                waitingWithRank.getDate(),
                new ThemeProfileResponse(waitingWithRank.getTheme()),
                new ReservationTimeResponse(waitingWithRank.getTime()),
                new MemberProfileResponse(waitingWithRank.getMember()),
                waitingWithRank.getRank(),
                waitingWithRank.getPaymentResultResponse()
        );
    }
}
