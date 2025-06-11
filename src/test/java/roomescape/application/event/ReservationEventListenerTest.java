package roomescape.application.event;

import static org.mockito.Mockito.verify;
import static roomescape.fixture.ThemeFixture.CREATE_THEME_1;
import static roomescape.fixture.TimeSlotFixture.CREATE_TIME_SLOT_1;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.application.WaitingService;
import roomescape.domain.theme.Theme;
import roomescape.domain.timeslot.TimeSlot;

@ExtendWith(MockitoExtension.class)
class ReservationEventListenerTest {

    @Mock
    WaitingService waitingService;

    @InjectMocks
    ReservationEventListener reservationEventListener;

    @Nested
    @DisplayName("예약 취소 이벤트 발생")
    class HandleReservationCancelled {

        @Test
        @DisplayName("예약 대기를 결제 대기로 변경한다.")
        void handleReservationCancelled() {
            // given
            TimeSlot timeSlot = CREATE_TIME_SLOT_1();
            Theme theme = CREATE_THEME_1();
            LocalDate date = LocalDate.now().plusDays(1);

            ReservationCancelledEvent event = new ReservationCancelledEvent(
                    this,
                    date,
                    timeSlot.getId(),
                    theme.getId()
            );

            // when
            reservationEventListener.handleReservationCancelled(event);

            // then
            verify(waitingService).approveNextWaiting(date, timeSlot.getId(), theme.getId());
        }
    }
}
