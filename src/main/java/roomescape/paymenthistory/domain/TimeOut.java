package roomescape.paymenthistory.domain;

public class TimeOut {

    private static final String ILLEGAL_TIME_NUMBER = "TimeOut 시간은 0이거나 음수가 될 수 없습니다.";
    private static final String TIME_IS_NULL = "TimeOut 시간이 null 입니다.";

    private final int timeOutTime;

    public TimeOut(String timeOutTime) {
        validation(timeOutTime);
        this.timeOutTime = Integer.parseInt(timeOutTime);
    }

    public int getTimeOutTime() {
        return timeOutTime;
    }

    private void validation(String time) {
        if (time == null) {
            throw new IllegalArgumentException(TIME_IS_NULL);
        }
        if (Integer.parseInt(time) <= 0) {
            throw new IllegalArgumentException(ILLEGAL_TIME_NUMBER);
        }
    }
}
