package roomescape.reservation.dto;

import java.util.ResourceBundle;

public enum ReservationStatusResponse {
    BOOKED,
    WAITING,
    ;

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("reservationStatus");

    public String getOutput() {
        return RESOURCE_BUNDLE.getString(name());
    }
}
