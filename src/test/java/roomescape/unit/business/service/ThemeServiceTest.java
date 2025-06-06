package roomescape.unit.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import roomescape.business.model.entity.Theme;
import roomescape.business.model.vo.Id;
import roomescape.business.service.ThemeService;
import roomescape.exception.reservation.ReservationExistsException;
import roomescape.exception.reservation.ThemeNotFoundException;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ThemeRepository;
import roomescape.presentation.dto.request.ThemeCreateRequest;
import roomescape.presentation.dto.response.ThemeResponse;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    @Mock
    private ThemeRepository themeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ThemeService sut;

    @Test
    void 테마를_추가하고_반환한다() {
        // given
        String name = "주홍색 연구";
        String description = "셜록 홈즈의 첫 번째 사건";
        String thumbnail = "thumbnail.jpg";
        ThemeCreateRequest request = new ThemeCreateRequest(name, description, thumbnail);
        // when
        ThemeResponse result = sut.addAndGet(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo(name);
        assertThat(result.description()).isEqualTo(description);
        assertThat(result.thumbnail()).isEqualTo(thumbnail);
        verify(themeRepository).save(any(Theme.class));
    }

    @Test
    void 모든_테마를_조회할_수_있다() {
        // given
        List<Theme> themeData = Arrays.asList(
                Theme.restore("theme-id-1", "Theme One", "Description One", "thumbnail1.jpg"),
                Theme.restore("theme-id-2", "Theme Two", "Description Two", "thumbnail2.jpg")
        );

        List<ThemeResponse> expectedThemes = Arrays.asList(
                new ThemeResponse("theme-id-1", "Theme One", "Description One", "thumbnail1.jpg"),
                new ThemeResponse("theme-id-2", "Theme Two", "Description Two", "thumbnail2.jpg")
        );

        when(themeRepository.findAll()).thenReturn(themeData);

        // when
        List<ThemeResponse> result = sut.getAll();

        // then
        assertThat(result).isEqualTo(expectedThemes);
        verify(themeRepository).findAll();
    }

    @Test
    void 인기_테마를_조회할_수_있다() {
        // given
        List<Theme> themeData = Arrays.asList(
                Theme.restore("theme-id-1", "Popular Theme One", "Description One", "thumbnail1.jpg"),
                Theme.restore("theme-id-2", "Popular Theme Two", "Description Two", "thumbnail2.jpg"),
                Theme.restore("theme-id-3", "Popular Theme Three", "Description Three", "thumbnail3.jpg")
        );

        List<ThemeResponse> expectedThemes = Arrays.asList(
                new ThemeResponse("theme-id-1", "Popular Theme One", "Description One", "thumbnail1.jpg"),
                new ThemeResponse("theme-id-2", "Popular Theme Two", "Description Two", "thumbnail2.jpg"),
                new ThemeResponse("theme-id-3", "Popular Theme Three", "Description Three", "thumbnail3.jpg")
        );

        when(themeRepository.findByDateBetweenOrderByReservationCountDescNameAsc(any(LocalDate.class),
                any(LocalDate.class), any(
                        Pageable.class)))
                .thenReturn(themeData);

        // when
        List<ThemeResponse> result = sut.getPopular();

        // then
        assertThat(result).isEqualTo(expectedThemes);
        verify(themeRepository).findByDateBetweenOrderByReservationCountDescNameAsc(any(LocalDate.class),
                any(LocalDate.class), any(
                        Pageable.class));
    }

    @Test
    void 테마를_삭제할_수_있다() {
        // given
        Id themeId = Id.create("theme-id");

        when(reservationRepository.existsByThemeId(themeId)).thenReturn(false);
        when(themeRepository.existsById(themeId)).thenReturn(true);

        // when
        sut.delete(themeId.value());

        // then
        verify(reservationRepository).existsByThemeId(themeId);
        verify(themeRepository).existsById(themeId);
        verify(themeRepository).deleteById(themeId);
    }

    @Test
    void 존재하지_않는_테마_삭제_시_예외가_발생한다() {
        // given
        Id themeId = Id.create("non-existing-id");

        when(reservationRepository.existsByThemeId(themeId)).thenReturn(false);
        when(themeRepository.existsById(themeId)).thenReturn(false);

        // when, then
        assertThatThrownBy(() -> sut.delete(themeId.value()))
                .isInstanceOf(ThemeNotFoundException.class);

        verify(reservationRepository).existsByThemeId(themeId);
        verify(themeRepository).existsById(themeId);
        verify(themeRepository, never()).deleteById(themeId);
    }

    @Test
    void 예약이_연결된_테마_삭제_시_예외가_발생한다() {
        // given
        Id themeId = Id.create("theme-with-reservations");

        when(reservationRepository.existsByThemeId(themeId)).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> sut.delete(themeId.value()))
                .isInstanceOf(ReservationExistsException.class);

        verify(reservationRepository).existsByThemeId(themeId);
        verify(themeRepository, never()).existsById(themeId);
        verify(themeRepository, never()).deleteById(themeId);
    }
}
