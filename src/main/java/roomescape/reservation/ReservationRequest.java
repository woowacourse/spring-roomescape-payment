package roomescape.reservation;

public class ReservationRequest {
    private String name;
    private String date;
    private Long theme;
    private Long time;

    public ReservationRequest() {
    }

    public ReservationRequest(String name, String date, Long theme, Long time) {
        this.name = name;
        this.date = date;
        this.theme = theme;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public Long getTheme() {
        return theme;
    }

    public Long getTime() {
        return time;
    }
}
