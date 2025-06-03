package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.fixture.ThemeFixture.CREATE_THEME_1;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.exception.InUseException;
import roomescape.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class ThemeServiceTest {

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    ThemeRepository themeRepository;

    @InjectMocks
    ThemeService themeService;

    @Nested
    @DisplayName("테마를 저장한다.")
    class SaveTheme {

        @Test
        @DisplayName("테마를 정상적으로 저장한다.")
        void saveTheme() {
            // given
            Theme theme = CREATE_THEME_1();

            when(themeRepository.save(Theme.register(theme.getName(), theme.getDescription(), theme.getThumbnail())))
                    .thenReturn(theme);

            // when
            Theme savedTheme = themeService.saveTheme(theme.getName(), theme.getDescription(), theme.getThumbnail());

            // then
            assertAll(
                    () -> assertThat(savedTheme).isEqualTo(theme),
                    () -> verify(themeRepository).save(any(Theme.class))
            );
        }
    }

    @Nested
    @DisplayName("인기 테마 목록을 조회한다.")
    class FindMostPopularThemes {

        @Test
        @DisplayName("요청한 개수가 최대 조회 개수보다 작으면 요청한 개수만큼 조회한다.")
        void findMostPopularThemes_WhenRequestLessThanMaxCount_thenUseRequestedCount() {
            // given
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            int requestedCount = 3;

            List<Theme> expectedThemes = List.of(mock(Theme.class), mock(Theme.class), mock(Theme.class));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            when(themeRepository.findRankingByPeriod(eq(startDate), eq(endDate), pageableCaptor.capture()))
                    .thenReturn(expectedThemes);

            // when
            themeService.findPopularThemes(startDate, endDate, requestedCount);

            assertAll(
                    () -> assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(requestedCount),
                    () -> assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0)
            );
        }

        @Test
        @DisplayName("요청한 개수가 최대 조회 개수보다 많으면 최대 조회 개수만큼 조회한다.")
        void findMostPopularThemes_WhenRequestMoreThanMaxCount_thenUseMaxCount() {
            // given
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            int requestedCount = 7;

            List<Theme> expectedThemes = List.of(mock(Theme.class), mock(Theme.class), mock(Theme.class),
                    mock(Theme.class), mock(Theme.class));

            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

            when(themeRepository.findRankingByPeriod(eq(startDate), eq(endDate), pageableCaptor.capture()))
                    .thenReturn(expectedThemes);

            // when
            themeService.findPopularThemes(startDate, endDate, requestedCount);

            assertAll(
                    () -> assertThat(pageableCaptor.getValue().getPageSize()).isEqualTo(5),
                    () -> assertThat(pageableCaptor.getValue().getPageNumber()).isEqualTo(0)
            );
        }
    }

    @Nested
    @DisplayName("ID값에 해당하는 테마를 제거한다.")
    class RemoveById {

        @Test
        @DisplayName("삭제하려는 테마를 사용하는 예약이 있으면 예외를 던진다.")
        void removeById_WhenThemeInUse_ThenThrowException() {
            // given
            Long removeId = 1L;

            when(reservationRepository.existsByThemeId(removeId)).thenReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> themeService.removeById(removeId))
                            .isInstanceOf(InUseException.class)
                            .hasMessage("삭제하려는 테마를 사용하는 예약이 있습니다."),
                    () -> verify(reservationRepository).existsByThemeId(removeId)
            );
        }

        @Test
        @DisplayName("삭제하려는 테마가 존재하지 않으면 예외를 던진다.")
        void removeById_WhenThemeNotExists_ThenThrowException() {
            // given
            Long removeId = 99L;

            when(themeRepository.existsById(removeId)).thenReturn(false);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> themeService.removeById(removeId))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 테마입니다."),
                    () -> verify(themeRepository).existsById(removeId)
            );
        }

        @Test
        @DisplayName("테마를 정상적으로 삭제한다.")
        void removeById() {
            // given
            Long removeId = 1L;

            when(reservationRepository.existsByThemeId(removeId)).thenReturn(false);
            when(themeRepository.existsById(removeId)).thenReturn(true);

            // when
            themeService.removeById(removeId);

            // then
            assertAll(
                    () -> verify(reservationRepository).existsByThemeId(removeId),
                    () -> verify(themeRepository).existsById(removeId)
            );
        }
    }
}
