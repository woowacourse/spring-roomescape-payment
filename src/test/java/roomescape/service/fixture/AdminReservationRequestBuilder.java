package roomescape.service.fixture;

import roomescape.controller.request.AdminReservationRequest;

import java.time.LocalDate;

public class AdminReservationRequestBuilder {

    private LocalDate date = LocalDate.now().plusDays(99);
    private Long timeId = 1L;
    private Long themeId = 1L;
    private Long memberId = 1L;

    public static AdminReservationRequestBuilder builder() {
        return new AdminReservationRequestBuilder();
    }

    public AdminReservationRequestBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public AdminReservationRequestBuilder timeId(Long timeId) {
        this.timeId = timeId;
        return this;
    }

    public AdminReservationRequestBuilder themeId(Long themeId) {
        this.themeId = themeId;
        return this;
    }


    public AdminReservationRequestBuilder memberId(Long memberId) {
        this.memberId = memberId;
        return this;
    }

    public AdminReservationRequest build() {
        return new AdminReservationRequest(date, timeId, themeId, memberId);
    }
}
