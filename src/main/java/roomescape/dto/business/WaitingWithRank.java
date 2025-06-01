package roomescape.dto.business;

import java.time.LocalDate;
import roomescape.domain.Member;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.Waiting;
import roomescape.dto.response.PaymentResultResponse;

public record WaitingWithRank(
        Long id,
        LocalDate date,
        Theme theme,
        ReservationTime time,
        Member member,
        Long rank,
        PaymentResultResponse paymentResultResponse
) {

    public WaitingWithRank(Waiting waiting, Long rank) {
        this(waiting.getId(), waiting.getDate(), waiting.getTheme(), waiting.getTime(), waiting.getMember(), rank,
                new PaymentResultResponse(waiting.getPaymentResult()));
    }
}
