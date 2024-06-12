package roomescape.exception;

public class ParsingFailException extends RuntimeException {

    private final String data;

    public ParsingFailException(String message, String data) {
        super(message);
        this.data = data;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "; Data: " + data;
    }
}
