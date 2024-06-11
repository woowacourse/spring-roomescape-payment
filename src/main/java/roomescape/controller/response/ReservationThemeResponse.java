package roomescape.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import roomescape.model.Theme;

public class ReservationThemeResponse {

    @Schema(description = "테마 ID", example = "1")
    private Long id;
    @Schema(description = "테마 이름", example = "에버")
    private String name;
    @Schema(description = "테마 설명", example = "공포")
    private String description;
    @Schema(description = "테마 썸네일 주소", example = "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg")
    private String thumbnail;

    public ReservationThemeResponse() {
    }

    public ReservationThemeResponse(Long id, String name, String description, String thumbnail) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
    }

    public static ReservationThemeResponse of(Theme domain) {
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
