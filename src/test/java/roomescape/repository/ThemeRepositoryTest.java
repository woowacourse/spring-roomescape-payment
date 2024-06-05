package roomescape.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.entity.Theme;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ThemeRepositoryTest {
    @Autowired
    ThemeRepository themeRepository;

    @DisplayName("테마 이름에 대한 존재 여부를 알 수 있다.")
    @ParameterizedTest
    @CsvSource(value = {"테마1, true", "테마2, false"})
    void existsByName(String name, boolean expected) {
        themeRepository.save(new Theme("테마1", "테마1입니다.", "테마1입니다."));

        boolean actual = themeRepository.existsByName(name);

        assertThat(actual).isEqualTo(expected);
    }
}
