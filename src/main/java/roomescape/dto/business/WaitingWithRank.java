package roomescape.dto.business;

import java.time.LocalDate;
import roomescape.domain.Member;
import roomescape.domain.PaymentResult;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.dto.response.PaymentResultResponse;

public class WaitingWithRank {
    private final Long id;
    private final LocalDate date;
    private final Theme theme;
    private final ReservationTime time;
    private final Member member;
    private final Long rank;
    private final PaymentResultResponse paymentResultResponse;

    public WaitingWithRank(Long id, LocalDate date, Theme theme, ReservationTime time, Member member, Long rank,
                           PaymentResult paymentResult) {
        this.id = id;
        this.date = date;
        this.theme = theme;
        this.time = time;
        this.member = member;
        this.rank = rank;
        this.paymentResultResponse = new PaymentResultResponse(paymentResult);
    }

    public WaitingWithRank(Long id, LocalDate date, Theme theme, ReservationTime time, Member member, Long rank) {
        this(id, date, theme, time, member, rank, null);
    }


    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public Theme getTheme() {
        return theme;
    }

    public ReservationTime getTime() {
        return time;
    }

    public Member getMember() {
        return member;
    }

    public Long getRank() {
        return rank;
    }

    public PaymentResultResponse getPaymentResultResponse() {
        return paymentResultResponse;
    }
}
