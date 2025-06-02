package roomescape.reservation.external.toss.dto;

import roomescape.global.api.ExternalApiErrorResponse;

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
