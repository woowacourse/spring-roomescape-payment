package roomescape.time;

public class AvailableTime {
    private Long timeId;
    private String time;
    private boolean booked;

    public AvailableTime(Long timeId, String time, boolean booked) {
        this.timeId = timeId;
        this.time = time;
        this.booked = booked;
    }

    public Long getTimeId() {
        return timeId;
    }

    public String getTime() {
        return time;
    }

    public boolean isBooked() {
        return booked;
    }
}
