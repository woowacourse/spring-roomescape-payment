package roomescape.domain.reservation.theme;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "잘못된 테마 이름을 입력하셨습니다.")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "잘못된 테마 설명을 입력하셨습니다.")
    private String description;

    @Embedded
    @AttributeOverride(name = "thumbnail", column = @Column(nullable = false))
    @NotNull(message = "잘못된 형식의 썸네일 url입니다.")
    private Thumbnail thumbnail;

    @Embedded
    @AttributeOverride(name = "price", column = @Column(nullable = false))
    @NotNull(message = "가격이 비어 있습니다.")
    private Price price;

    public Theme() {
    }

    public Theme(String name, String description, String thumbnail, Long price) {
        this(null, name, description, new Thumbnail(thumbnail), new Price(price));
    }

    public Theme(Long id, String name, String description, String thumbnail, Long price) {
        this(id, name, description, new Thumbnail(thumbnail), new Price(price));
    }

    public Theme(Long id, String name, String description, Thumbnail thumbnail, Price price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.thumbnail = thumbnail;
        this.price = price;
    }

    public boolean isPriceEqual(Long price) {
        return this.price.isPriceEquals(price);
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
        return thumbnail.getThumbnail();
    }

    public Long getPrice() {
        return price.getPrice();
    }
}
