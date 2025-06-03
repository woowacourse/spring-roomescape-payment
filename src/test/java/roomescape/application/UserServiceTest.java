package roomescape.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static roomescape.fixture.PaymentFixture.CREATE_PAYMENT_1;
import static roomescape.fixture.ReservationFixture.CREATE_RESERVATION_OF;
import static roomescape.fixture.ThemeFixture.CREATE_THEME_1;
import static roomescape.fixture.ThemeFixture.CREATE_THEME_2;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_1;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_2;
import static roomescape.fixture.UserFixture.CREATE_USER_1;
import static roomescape.fixture.WaitingFixture.CREATE_WAITING_OF;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.domain.payment.Payment;
import roomescape.domain.reservation.reserved.Reserved;
import roomescape.domain.reservation.reserved.ReservedRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;
import roomescape.domain.user.User;
import roomescape.domain.user.UserRepository;
import roomescape.domain.reservation.waiting.WaitingRepository;
import roomescape.domain.reservation.waiting.WaitingWithRank;
import roomescape.exception.AlreadyExistedException;
import roomescape.exception.NotFoundException;
import roomescape.presentation.response.UserReservationRecordsResponse;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ReservedRepository reservationRepository;

    @Mock
    WaitingRepository waitingRepository;

    @InjectMocks
    UserService userService;

    @Nested
    @DisplayName("사용자를 저장한다.")
    class SaveUser {

        @Test
        @DisplayName("이미 등록된 이메일이 존재하면 예외를 던진다.")
        void saveUser_WhenEmailExists() {
            // given
            User user = CREATE_USER_1();

            when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(
                            () -> userService.saveUser(user.getEmail(), user.getPassword(), user.getName()))
                            .isInstanceOf(AlreadyExistedException.class)
                            .hasMessage("이미 해당 이메일로 가입된 사용자가 있습니다."),
                    () -> verify(userRepository).existsByEmail(user.getEmail())
            );
        }

        @Test
        @DisplayName("사용자를 정상적으로 저장한다.")
        void saveUser() {
            // given
            User user = CREATE_USER_1();

            when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
            when(userRepository.save(User.register(user.getName(), user.getEmail(), user.getPassword()))).thenReturn(
                    user);

            // when
            User savedUser = userService.saveUser(user.getEmail(), user.getPassword(), user.getName());

            // then
            assertAll(
                    () -> assertThat(savedUser).isEqualTo(user),
                    () -> verify(userRepository).existsByEmail(user.getEmail()),
                    () -> verify(userRepository).save(any(User.class))
            );
        }
    }

    @Nested
    @DisplayName("사용자의 예약 및 대기 기록 목록을 조회한다.")
    class FindTotalRecordByUserId {

        @Test
        @DisplayName("사용자가 존재하지 않으면 예외를 던진다.")
        void findTotalRecordByUserId_WhenUserNotExists_ThenThrowException() {
            // given
            Long userId = 99L;

            when(userRepository.existsById(userId)).thenReturn(false);

            // when & then
            assertAll(
                    () -> assertThatThrownBy(() -> userService.findTotalRecordByUserId(userId))
                            .isInstanceOf(NotFoundException.class)
                            .hasMessage("존재하지 않는 사용자입니다."),
                    () -> verify(userRepository).existsById(userId)
            );
        }

        @Test
        @DisplayName("사용자의 예약 및 대기 기록 목록을 정상적으로 조회한다.")
        void findTotalRecordByUserId() {
            // given
            Long userId = 1L;
            User user = CREATE_USER_1();
            Theme theme1 = CREATE_THEME_1();
            Theme theme2 = CREATE_THEME_2();
            TimeSlot timeSlot1 = CREATE_TIME_SLOT_1();
            TimeSlot timeSlot2 = CREATE_TIME_SLOT_2();
            Payment payment = CREATE_PAYMENT_1();
            LocalDate date = LocalDate.now().plusDays(1);

            List<Reserved> reservations = List.of(
                    CREATE_RESERVATION_OF(1L, user, date, timeSlot1, theme1, payment)
            );

            List<WaitingWithRank> waitings = List.of(
                    new WaitingWithRank(CREATE_WAITING_OF(1L, user, date, timeSlot2, theme2), 2)
            );

            List<UserReservationRecordsResponse> expectedResponses = new ArrayList<>();
            expectedResponses.addAll(UserReservationRecordsResponse.fromReservations(reservations));
            expectedResponses.addAll(UserReservationRecordsResponse.fromWaitingsWithRank(waitings));

            when(userRepository.existsById(userId)).thenReturn(true);
            when(reservationRepository.findByUserId(userId)).thenReturn(reservations);
            when(waitingRepository.findWaitingWithRankByUserId(userId)).thenReturn(waitings);

            // when
            List<UserReservationRecordsResponse> actualResponses = userService.findTotalRecordByUserId(userId);

            // then
            assertAll(
                    () -> assertThat(actualResponses).hasSize(expectedResponses.size()),
                    () -> assertThat(actualResponses).containsExactlyInAnyOrderElementsOf(expectedResponses),
                    () -> verify(userRepository).existsById(userId),
                    () -> verify(reservationRepository).findByUserId(userId),
                    () -> verify(waitingRepository).findWaitingWithRankByUserId(userId)
            );
        }
    }
}
