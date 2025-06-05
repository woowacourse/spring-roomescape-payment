package roomescape.domain.payment;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import roomescape.exception.BusinessRuleViolationException;

@Embeddable
public record OrderId(
        @Column(name = "order_id", nullable = false, length = MAX_LENGTH)
        String value
) {

    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 64;
    private static final Pattern VALID_PATTERN = Pattern.compile("^[a-zA-Z0-9_-]+$");

    public OrderId {
        if (value.length() < MIN_LENGTH) {
            throw new BusinessRuleViolationException(String.format("주문번호는 %d자 이상이어야 합니다.", MIN_LENGTH));
        }

        if (value.length() > MAX_LENGTH) {
            throw new BusinessRuleViolationException(String.format("주문번호는 %d자를 넘길 수 없습니다.", MAX_LENGTH));
        }

        if (!VALID_PATTERN.matcher(value).matches()) {
            throw new BusinessRuleViolationException("잘못된 형식의 주문번호입니다 : " + value);
        }
    }
}
