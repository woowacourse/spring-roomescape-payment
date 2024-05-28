package roomescape.core.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ReservationPaymentRequest {
    @NotBlank(message = "날짜는 비어있을 수 없습니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 yyyy-MM-dd 형식이어야 합니다.")
    private String date;

    @NotNull(message = "시간 ID는 null일 수 없습니다.")
    private Long timeId;

    @NotNull(message = "테마 ID는 null일 수 없습니다.")
    private Long themeId;

    @NotNull(message = "결제 키는 null일 수 없습니다.")
    private String paymentKey;

    @NotNull(message = "주문 ID는 null일 수 없습니다.")
    private String orderId;

    @NotNull(message = "결제 금액은 null일 수 없습니다.")
    private Integer amount;

    @NotBlank(message = "결제 타입은 비어있을 수 없습니다.")
    private String paymentType;

    public ReservationPaymentRequest(final String date, final Long timeId, final Long themeId, final String paymentKey,
                                     final String orderId, final Integer amount, final String paymentType) {
        this.date = date;
        this.timeId = timeId;
        this.themeId = themeId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public String getDate() {
        return date;
    }

    public Long getTimeId() {
        return timeId;
    }

    public Long getThemeId() {
        return themeId;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Integer getAmount() {
        return amount;
    }

    public String getPaymentType() {
        return paymentType;
    }
}
