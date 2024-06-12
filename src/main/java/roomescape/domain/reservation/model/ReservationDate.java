package roomescape.domain.reservation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import roomescape.domain.reservation.exception.InvalidReserveInputException;

import java.time.LocalDate;

@Embeddable
public class ReservationDate {

    @Column(nullable = false)
    private LocalDate date;

    protected ReservationDate() {
    }

    public ReservationDate(final LocalDate date) {
        validateDate(date);
        this.date = date;
    }

    private void validateDate(final LocalDate value) {
        if (value == null) {
            throw new InvalidReserveInputException("예약 날짜는 공백을 입력할 수 없습니다.");
        }
    }

    @Override
    public String toString() {
        return "ReservationDate{" +
                "date=" + date +
                '}';
    }

    public LocalDate getValue() {
        return date;
    }
}
