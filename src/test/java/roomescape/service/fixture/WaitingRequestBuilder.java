package roomescape.service.fixture;

import roomescape.request.WaitingRequest;

import java.time.LocalDate;

public class WaitingRequestBuilder {

    private LocalDate date = LocalDate.now().plusDays(99);
    private Long timeId = 1L;
    private Long themeId = 1L;

    public static WaitingRequestBuilder builder() {
        return new WaitingRequestBuilder();
    }

    public WaitingRequestBuilder date(LocalDate date) {
        this.date = date;
        return this;
    }

    public WaitingRequestBuilder timeId(Long timeId) {
        this.timeId = timeId;
        return this;
    }

    public WaitingRequestBuilder themeId(Long themeId) {
        this.themeId = themeId;
        return this;
    }

    public WaitingRequest build() {
        return new WaitingRequest(date, timeId, themeId);
    }
}
