package roomescape.reservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import roomescape.payment.domain.PaymentStatus;
import roomescape.reservation.repository.dto.MemberRegistrationProjection;

public record MyReservationResponse(
        Long reservationId,
        String theme,
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "HH:mm") LocalTime time,
        String reservationStatus,
        int rank,
        String paymentKey,
        long amount,
        String paymentStatus
) {
    public static MyReservationResponse from(MemberRegistrationProjection projection) {
        return new MyReservationResponse(
                projection.getId(),
                projection.getThemeName(),
                projection.getDate(),
                projection.getTime(),
                ReservationStatus.from(projection.getReservationStatus()).getOutput(),
                projection.getRank(),
                projection.getPaymentKey(),
                getAmount(projection),
                //TODO: ReservationStatus와 같이 getOutput 형태로 고치기
                getDescription(projection)
        );
    }

    private static String getDescription(MemberRegistrationProjection projection) {
        String paymentStatus = projection.getPaymentStatus();
        if(paymentStatus == null) {
            return "";
        }
        return PaymentStatus.valueOf(paymentStatus).getDescription();
    }

    private static Long getAmount(MemberRegistrationProjection projection) {
        Long amount = projection.getAmount();
        if (amount == null) {
            return 0L;
        }
        return projection.getAmount();
    }

    public static List<MyReservationResponse> from(List<MemberRegistrationProjection> projections) {
        return projections.stream()
                .map(MyReservationResponse::from)
                .toList();
    }
}
