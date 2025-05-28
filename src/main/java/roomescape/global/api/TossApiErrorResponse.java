package roomescape.global.api;

public class TossApiErrorResponse implements ExternalApiErrorResponse {
    private final String message;

    public TossApiErrorResponse(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
