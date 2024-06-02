package roomescape.theme.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import roomescape.theme.entity.Theme;

public interface ThemeRepository extends JpaRepository<Theme, Long> {
}
