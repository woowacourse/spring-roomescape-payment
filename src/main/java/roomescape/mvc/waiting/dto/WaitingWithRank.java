package roomescape.mvc.waiting.dto;

import java.time.LocalDate;
import java.util.Objects;
import roomescape.mvc.member.domain.Member;
import roomescape.mvc.theme.domain.Theme;
import roomescape.mvc.time.domain.ReservationTime;
import roomescape.mvc.waiting.domain.Waiting;

public final class WaitingWithRank {

    private final Long id;
    private final LocalDate date;
    private final Theme theme;
    private final ReservationTime time;
    private final Member member;
    private final Long rank;
    private String paymentKey;
    private Long amount;


    public WaitingWithRank(Waiting waiting, long rank) {
        this.id = waiting.getId();
        this.date = waiting.getDate();
        this.theme = waiting.getTheme();
        this.time = waiting.getTime();
        this.member = waiting.getMember();
        this.rank = rank;
        if (waiting.getPayment() != null) {
            this.paymentKey = waiting.getPayment().getPaymentKey();
            this.amount = waiting.getPayment().getAmount();
        }
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

    public String getPaymentKey() {
        return paymentKey;
    }

    public Long getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WaitingWithRank that = (WaitingWithRank) o;
        return Objects.equals(id, that.id) && Objects.equals(date, that.date)
                && Objects.equals(theme, that.theme) && Objects.equals(time, that.time)
                && Objects.equals(member, that.member) && Objects.equals(rank, that.rank)
                && Objects.equals(paymentKey, that.paymentKey) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, theme, time, member, rank, paymentKey, amount);
    }
}
