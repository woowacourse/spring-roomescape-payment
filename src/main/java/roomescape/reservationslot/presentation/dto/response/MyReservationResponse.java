package roomescape.reservationslot.presentation.dto.response;

public record MyReservationResponse(Long reservationSlotId,
                                    String theme,
                                    String date,
                                    String time,
                                    String paymentKey,
                                    Long amount,
                                    boolean isReserved,
                                    long waitingRank) {

}
