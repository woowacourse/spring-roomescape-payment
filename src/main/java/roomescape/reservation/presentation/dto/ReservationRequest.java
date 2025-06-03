package roomescape.reservation.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class ReservationRequest {

    @NotNull(message = "날짜는 반드시 입력해야 합니다")
    private final LocalDate date;

    @NotNull(message = "테마는 반드시 입력해야 합니다")
    private final Long themeId;

    @NotNull(message = "시작 시간은 반드시 입력해야 합니다")
    private final Long timeId;

    @NotBlank(message = "결제 키를 반드시 입력해야 합니다")
    private final String paymentKey;

    @NotBlank(message = "주문 번호를 반드시 입력해야 합니다")
    private final String orderId;

    @NotNull(message = "주문 금액을 반드시 입력해야 합니다")
    private final Integer amount;

    @NotBlank(message = "결제 유형을 반드시 입력해야 합니다")
    private final String paymentType;

    public ReservationRequest(LocalDate date, Long themeId, Long timeId, String paymentKey, String orderId, Integer amount, String paymentType) {
        this.date = date;
        this.themeId = themeId;
        this.timeId = timeId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentType = paymentType;
    }

    public LocalDate getDate() {
        return date;
    }

    public Long getThemeId() {
        return themeId;
    }

    public Long getTimeId() {
        return timeId;
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
