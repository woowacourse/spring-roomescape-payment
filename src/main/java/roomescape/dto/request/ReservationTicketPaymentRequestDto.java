package roomescape.dto.request;

public record ReservationTicketPaymentRequestDto(
        ReservationTicketRegisterDto reservationTicketRegisterDto,
        TossPaymentRequestDto tossPaymentRequestDto
) {

}
