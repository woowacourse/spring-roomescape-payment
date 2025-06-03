package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.fixture.PaymentFixture.CREATE_PAYMENT_1;
import static roomescape.fixture.ReservationFixture.CREATE_RESERVATION_OF;
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
import roomescape.application.request.PaymentInfo;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.timeslot.TimeSlotRepository;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRepository;
import roomescape.domain.waiting.Waiting;
import roomescape.domain.waiting.WaitingRepository;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.NotFoundException;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    WaitingRepository waitingRepository;

    @Mock
    TimeSlotRepository timeSlotRepository;

    @Mock
    ThemeRepository themeRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PaymentService paymentService;

    @InjectMocks
    ReservationService reservationService;


    @Nested
    @DisplayName("결제 정보를 통해 예약을 저장한다.")
    class SaveReservationWithPurchase {

        @Test
        @DisplayName("날짜, 시간, 테마에 해당하는 예약이 이미 존재하면 예외를 던진다.")
        void saveReservationWithPurchase_WhenReservationExists_ThenThrowException() {
            // given
            User user = CREATE_USER_1();
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            LocalDate date = LocalDate.now().plusDays(1);

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
            when(themeRepository.findById(theme.getId())).thenReturn(Optional.of(theme));

            when(reservationRepository.existsByDateAndTimeSlotIdAndThemeId(date, timeSlot.getId(), theme.getId()))
                    .thenReturn(true);

            PaymentInfo paymentInfo = new PaymentInfo("payment_key_1", "order_id_1", 10000L);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(
                            () -> reservationService.saveReservationWithPurchase(user.getId(), date, timeSlot.getId(),
                                    theme.getId(), paymentInfo))
                            .isInstanceOf(AlreadyExistedException.class)
                            .hasMessage("이미 예약된 날짜, 시간, 테마에 대한 예약은 불가능합니다."),
                    () -> verify(userRepository).findById(user.getId()),
                    () -> verify(timeSlotRepository).findById(timeSlot.getId()),
                    () -> verify(themeRepository).findById(theme.getId()),
                    () -> verify(reservationRepository).existsByDateAndTimeSlotIdAndThemeId(date, timeSlot.getId(),
                            theme.getId())
            );

        }

        @Test
        @DisplayName("결제 정보를 통해 예약을 정상적으로 저장한다.")
        void saveReservationWithPurchase() {
            // given
            User user = CREATE_USER_1();
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            LocalDate date = LocalDate.now().plusDays(1);
            Payment payment = CREATE_PAYMENT_1();

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
            when(themeRepository.findById(theme.getId())).thenReturn(Optional.of(theme));
            when(reservationRepository.save(Reservation.register(user, date, timeSlot, theme)))
                    .thenReturn(CREATE_RESERVATION_OF(1L, user, date, timeSlot, theme));

            PaymentInfo paymentInfo = new PaymentInfo("payment_key_1", "order_id_1", 10000L);

            when(paymentService.savePayment(paymentInfo)).thenReturn(payment);

            // when
            Reservation savedReservation = reservationService.saveReservationWithPurchase(
                    user.getId(), date, timeSlot.getId(), theme.getId(), paymentInfo);

            // then
            assertAll(
                    () -> assertThat(savedReservation.getUser()).isEqualTo(user),
                    () -> assertThat(savedReservation.getDate()).isEqualTo(date),
                    () -> assertThat(savedReservation.getTimeSlot()).isEqualTo(timeSlot),
                    () -> assertThat(savedReservation.getTheme()).isEqualTo(theme),
                    () -> assertThat(savedReservation.getPayment()).isEqualTo(payment),

                    () -> verify(userRepository).findById(user.getId()),
                    () -> verify(timeSlotRepository).findById(timeSlot.getId()),
                    () -> verify(themeRepository).findById(theme.getId()),
                    () -> verify(paymentService).savePayment(any()),
                    () -> verify(reservationRepository).save(any(Reservation.class))
            );
        }
    }


    @Nested
    @DisplayName("결제를 진행하지 않고 예약을 저장한다.")
    class saveReservationWithoutPurchase {

        @Test
        @DisplayName("결제를 진행하지 않고 정상적으로 예약을 저장한다.")
        void saveReservationWithoutPurchase() {
            // given
            User user = CREATE_USER_1();
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            LocalDate date = LocalDate.now().plusDays(1);
            Reservation reservation = CREATE_RESERVATION_OF(1L, user, date, timeSlot, theme);

            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(timeSlotRepository.findById(timeSlot.getId())).thenReturn(Optional.of(timeSlot));
            when(themeRepository.findById(theme.getId())).thenReturn(Optional.of(theme));

            when(reservationRepository.save(Reservation.register(user, date, timeSlot, theme)))
                    .thenReturn(reservation);

            // when
            Reservation savedReservation = reservationService.saveReservationWithoutPurchase(user.getId(), date,
                    timeSlot.getId(), theme.getId());

            // then
            assertAll(
                    () -> assertThat(savedReservation.getUser()).isEqualTo(user),
                    () -> assertThat(savedReservation.getDate()).isEqualTo(date),
                    () -> assertThat(savedReservation.getTimeSlot()).isEqualTo(timeSlot),
                    () -> assertThat(savedReservation.getTheme()).isEqualTo(theme),
                    () -> assertThat(savedReservation.getPayment()).isNull(),

                    () -> verify(userRepository).findById(user.getId()),
                    () -> verify(timeSlotRepository).findById(timeSlot.getId()),
                    () -> verify(themeRepository).findById(theme.getId()),
                    () -> verify(reservationRepository).save(any(Reservation.class))
            );
        }
    }


    @Nested
    @DisplayName("ID 값에 해당하는 예약이 없으면 예외를 던진다.")
    class removeById {

        @Test
        @DisplayName("ID 값에 해당하는 예약이 없으면 예외를 던진다.")
        void removeById() {
            // given
            Long findId = 99L;
            when(reservationRepository.findById(findId)).thenReturn(Optional.empty());

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> reservationService.removeById(findId))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 예약입니다."),
                    () -> verify(reservationRepository).findById(findId)
            );
        }

        @Test
        @DisplayName("예약 삭제 시 예약 대기가 존재하면 예약으로 추가하고 예약 대기가 삭제된다.")
        void removeById_WhenWaitingExists_ThenApproveNextWaiting() {
            // given
            Long removeId = 1L;
            User user = CREATE_USER_1();
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            Waiting waiting = CREATE_WAITING_OF(1L, user, date, timeSlot, theme);

            when(reservationRepository.findById(removeId)).thenReturn(
                    Optional.of(CREATE_RESERVATION_OF(removeId, user, date, timeSlot, theme)));

            when(waitingRepository.findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(date, timeSlot.getId(),
                    theme.getId()))
                    .thenReturn(Optional.of(waiting));

            doNothing().when(waitingRepository).deleteById(removeId);

            // when
            reservationService.removeById(removeId);

            // then
            assertAll(
                    () -> verify(waitingRepository).findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(date,
                            timeSlot.getId(), theme.getId()),
                    () -> verify(reservationRepository).save(Reservation.fromWaiting(waiting)),
                    () -> verify(waitingRepository).deleteById(waiting.getId())
            );
        }

        @Test
        @DisplayName("예약 삭제 시 예약 대기가 존재하지 않으면 예약을 삭제한다.")
        void removeById_WhenWaitingEmpty() {
            // given
            Long removeId = 1L;
            User user = CREATE_USER_1();
            LocalDate date = LocalDate.now().plusDays(1);
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();

            when(reservationRepository.findById(removeId)).thenReturn(
                    Optional.of(CREATE_RESERVATION_OF(removeId, user, date, timeSlot, theme)));

            when(waitingRepository.findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(date, timeSlot.getId(),
                    theme.getId()))
                    .thenReturn(Optional.empty());

            // when
            reservationService.removeById(removeId);

            // then
            assertAll(
                    () -> verify(waitingRepository).findFirstByDateAndTimeSlotIdAndThemeIdOrderByIdAsc(date,
                            timeSlot.getId(), theme.getId()),
                    () -> verify(reservationRepository, never()).save(any(Reservation.class)),
                    () -> verify(waitingRepository, never()).deleteById(anyLong())
            );
        }
    }
}
