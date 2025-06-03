package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.DEFAULT_DATE;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createReservation_1;
import static roomescape.TestFixture.createReservation_2;
import static roomescape.TestFixture.createTimeAt_10;
import static roomescape.constant.TestData.RESERVATION_COUNT;

import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import roomescape.DBHelper;
import roomescape.auth.dto.LoginMember;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchRequest;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@DataJpaTest
@Import({ReservationService.class, WaitingReservationService.class, DBHelper.class})
class ReservationServiceTest {

    @Autowired
    private DBHelper dbHelper;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService service;

    @Test
    void 모든_예약을_조회한다() {
        // given
        dbHelper.insertReservation(createReservation_1());
        dbHelper.insertReservation(createReservation_2());

        // when
        List<ReservationResponse> responses = service.findReservationsByCriteria(
                new ReservationSearchRequest(null, null, null, null));

        // then
        assertThat(responses).hasSize(RESERVATION_COUNT)
                .extracting(ReservationResponse::id)
                .doesNotContain(0L);
    }

//    @Test
//    void 지나간_날짜와_시간이면_예외가_발생한다() {
//        // given
//        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
//        Theme theme = dbHelper.insertTheme(createDefaultTheme());
//        LocalDate pastDate = LocalDate.now().minusDays(1);
//        ReservationRequest request = new ReservationRequest(pastDate, time.getId(), theme.getId());
//
//        Member member = dbHelper.insertMember(createDefaultMember_1());
//        LoginMember loginMember = LoginMember.from(member);
//
//        // when & then
//        assertThatThrownBy(() -> service.resisterReservation(request, loginMember))
//                .isInstanceOf(ReservationException.class)
//                .hasMessage("예약은 현재 시간 이후로 가능합니다.");
//    }

    @Test
    void 새로운_예약은_정상_생성된다() {
        // given
        ReservationTime time = dbHelper.insertTime(createTimeAt_10());
        Theme theme = dbHelper.insertTheme(createDefaultTheme());
        ReservationRequest request = new ReservationRequest(DEFAULT_DATE, time.getId(), theme.getId());

        Member member = dbHelper.insertMember(createDefaultMember_1());
        LoginMember loginMember = LoginMember.from(member);

        // when
        ReservationResponse result = service.resisterReservation(request, loginMember);

        // then
        SoftAssertions.assertSoftly(soft -> {
            assertThat(reservationRepository.findAll()).hasSize(1);
            assertThat(result.date()).isEqualTo(DEFAULT_DATE);
            assertThat(result.time().startAt()).isEqualTo(time.getStartAt());
            assertThat(result.theme().name()).isEqualTo(theme.getName());
        });
    }

    @Test
    void 예약을_삭제한다() {
        // given
        Reservation reservation = dbHelper.insertReservation(createReservation_1());
        assertThat(reservationRepository.findAll()).hasSize(1);

        // when
        service.deleteById(reservation.getId());

        // then
        assertThat(reservationRepository.findAll()).hasSize(0);
    }
}
