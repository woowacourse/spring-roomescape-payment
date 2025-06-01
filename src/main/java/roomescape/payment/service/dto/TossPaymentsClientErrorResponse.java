package roomescape.payment.service.dto;

public record TossPaymentsClientErrorResponse(
    String code,
    String message,
    String data
) {

}
