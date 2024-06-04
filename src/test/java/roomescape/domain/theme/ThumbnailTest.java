package roomescape.domain.theme;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import roomescape.BaseTest;

class ThumbnailTest extends BaseTest {

    @Test
    void 썸네일이_비어있을_경우_예외_발생() {
        // given
        String thumbnail = "";

        // when, then
        assertThatThrownBy(() -> new Thumbnail(thumbnail))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
