package roomescape.waiting;

public class WaitingResponse {
    private Long id;
    private Long theme;
    private String date;
    private String time;
    private int waitingNumber;

    public WaitingResponse(Long id, Long theme, String date, String time, int waitingNumber) {
        this.id = id;
        this.theme = theme;
        this.date = date;
        this.time = time;
        this.waitingNumber = waitingNumber;
    }

    public Long getId() {
        return id;
    }

    public Long getTheme() {
        return theme;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public int getWaitingNumber() {
        return waitingNumber;
    }
}
