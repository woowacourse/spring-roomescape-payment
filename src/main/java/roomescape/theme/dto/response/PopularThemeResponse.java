package roomescape.theme.dto.response;

public record PopularThemeResponse(String name, String description, String thumbnail) {
    public PopularThemeResponse {
        if (name == null) {
            throw new IllegalArgumentException("name은 필수입니다.");
        }

        if (thumbnail == null) {
            throw new IllegalArgumentException("thumbnail은 필수입니다.");
        }

        if (description == null) {
            throw new IllegalArgumentException("description은 필수입니다.");
        }
    }
}
