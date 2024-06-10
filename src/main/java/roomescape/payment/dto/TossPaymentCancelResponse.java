package roomescape.payment.dto;

import java.util.List;

public record TossPaymentCancelResponse(String orderName, String currency, List<Cancel> cancels) {
}
