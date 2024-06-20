package roomescape.domain.theme;

import org.junit.jupiter.api.Test;
import roomescape.exception.theme.InvalidThemePriceRangeException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ThemePriceTest {
    @Test
    void 테마_가격_생성시_가격이_0원_이상이_아니면_예외가_발생한다() {
        int price = -1;
        assertThatThrownBy(() -> new ThemePrice(price))
                .isInstanceOf(InvalidThemePriceRangeException.class);
    }
}