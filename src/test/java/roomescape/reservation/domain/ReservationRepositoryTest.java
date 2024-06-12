package roomescape.reservation.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.fixture.MemberFixture;
import roomescape.fixture.PaymentFixture;
import roomescape.fixture.ReservationSlotFixture;
import roomescape.fixture.ReservationTimeFixture;
import roomescape.fixture.ThemeFixture;
import roomescape.reservation.domain.repository.PaymentRepository;
import roomescape.reservation.domain.repository.ReservationRepository;
import roomescape.reservation.domain.repository.ReservationSlotRepository;

@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    ReservationSlotRepository slotRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    PaymentRepository paymentRepository;


    @DisplayName("성공 : 예약을 등록한다.")
    @Test
    void addReservation() {
        //given
        ReservationSlot nextDayReservationSlot = ReservationSlotFixture.getTestYearReservationSlot(
                ReservationTimeFixture.get2PM(), ThemeFixture.getTheme2());

        //when
        ReservationSlot reservationSlot = slotRepository.save(nextDayReservationSlot);
        Reservation beforeSaveReservation = new Reservation(MemberFixture.getMemberTacan(), reservationSlot,
                PaymentFixture.getPaymentWithId());
        Reservation savedReservation = reservationRepository.save(beforeSaveReservation);

        //then
        assertThat(savedReservation.getId()).isNotNull();
    }

    @DisplayName("성공 : 예약 대기 순서를 조회한다.")
    @Test
    void addWaitingReservation() {
        //given
        ReservationSlot reservedSlot = ReservationSlotFixture.getReservationSlot1();
        Reservation reservation = new Reservation(MemberFixture.getMemberTacan(), reservedSlot,
                PaymentFixture.getPaymentWithId());

        //when
        Reservation savedReservation = reservationRepository.save(reservation);
        List<Reservation> all = reservationRepository.findAll();
        int waitingReservationCount = reservationRepository.findMyWaitingOrder(savedReservation.getId());

        //then
        assertThat(waitingReservationCount).isEqualTo(2);
    }
}
