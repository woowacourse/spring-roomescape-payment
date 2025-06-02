package roomescape.test_util;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestComponent;
import roomescape.repository.jpa.MemberJpaRepository;
import roomescape.repository.jpa.PaymentJpaRepository;
import roomescape.repository.jpa.ReservationItemJpaRepository;
import roomescape.repository.jpa.ReservationJpaRepository;
import roomescape.repository.jpa.ReservationThemeJpaRepository;
import roomescape.repository.jpa.ReservationTimeJpaRepository;

@RequiredArgsConstructor
@TestComponent
public class DataCleaner {

    private final ReservationJpaRepository reservationJpaRepository;
    private final ReservationItemJpaRepository reservationItemJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    private final ReservationTimeJpaRepository reservationTimeJpaRepository;
    private final ReservationThemeJpaRepository reservationThemeJpaRepository;
    private final PaymentJpaRepository paymentJpaRepository;

    public void clean() {
        paymentJpaRepository.deleteAll();
        reservationJpaRepository.deleteAll();
        reservationItemJpaRepository.deleteAll();
        memberJpaRepository.deleteAll();
        reservationTimeJpaRepository.deleteAll();
        reservationThemeJpaRepository.deleteAll();
    }
}
