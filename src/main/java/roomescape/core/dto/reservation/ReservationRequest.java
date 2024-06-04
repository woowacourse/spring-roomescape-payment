package roomescape.core.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class ReservationRequest {
    @NotNull(message = "예약자 ID는 비어있을 수 없습니다.")
    private Long memberId;

    @NotBlank(message = "날짜는 비어있을 수 없습니다.")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "날짜는 yyyy-MM-dd 형식이어야 합니다.")
    private String date;

    @NotNull(message = "시간 ID는 비어있을 수 없습니다.")
    private Long timeId;

    @NotNull(message = "테마 ID는 비어있을 수 없습니다.")
    private Long themeId;

    private String paymentKey;

    private String orderId;

    public ReservationRequest() {
    }

    public ReservationRequest(final Long memberId, final String date, final Long timeId, final Long themeId) {
        this(memberId, date, timeId, themeId, null, null);
    }

    public ReservationRequest(final Long memberId, final String date, final Long timeId, final Long themeId,
                              final String paymentKey, final String orderId) {
        this.memberId = memberId;
        this.date = date;
        this.timeId = timeId;
        this.themeId = themeId;
        this.paymentKey = paymentKey;
        this.orderId = orderId;
    }

    public Long getMemberId() {
        return memberId;
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
}