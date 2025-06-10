package roomescape.infrastructure.theme;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.domain.theme.entity.Theme;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaThemeRepositoryTest {

    @Autowired
    private JpaThemeRepository jpaThemeRepository;

    @Test
    void 전체_테마_조회() {
        // when & then
        assertThat(jpaThemeRepository.findAll()).hasSize(0);
    }

    @Test
    void 새로운_테마_저장() {
        // given
        final Theme theme = new Theme("테마1", "설명1", "썸네일1");
        jpaThemeRepository.save(theme);

        // when & then
        assertThat(jpaThemeRepository.findAll()).hasSize(1);
    }

    @Test
    void 아이디를_기준으로_테마조회() {
        // given
        final Theme theme = new Theme("테마1", "설명1", "썸네일1");
        final Theme savedTheme = jpaThemeRepository.save(theme);

        // when
        final Theme foundTheme = jpaThemeRepository.findById(savedTheme.getId()).orElseThrow();

        // then
        assertThat(foundTheme.getId()).isEqualTo(savedTheme.getId());
    }


    @Test
    void 아이디를_기준으로_테마삭제() {
        // given
        final Theme theme = new Theme("테마1", "설명1", "썸네일1");
        final Theme savedTheme = jpaThemeRepository.save(theme);

        // when
        jpaThemeRepository.deleteById(theme.getId());

        // then
        assertThat(jpaThemeRepository.findById(savedTheme.getId())).isEmpty();
    }
}
