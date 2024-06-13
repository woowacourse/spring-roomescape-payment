package roomescape.application.dto.response;

public record PaymentConfirmApiResponse(String status) {

    private static final String STATUS_DONE = "DONE";

    public boolean isNotDone() {
        return !STATUS_DONE.equals(status);
    }
}
