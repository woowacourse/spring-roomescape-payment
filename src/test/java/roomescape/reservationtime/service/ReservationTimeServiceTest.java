package roomescape.reservationtime.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservationOf;
import static roomescape.TestFixture.createTimeAt;

import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.exception.ReservationException;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.repository.ThemeRepository;

@DataJpaTest
@Import({ReservationTimeService.class, DBHelper.class})
class ReservationTimeServiceTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    ReservationTimeService reservationTimeService;

    @Autowired
    DBHelper dbHelper;

    @Test
    void 예약_시간이_정상적으로_저장된다() {
        // given
        LocalTime newTime = LocalTime.of(16, 30);
        ReservationTimeRequest request = new ReservationTimeRequest(newTime);

        // when
        ReservationTimeResponse response = reservationTimeService.saveTime(request);

        // then
        assertThat(response.id()).isNotNull();
        assertThat(response.startAt()).isEqualTo(newTime);
    }

    @Test
    void 모든_예약_시간을_조회한다() {
        // given
        dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));
        dbHelper.insertTime(createTimeAt(LocalTime.of(11, 0)));

        // when
        List<ReservationTimeResponse> responses = reservationTimeService.findAll();

        // then
        assertThat(responses).hasSize(2)
                .extracting(ReservationTimeResponse::startAt)
                .contains(
                        LocalTime.of(10, 0),
                        LocalTime.of(11, 0)
                );
    }

    @Test
    void 예약이_없는_시간은_삭제된다() {
        // given
        ReservationTime time = ReservationTime.from(LocalTime.of(14, 30));
        ReservationTime savedTime = reservationTimeRepository.save(time);

        // when
        reservationTimeService.delete(savedTime.getId());

        // then
        assertThat(reservationTimeRepository.findById(savedTime.getId())).isEmpty();
    }

    @Test
    void 예약이_있는_시간은_삭제할_수_없다() {
        // given
        ReservationTime time = dbHelper.insertTime(createTimeAt(LocalTime.of(10, 0)));

        Reservation reservation = createReservationOf(
                createDefaultMember_1(),
                DEFAULT_DATE,
                time,
                createDefaultTheme()
        );
        dbHelper.insertReservation(reservation);

        // when & then
        assertThatThrownBy(() -> reservationTimeService.delete(time.getId()))
                .isInstanceOf(ReservationException.class)
                .hasMessage("해당 시간으로 예약된 건이 존재합니다.");
    }
}
