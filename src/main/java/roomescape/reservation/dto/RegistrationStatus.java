package roomescape.reservation.dto;

import java.util.Arrays;
import java.util.ResourceBundle;

public enum RegistrationStatus {
    BOOKED,
    WAITING,
    ;

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("reservationStatus");

    public String getOutput() {
        return RESOURCE_BUNDLE.getString(name());
    }

    public static RegistrationStatus from(String type) {
        return Arrays.stream(RegistrationStatus.values())
                .filter(bookingType -> bookingType.name().equalsIgnoreCase(type))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("해당 type을 RegistrationStatus으로 변환할 수 없습니다: " + type));
    }
}
