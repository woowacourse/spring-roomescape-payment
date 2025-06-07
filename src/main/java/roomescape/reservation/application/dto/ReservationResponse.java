package roomescape.reservation.application.dto;

import java.time.LocalDate;
import roomescape.member.application.dto.MemberResponse;
import roomescape.reservation.domain.Reservation;

public class ReservationResponse {
    private Long id;
    private MemberResponse member;
    private ThemeResponse theme;
    private LocalDate date;
    private ReservationTimeResponse time;
    private String paymentKey;
    private Integer amount;

    private ReservationResponse() {
    }

    public ReservationResponse(final Reservation reservation) {
        this.id = reservation.getId();
        this.member = new MemberResponse(reservation.getMember());
        this.theme = new ThemeResponse(reservation.getTheme());
        this.date = reservation.getReservationInfo().getDate();
        this.time = new ReservationTimeResponse(reservation.getReservationTime());
        this.paymentKey = reservation.getPayment().getPaymentKey();
        this.amount = reservation.getPayment().getAmount();
    }

    public Long getId() {
        return id;
    }

    public MemberResponse getMember() {
        return member;
    }

    public LocalDate getDate() {
        return date;
    }

    public ReservationTimeResponse getTime() {
        return time;
    }

    public ThemeResponse getTheme() {
        return theme;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public Integer getAmount() {
        return amount;
    }
}
