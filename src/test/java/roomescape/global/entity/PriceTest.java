package roomescape.global.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("가격 도메인 테스트")
class PriceTest {

    @DisplayName("형식에 맞지 않은 가격 생성 시, 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(doubles = {-1000.0, -1.1, 210000000000000000L})
    void invalidPrice(Double invalidPrice) {
        //given & when & then
        BigDecimal bigDecimal = BigDecimal.valueOf(0);

        assertAll(
                () -> assertThat(new Price(bigDecimal).getPrice()).isEqualTo(bigDecimal),
                () -> assertThatThrownBy(() -> new Price(BigDecimal.valueOf(invalidPrice)))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }
}
