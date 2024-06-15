package roomescape.service.theme.dto;

import roomescape.domain.theme.Theme;

public class ThemeResponse {
    private final Long id;
    private final String name;
    private final String description;
    private final String thumbnail;
    private final int price;

    public ThemeResponse(Long id, String name, String description, String thumbnail, int price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    public ThemeResponse(Theme theme) {
        this(theme.getId(),
                theme.getName().name(),
                theme.getDescription(),
                theme.getThumbnail(),
                theme.getPrice()
        );
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

    public int getPrice() {
        return price;
    }
}
