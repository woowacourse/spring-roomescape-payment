package roomescape.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.domain.Invoice;

@Schema(description = "예약 및 결제 정보 응답 DTO")
public record InvoiceResponse(
        @Schema(description = "영수증 ID")
        Long id,

        @Schema(description = "예약 정보")
        MyReservationResponse myReservationResponse,

        @Schema(description = "결제 정보")
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
