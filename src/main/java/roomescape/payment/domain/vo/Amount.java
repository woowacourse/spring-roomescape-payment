package roomescape.payment.domain.vo;

import jakarta.persistence.Embeddable;

@Embeddable
public record Amount(Long value) {

    public Amount {
        if (value < 0) {
            throw new IllegalArgumentException("금액은 음수가 될 수 없습니다.");
        }
    }
}
