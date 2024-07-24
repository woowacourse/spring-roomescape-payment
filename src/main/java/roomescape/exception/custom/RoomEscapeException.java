package roomescape.exception.custom;

public class RoomEscapeException extends IllegalArgumentException {

    private final String input;

    public RoomEscapeException(final String message, final String input) {
        super(message);
        this.input = input;
    }

    public RoomEscapeException(final String message) {
        super(message);
        this.input = "default input";
    }

    public String getInput() {
        return input;
    }
}
