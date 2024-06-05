package roomescape.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String thumbnail;
    private Long price;

    public Theme() {
    }

    public Theme(String name, String description, String thumbnail, Long price) {
        this(null, name, description, thumbnail, price);
    }

    public Theme(Long id, String name, String description, String thumbnail, Long price) {
        validateName(name);
        validateDescription(description);
        validateThumbnail(thumbnail);
        validatePrice(price);
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("잘못된 테마 이름을 입력하셨습니다.");
        }
    }

    private void validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("잘못된 테마 설명을 입력하셨습니다.");
        }
    }

    private void validateThumbnail(String thumbnail) {
        if (thumbnail == null || !thumbnail.startsWith("https://")) {
            throw new IllegalArgumentException("잘못된 형식의 썸네일 url입니다.");
        }
    }

    private void validatePrice(Long price) {
        if (price == null) {
            throw new IllegalArgumentException("가격이 비어 있습니다.");
        }
        if (price < 0) {
            throw new IllegalArgumentException("가격은 음수가 될 수 없습니다.");
        }
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

    public Long getPrice() {
        return price;
    }
}
