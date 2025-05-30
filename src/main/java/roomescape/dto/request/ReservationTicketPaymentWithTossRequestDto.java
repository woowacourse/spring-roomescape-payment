package roomescape.dto.request;

public record ReservationTicketPaymentWithTossRequestDto(
        ReservationTicketRegisterDto reservationTicketRegisterDto,
        TossPaymentRequestDto tossPaymentRequestDto
) {

}
