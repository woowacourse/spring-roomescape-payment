package roomescape.presentation.dto.response;

import roomescape.domain.Invoice;

public record InvoiceResponse(
        Long id,
        MyReservationResponse myReservationResponse,
        PaymentResponse paymentResponse
) {

    public static InvoiceResponse from(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                MyReservationResponse.from(invoice.getReservation()),
                PaymentResponse.from(invoice.getPayment())
        );
    }
}
