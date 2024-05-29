package roomescape.core.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class MemberReservationRequest {
    @NotBlank(message = "날짜는 비어있을 수 없습니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 yyyy-MM-dd 형식이어야 합니다.")
    private String date;

    @NotNull(message = "시간 ID는 null일 수 없습니다.")
    private Long timeId;

    @NotNull(message = "테마 ID는 null일 수 없습니다.")
    private Long themeId;

    @NotBlank(message = "status 는 비어있을 수 없습니다.")
    private String status;

    @NotBlank(message = "paymentKey 는 비어있을 수 없습니다.")
    private String paymentKey;

    @NotBlank(message = "orderId 는 비어있을 수 없습니다.")
    private String orderId;

    @NotNull(message = "amount 는 null일 수 없습니다.")
    private Long amount;

    public MemberReservationRequest() {
    }

    public MemberReservationRequest(String date, Long timeId, Long themeId, String status, String paymentKey,
                                    String orderId, Long amount) {
        this.date = date;
        this.timeId = timeId;
        this.themeId = themeId;
        this.status = status;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
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

    public String getStatus() {
        return status;
    }

    public String getPaymentKey() {
        return paymentKey;
    }

    public String getOrderId() {
        return orderId;
    }

    public Long getAmount() {
        return amount;
    }
}
