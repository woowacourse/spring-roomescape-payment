package roomescape.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.ReservationStatus;
import roomescape.domain.Waiting;
import roomescape.entity.Payment;
import roomescape.entity.Reservation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;

@Schema(description = "예약 응답 DTO 입니다.")
public record ReservationDetailResponse(
        @Schema(description = "예약 ID입니다.")
        long reservationId,
        @Schema(description = "테마명입니다.")
        String theme,
        @Schema(description = "예약 날짜입니다.")
        LocalDate date,
        @Schema(description = "예약 시간입니다.")
        LocalTime time,
        @Schema(description = "예약 상태입니다.")
        String status,
        @Schema(description = "예약 결제 키 값입니다.")
        String paymentKey,
        @Schema(description = "결제 금액입니다.")
        Long amount
) {

    public static ReservationDetailResponse from(Waiting waiting) {
        Reservation reservation = waiting.getReservation();
        String statusName = ReservationStatusName.findByReservationStatus(reservation.getStatus());
        return toReservationDetailResponse(reservation, String.format(statusName, waiting.getRank()));
    }

    public static ReservationDetailResponse of(Reservation reservation, Payment payment) {
        String statusName = ReservationStatusName.findByReservationStatus(reservation.getStatus());

        if (reservation.isWaitingForPaymentStatus()) {
            return toReservationDetailResponse(reservation, statusName);
        }
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                statusName,
                payment.getPaymentKey(),
                payment.getTotalAmount());
    }

    private static ReservationDetailResponse toReservationDetailResponse(Reservation reservation, String status) {
        return new ReservationDetailResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getReservationTime().getStartAt(),
                status,
                null,
                null);
    }

    private enum ReservationStatusName {
        BOOKED("예약", ReservationStatus.BOOKED),
        WAITING("%d번째 예약대기", ReservationStatus.WAITING),
        WAITING_FOR_PAYMENT("결제 대기", ReservationStatus.WAITING_FOR_PAYMENT);

        private final String statusName;
        private final ReservationStatus status;

        ReservationStatusName(String statusName, ReservationStatus status) {
            this.statusName = statusName;
            this.status = status;
        }

        public static String findByReservationStatus(ReservationStatus status) {
            return Arrays.stream(values())
                    .filter(it -> it.isStatusMatch(status))
                    .findFirst()
                    .map(it -> it.statusName)
                    .orElseThrow(() -> new IllegalArgumentException("일치하는 상태명을 찾을 수 없습니다."));
        }

        private boolean isStatusMatch(ReservationStatus status) {
            return this.status == status;
        }
    }
}
