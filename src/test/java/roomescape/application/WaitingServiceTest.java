package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.fixture.ThemeFixture.CREATE_THEME_1;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_1;
import static roomescape.fixture.UserFixture.CREATE_USER_1;
import static roomescape.fixture.WaitingFixture.CREATE_WAITING_OF;

import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.application.event.ReservationCancelledEvent;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.domain.reservation.waiting.Waiting;
import roomescape.domain.reservation.waiting.WaitingRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.domain.user.User;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.BusinessRuleViolationException;
import roomescape.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
public class WaitingServiceTest {

    @Mock
    WaitingRepository waitingRepository;

    @Mock
    ReservedRepository reservationRepository;

    @Mock
    TimeSlotRepository timeSlotRepository;

    @Mock
    ThemeRepository themeRepository;

    @InjectMocks
    WaitingService waitingService;

    @Nested
    @DisplayName("예약 대기를 저장한다.")
    class SaveWaiting {

        @Test
        @DisplayName("해당하는 ID의 예약 시간 슬롯이 존재하지 않으면 예외를 던진다.")
        void saveWaiting_WhenTimeSlotNotExists_ThenThrowException() {
            // given
            Long timeId = 1L;
            Theme theme = CREATE_THEME_1();
            User user = CREATE_USER_1();
            LocalDate date = LocalDate.now().plusDays(1);

            when(timeSlotRepository.findById(timeId)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> waitingService.saveWaiting(user, date, timeId, theme.getId()))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 타임 슬롯입니다."),
                    () -> verify(timeSlotRepository).findById(timeId)
            );
        }

        @Test
        @DisplayName("해당하는 ID의 테마가 존재하지 않으면 예외를 던진다.")
        void saveWaiting_WhenThemeNotExists_ThenThrowException() {
            // given
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            User user = CREATE_USER_1();
            LocalDate date = LocalDate.now().plusDays(1);

            when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
            when(themeRepository.findById(theme.getId())).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(
                            () -> waitingService.saveWaiting(user, date, timeSlot.getId(), theme.getId()))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 테마입니다."),
                    () -> verify(timeSlotRepository).findById(timeSlot.getId()),
                    () -> verify(themeRepository).findById(theme.getId())
            );
        }

        @Test
        @DisplayName("날짜, 시간, 테마에 해당하는 예약 대기가 이미 존재하면 예외를 던진다.")
        void saveWaiting_WhenWaitingExists_ThenThrowException() {
            // given
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            User user = CREATE_USER_1();
            LocalDate date = LocalDate.now().plusDays(1);

            when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
            when(themeRepository.findById(theme.getId())).thenReturn(Optional.of(theme));

            when(waitingRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlot.getId(), theme.getId(),
                    user.getId()))
                    .thenReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(
                            () -> waitingService.saveWaiting(user, date, timeSlot.getId(), theme.getId()))
                            .isInstanceOf(AlreadyExistedException.class)
                            .hasMessage("이미 예약 대기한 내역이 있습니다."),
                    () -> verify(timeSlotRepository).findById(timeSlot.getId()),
                    () -> verify(themeRepository).findById(theme.getId()),
                    () -> verify(waitingRepository).existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlot.getId(),
                            theme.getId(), user.getId())
            );
        }

        @Test
        @DisplayName("날짜, 시간, 테마에 해당하는 예약이 이미 존재하면 예외를 던진다.")
        void saveWaiting_WhenReservationExists_ThenThrowException() {
            // given
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            User user = CREATE_USER_1();
            LocalDate date = LocalDate.now().plusDays(1);

            when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
            when(themeRepository.findById(theme.getId())).thenReturn(Optional.of(theme));

            when(waitingRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlot.getId(), theme.getId(),
                    user.getId()))
                    .thenReturn(false);
            when(reservationRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlot.getId(),
                    theme.getId(), user.getId()))
                    .thenReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(
                            () -> waitingService.saveWaiting(user, date, timeSlot.getId(), theme.getId()))
                            .isInstanceOf(BusinessRuleViolationException.class)
                            .hasMessage("해당 테마의 시간대에 이미 예약되어 있습니다."),
                    () -> verify(timeSlotRepository).findById(timeSlot.getId()),
                    () -> verify(themeRepository).findById(theme.getId()),
                    () -> verify(waitingRepository).existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlot.getId(),
                            theme.getId(), user.getId()),
                    () -> verify(reservationRepository).existsByDateAndTimeSlotIdAndThemeIdAndUserId(date,
                            timeSlot.getId(), theme.getId(), user.getId())
            );
        }

        @Test
        @DisplayName("예약 대기를 성공적으로 저장한다.")
        void saveWaiting() {
            // given
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            User user = CREATE_USER_1();
            LocalDate date = LocalDate.now().plusDays(1);
            Waiting waiting = CREATE_WAITING_OF(1L, user, date, timeSlot, theme);

            when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
            when(themeRepository.findById(theme.getId())).thenReturn(Optional.of(theme));

            when(waitingRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlot.getId(), theme.getId(),
                    user.getId()))
                    .thenReturn(false);
            when(reservationRepository.existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlot.getId(),
                    theme.getId(), user.getId()))
                    .thenReturn(false);

            when(waitingRepository.save(Waiting.register(user, date, timeSlot, theme)))
                    .thenReturn(waiting);

            // when & then
            assertAll(
                    () -> assertThat(waitingService.saveWaiting(user, date, timeSlot.getId(), theme.getId())).isEqualTo(
                            waiting),
                    () -> verify(timeSlotRepository).findById(timeSlot.getId()),
                    () -> verify(themeRepository).findById(theme.getId()),
                    () -> verify(waitingRepository).existsByDateAndTimeSlotIdAndThemeIdAndUserId(date, timeSlot.getId(),
                            theme.getId(), user.getId()),
                    () -> verify(reservationRepository).existsByDateAndTimeSlotIdAndThemeIdAndUserId(date,
                            timeSlot.getId(), theme.getId(), user.getId()),
                    () -> verify(waitingRepository).save(any(Waiting.class))
            );
        }
    }

    @Nested
    @DisplayName("예약 대기를 제거한다.")
    class RemoveById {

        @Test
        @DisplayName("해당하는 ID의 예약 대기가 존재하지 않으면 예외를 던진다.")
        void removeById_WhenWaitingNotExists_ThenThrowException() {
            // given
            Long removeId = 1L;

            when(waitingRepository.existsById(removeId)).thenReturn(false);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> waitingService.removeById(removeId))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 예약 대기입니다."),
                    () -> verify(waitingRepository).existsById(removeId),
                    () -> verify(waitingRepository, times(0)).deleteById(removeId)
            );
        }

        @Test
        @DisplayName("예약 대기를 정상적으로 삭제한다.")
        void removeById() {
            // given
            Long removeId = 1L;

            when(waitingRepository.existsById(removeId)).thenReturn(true);

            // when
            waitingService.removeById(removeId);

            // then
            assertAll(
                    () -> verify(waitingRepository).existsById(removeId),
                    () -> verify(waitingRepository).deleteById(removeId)
            );
        }
    }

    @Nested
    @DisplayName("예약 취소 이벤트 처리")
    class HandleReservationCancelled {

        @Test
        @DisplayName("예약 취소 이벤트 발생 시 대기 목록이 있으면 다음 대기자를 예약으로 전환한다")
        void handleReservationCancelled_WithWaiting() {
            // given
            User user = CREATE_USER_1();
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            Waiting waiting = CREATE_WAITING_OF(1L, user, date, timeSlot, theme);
            ReservationCancelledEvent event = new ReservationCancelledEvent(
                    this, date, timeSlot.getId(), theme.getId()
            );

            when(waitingRepository.findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(
                    date, timeSlot.getId(), theme.getId()))
                    .thenReturn(Optional.of(waiting));

            // when
            waitingService.handleReservationCancelled(event);

            // then
            assertAll(
                    () -> verify(waitingRepository, times(1))
                            .findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(date, timeSlot.getId(), theme.getId()),
                    () -> verify(reservationRepository, times(1)).save(any(Reserved.class)),
                    () -> verify(waitingRepository, times(1)).deleteById(waiting.getId())
            );
        }

        @Test
        @DisplayName("예약 취소 이벤트 발생 시 대기 목록이 없으면 아무 작업도 하지 않는다")
        void handleReservationCancelled_WithoutWaiting() {
            // given
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            ReservationCancelledEvent event = new ReservationCancelledEvent(
                    this, date, timeSlot.getId(), theme.getId()
            );

            when(waitingRepository.findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(
                    date, timeSlot.getId(), theme.getId()))
                    .thenReturn(Optional.empty());

            // when
            waitingService.handleReservationCancelled(event);

            // then
            assertAll(
                    () -> verify(waitingRepository, times(1))
                            .findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(date, timeSlot.getId(), theme.getId()),
                    () -> verify(reservationRepository, never()).save(any(Reserved.class)),
                    () -> verify(waitingRepository, never()).deleteById(any(Long.class))
            );

        }
    }
}
