package roomescape.domain.reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

public class ReservationWithRank {
    private final long reservationId;
    private final String theme;
    private final LocalDate date;
    private final LocalTime time;
    private final String status;
    private final long waitingRank;

    public ReservationWithRank(long reservationId, String theme, LocalDate date, LocalTime time, Status status,
                               long waitingRank) {

        this.reservationId = reservationId;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.status = status.toString();
        this.waitingRank = waitingRank;
    }

    public boolean isWaiting() {
        return Objects.equals(status, Status.WAITING.toString());
    }

    public long reservationId() {
        return reservationId;
    }

    public String theme() {
        return theme;
    }

    public LocalDate date() {
        return date;
    }

    public LocalTime time() {
        return time;
    }

    public String status() {
        return status;
    }

    public long waitingRank() {
        return waitingRank;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }
        var that = (ReservationWithRank) obj;
        return this.reservationId == that.reservationId && Objects.equals(this.theme, that.theme) && Objects.equals(
                this.date, that.date) && Objects.equals(this.time, that.time) && Objects.equals(this.status,
                that.status) && this.waitingRank == that.waitingRank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId, theme, date, time, status, waitingRank);
    }

    @Override
    public String toString() {
        return "ReservationWithRank[" + "reservationId=" + reservationId + ", " + "theme=" + theme + ", " + "date="
                + date + ", " + "time=" + time + ", " + "status=" + status + ", " + "waitingRank=" + waitingRank + ']';
    }

}
