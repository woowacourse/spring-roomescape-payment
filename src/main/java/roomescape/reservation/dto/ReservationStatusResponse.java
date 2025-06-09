package roomescape.reservation.dto;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ReservationStatusResponse {
    RESERVED("예약"),
    WAITING("예약 대기"),
    CANCELED("취소된 예약")
    ;

    private final String description;

    public static ReservationStatusResponse from(String rawStatus) {
        return Arrays.stream(ReservationStatusResponse.values())
                .filter(statusResponse -> statusResponse.name().equals(rawStatus))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("ReservationStatusResponse로 변환할 수 없습니다, 입력값: " + rawStatus));
    }
}
