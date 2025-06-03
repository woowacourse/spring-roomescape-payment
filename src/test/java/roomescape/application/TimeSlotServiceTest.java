package roomescape.application;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.fixture.ThemeFixture.CREATE_THEME_1;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_1;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_2;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_3;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_4;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.AvailableTimeSlot;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.domain.user.User;
import roomescape.exception.InUseException;
import roomescape.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class TimeSlotServiceTest {

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    TimeSlotRepository timeSlotRepository;

    @InjectMocks
    TimeSlotService timeSlotService;

    @Nested
    @DisplayName("예약 시간 슬롯 데이터를 저장한다.")
    class SaveTimeSlot {

        @Test
        @DisplayName("예약 시간 슬롯을 정상적으로 저장한다.")
        void saveTimeSlot() {
            // given
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();

            when(timeSlotRepository.save(TimeSlot.register(timeSlot.getStartAt()))).thenReturn(timeSlot);

            // when
            TimeSlot savedTimeSlot = timeSlotService.saveTimeSlot(timeSlot.getStartAt());

            // then
            assertAll(
                    () -> assertThat(savedTimeSlot).isEqualTo(timeSlot),
                    () -> verify(timeSlotRepository).save(any(TimeSlot.class))
            );
        }
    }

    @Nested
    @DisplayName("이용 가능한 예약 시간 슬롯 목록을 조회한다.")
    class FindAvailableTimeSlots {

        @ParameterizedTest
        @MethodSource
        @DisplayName("이용 가능한 예약 시간 슬롯 목록을 정상적으로 조회한다.")
        void findAvailableTimeSlots(List<TimeSlot> reservedTimeSlots, List<TimeSlot> expectedTimeSlots) {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            Theme theme = CREATE_THEME_1();

            List<TimeSlot> allTimeSlots = List.of(CREATE_TIME_SLOT_1(), CREATE_TIME_SLOT_2(), CREATE_TIME_SLOT_3(),
                    CREATE_TIME_SLOT_4());

            when(timeSlotRepository.findAll()).thenReturn(allTimeSlots);
            when(reservationRepository.findByDateAndThemeId(date, theme.getId()))
                    .thenReturn(toDummyReservation(date, reservedTimeSlots, theme));

            // when
            List<AvailableTimeSlot> actualTimeSlots = timeSlotService.findAvailableTimeSlots(date, theme.getId());

            // then
            assertAll(
                    () -> assertThat(actualTimeSlots)
                            .filteredOn(availableTimeSlot -> !availableTimeSlot.alreadyBooked())
                            .extracting(AvailableTimeSlot::timeSlot)
                            .containsExactlyInAnyOrderElementsOf(expectedTimeSlots),
                    () -> verify(timeSlotRepository).findAll(),
                    () -> verify(reservationRepository).findByDateAndThemeId(date, theme.getId())
            );

        }

        static Stream<Arguments> findAvailableTimeSlots() {
            return Stream.of(
                    Arguments.of(
                            List.of(CREATE_TIME_SLOT_1(), CREATE_TIME_SLOT_2()),
                            List.of(CREATE_TIME_SLOT_3(), CREATE_TIME_SLOT_4())
                    ),
                    Arguments.of(
                            List.of(CREATE_TIME_SLOT_1()),
                            List.of(CREATE_TIME_SLOT_2(), CREATE_TIME_SLOT_3(), CREATE_TIME_SLOT_4())
                    ),
                    Arguments.of(
                            List.of(),
                            List.of(CREATE_TIME_SLOT_1(), CREATE_TIME_SLOT_2(), CREATE_TIME_SLOT_3(),
                                    CREATE_TIME_SLOT_4())
                    ),
                    Arguments.of(
                            List.of(CREATE_TIME_SLOT_1(), CREATE_TIME_SLOT_2(), CREATE_TIME_SLOT_3(),
                                    CREATE_TIME_SLOT_4()),
                            List.of()
                    ),
                    Arguments.of(
                            List.of(CREATE_TIME_SLOT_2(), CREATE_TIME_SLOT_3()),
                            List.of(CREATE_TIME_SLOT_1(), CREATE_TIME_SLOT_4())
                    )
            );
        }

        private List<Reservation> toDummyReservation(LocalDate date, List<TimeSlot> timeSlots, Theme theme) {
            return timeSlots.stream()
                    .map(timeSlot -> Reservation.register(mock(User.class), date, timeSlot, theme))
                    .toList();
        }
    }

    @Nested
    @DisplayName("해당하는 ID의 예약 시간 슬롯을 제거한다.")
    class RemoveById {

        @Test
        @DisplayName("삭제하려는 예약 시간 슬롯을 사용하는 예약이 존재하면 예외를 던진다.")
        void removeById_WhenTimeSlotInUse_ThenThrowException() {
            // given
            Long removeId = 1L;

            when(reservationRepository.existsByTimeSlotId(removeId)).thenReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> timeSlotService.removeById(removeId))
                            .isInstanceOf(InUseException.class)
                            .hasMessage("삭제하려는 타임 슬롯을 사용하는 예약이 있습니다."),
                    () -> verify(reservationRepository).existsByTimeSlotId(removeId)
            );
        }

        @Test
        @DisplayName("삭제하려는 예약 시간 슬롯이 존재하지 않으면 예외를 던진다.")
        void removeById_WhenTimeSlotNotExists_ThenThrowException() {
            // given
            Long removeId = 1L;

            when(timeSlotRepository.existsById(removeId)).thenReturn(false);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> timeSlotService.removeById(removeId))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 타임슬롯입니다."),
                    () -> verify(timeSlotRepository).existsById(removeId)
            );
        }
    }
}
