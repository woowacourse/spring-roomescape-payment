package roomescape.reservation.application.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import roomescape.approval.application.dto.ApprovalResponse;
import roomescape.approval.domain.Approval;
import roomescape.reservation.domain.Reservation;
import roomescape.waiting.domain.WaitingWithRank;

public record MyReservationResponse(
        Long id,
        String theme,
        LocalDate date,
        LocalTime time,
        String status,
        ApprovalResponse approval
) {
    public static final String RESERVED = "예약";
    public static final String WAITING = "번째 예약 대기";

    public static MyReservationResponse from(Reservation reservation, Approval approval) {
        return new MyReservationResponse(
                reservation.getId(),
                reservation.getTheme().getName(),
                reservation.getDate(),
                reservation.getTime().getStartAt(),
                RESERVED,
                ApprovalResponse.from(approval));
    }

    public static MyReservationResponse from(WaitingWithRank waitingWithRank) {
        return new MyReservationResponse(
                waitingWithRank.getId(),
                waitingWithRank.getTheme().getName(),
                waitingWithRank.getDate(),
                waitingWithRank.getTime().getStartAt(),
                waitingWithRank.getRank() + WAITING,
                null);
    }

    public static List<MyReservationResponse> of(List<Reservation> reservations,
                                                 List<Approval> approvals,
                                                 List<WaitingWithRank> waitings) {
        Map<Long, Approval> approvalMap = approvals.stream()
                .collect(Collectors.toMap(
                        approval -> approval.getReservation().getId(),
                        approval -> approval
                ));

        List<MyReservationResponse> reservationResponses = reservations.stream()
                .map(reservation -> MyReservationResponse.from(
                        reservation,
                        approvalMap.get(reservation.getId())
                ))
                .toList();

        List<MyReservationResponse> waitingResponses = waitings.stream()
                .map(MyReservationResponse::from)
                .toList();

        return Stream.concat(reservationResponses.stream(), waitingResponses.stream())
                .toList();
    }

}
