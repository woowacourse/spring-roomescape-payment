package roomescape.response;

import roomescape.model.Theme;

public class ReservationThemeResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final String thumbnail;

    public ReservationThemeResponse(final Long id, final String name, final String description, final String thumbnail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public static ReservationThemeResponse of(final Theme domain) {
        return new ReservationThemeResponse(domain.getId(), domain.getName(), domain.getDescription(), domain.getThumbnail());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}
