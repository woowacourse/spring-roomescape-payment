package roomescape.business.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.business.model.entity.TimeSlot;
import roomescape.business.model.vo.Id;
import roomescape.exception.business.DuplicatedException;
import roomescape.exception.business.InvalidCreateArgumentException;
import roomescape.exception.business.NotFoundException;
import roomescape.exception.business.RelatedEntityExistException;
import roomescape.infrastructure.ReservationRepository;
import roomescape.infrastructure.ReservationTimeRepository;
import roomescape.presentation.dto.request.ReservationTimeRequest;
import roomescape.presentation.dto.response.ReservationTimeResponse;
import roomescape.presentation.dto.response.ReservationTimeResponseWithBooked;

@ExtendWith(MockitoExtension.class)
class TimeSlotServiceTest {

    @Mock
    private ReservationTimeRepository reservationTimeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationTimeService sut;

    @Test
    void 예약_시간을_추가하고_반환한다() {
        // given
        LocalTime time = LocalTime.of(10, 0);
        ReservationTimeRequest request = new ReservationTimeRequest(String.valueOf(time));

        when(reservationTimeRepository.existsByStartAt(time)).thenReturn(false);
        when(reservationTimeRepository.existsByStartAtBetween(any(LocalTime.class),
                any(LocalTime.class))).thenReturn(false);

        // when
        ReservationTimeResponse result = sut.addAndGet(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.startAt()).isEqualTo(time);
        verify(reservationTimeRepository).existsByStartAt(time);
        verify(reservationTimeRepository).existsByStartAtBetween(any(LocalTime.class), any(LocalTime.class));
        verify(reservationTimeRepository).save(any(TimeSlot.class));
    }

    @Test
    void 중복된_시간으로_예약_시간_추가_시_예외가_발생한다() {
        // given
        LocalTime time = LocalTime.of(10, 0);
        ReservationTimeRequest request = new ReservationTimeRequest(String.valueOf(time));

        when(reservationTimeRepository.existsByStartAt(time)).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> sut.addAndGet(request))
                .isInstanceOf(DuplicatedException.class);

        verify(reservationTimeRepository).existsByStartAt(time);
        verify(reservationTimeRepository, never()).existsByStartAtBetween(any(LocalTime.class),
                any(LocalTime.class));
        verify(reservationTimeRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    void 시간_간격이_겹치는_예약_시간_추가_시_예외가_발생한다() {
        // given
        LocalTime time = LocalTime.of(10, 0);
        ReservationTimeRequest request = new ReservationTimeRequest(String.valueOf(time));

        when(reservationTimeRepository.existsByStartAt(time)).thenReturn(false);
        when(reservationTimeRepository.existsByStartAtBetween(any(LocalTime.class),
                any(LocalTime.class))).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> sut.addAndGet(request))
                .isInstanceOf(InvalidCreateArgumentException.class);

        verify(reservationTimeRepository).existsByStartAt(time);
        verify(reservationTimeRepository).existsByStartAtBetween(any(LocalTime.class), any(LocalTime.class));
        verify(reservationTimeRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    void 모든_예약_시간을_조회할_수_있다() {
        // given
        List<TimeSlot> dataTimes = Arrays.asList(
                TimeSlot.restore("time-id-1", LocalTime.of(10, 0)),
                TimeSlot.restore("time-id-2", LocalTime.of(14, 0))
        );

        List<ReservationTimeResponse> expectedTimes = List.of(
                new ReservationTimeResponse("time-id-1", LocalTime.of(10, 0)),
                new ReservationTimeResponse("time-id-2", LocalTime.of(14, 0))
        );

        when(reservationTimeRepository.findAll()).thenReturn(dataTimes);

        // when
        List<ReservationTimeResponse> result = sut.getAll();

        // then
        assertThat(result).isEqualTo(expectedTimes);
        verify(reservationTimeRepository).findAll();
    }

    @Test
    void 날짜와_테마_ID로_이용_가능한_예약_시간을_조회할_수_있다() {
        // given
        LocalDate date = LocalDate.now();
        Id themeId = Id.create("theme-id");
        List<ReservationTimeResponseWithBooked> expected = Arrays.asList(
                new ReservationTimeResponseWithBooked("time-id-3", LocalTime.of(11, 0), true),
                new ReservationTimeResponseWithBooked("time-id-4", LocalTime.of(15, 0), true),
                new ReservationTimeResponseWithBooked("time-id-5", LocalTime.of(12, 0), false),
                new ReservationTimeResponseWithBooked("time-id-6", LocalTime.of(16, 0), false)
        );

        when(reservationTimeRepository.findByDateAndThemeIdWithAlreadyBooked(date, themeId))
                .thenReturn(expected);

        // when
        List<ReservationTimeResponseWithBooked> result = sut.getAllByDateAndThemeId(date, themeId.value());

        // then
        assertThat(result).containsExactlyElementsOf(expected);
        verify(reservationTimeRepository).findByDateAndThemeIdWithAlreadyBooked(date, themeId);
    }

    @Test
    void 예약_시간을_삭제할_수_있다() {
        // given
        Id timeId = Id.create("time-id");

        when(reservationRepository.existsByTimeSlotId(timeId)).thenReturn(false);
        when(reservationTimeRepository.existsById(timeId)).thenReturn(true);

        // when
        sut.delete(timeId.value());

        // then
        verify(reservationRepository).existsByTimeSlotId(timeId);
        verify(reservationTimeRepository).existsById(timeId);
        verify(reservationTimeRepository).deleteById(timeId);
    }

    @Test
    void 존재하지_않는_예약_시간_삭제_시_예외가_발생한다() {
        // given
        Id timeId = Id.create("non-existing-id");

        when(reservationRepository.existsByTimeSlotId(timeId)).thenReturn(false);
        when(reservationTimeRepository.existsById(timeId)).thenReturn(false);

        // when, then
        assertThatThrownBy(() -> sut.delete(timeId.value()))
                .isInstanceOf(NotFoundException.class);

        verify(reservationRepository).existsByTimeSlotId(timeId);
        verify(reservationTimeRepository).existsById(timeId);
        verify(reservationTimeRepository, never()).deleteById(timeId);
    }

    @Test
    void 예약이_연결된_예약_시간_삭제_시_예외가_발생한다() {
        // given
        Id timeId = Id.create("time-with-reservations");

        when(reservationRepository.existsByTimeSlotId(timeId)).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> sut.delete(timeId.value()))
                .isInstanceOf(RelatedEntityExistException.class);

        verify(reservationRepository).existsByTimeSlotId(timeId);
        verify(reservationTimeRepository, never()).existsById(timeId);
        verify(reservationTimeRepository, never()).deleteById(timeId);
    }
}
