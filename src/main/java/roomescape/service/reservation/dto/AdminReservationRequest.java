package roomescape.service.reservation.dto;

import java.time.DateTimeException;
import java.time.LocalDate;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.exception.common.InvalidRequestBodyException;

public class AdminReservationRequest {
    private final LocalDate date;
    private final Long timeId;
    private final Long themeId;
    private final Long memberId;

    public AdminReservationRequest(String date, String timeId, String themeId, String memberId) {
        validate(date, timeId, themeId, memberId);
        this.date = LocalDate.parse(date);
        this.timeId = Long.parseLong(timeId);
        this.themeId = Long.parseLong(themeId);
        this.memberId = Long.parseLong(memberId);
    }

    public void validate(String date, String timeId, String themeId, String memberId) {
        if (date == null || timeId == null || themeId == null || memberId == null) {
            throw new InvalidRequestBodyException();
        }
        try {
            LocalDate.parse(date);
        } catch (DateTimeException e) {
            throw new InvalidRequestBodyException();
        }
    }

    public Reservation toReservation(ReservationTime reservationTime, Theme theme, Member member) {
        return new Reservation(date, reservationTime, theme, member);
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getTimeId() {
        return timeId;
    }

    public Long getThemeId() {
        return themeId;
    }

    public Long getMemberId() {
        return memberId;
    }
}
