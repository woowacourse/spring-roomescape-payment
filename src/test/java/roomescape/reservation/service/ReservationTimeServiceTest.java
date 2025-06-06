package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import roomescape.common.exception.AlreadyInUseException;
import roomescape.common.exception.EntityNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.dto.request.ReservationTimeRequest;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@ActiveProfiles("test")
@DataJpaTest
@Import(ReservationTimeService.class)
class ReservationTimeServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeService reservationTimeService;

    @BeforeEach
    void setUp() {
        reservationTimeService = new ReservationTimeService(reservationTimeRepository, reservationRepository);
    }

    @DisplayName("모든 예약 시간을 가져온다.")
    @Test
    void getAllReservationTimes() {
        // given
        ReservationTime reservationTime1 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        ReservationTime reservationTime2 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(9, 0)));

        // when
        List<ReservationTimeResponse> result = reservationTimeService.getAll();

        // then
        assertThat(result).containsExactlyInAnyOrder(
                ReservationTimeResponse.from(reservationTime1),
                ReservationTimeResponse.from(reservationTime2)
        );
    }

    @DisplayName("정보가 없다면 빈 리스트를 반환한다.")
    @Test
    void getAllReservationTimesWhenEmpty() {
        // given & when
        List<ReservationTimeResponse> result = reservationTimeService.getAll();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("예약 시간을 생성한다.")
    @Test
    void createReservationTime() {
        // given
        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(LocalTime.of(8, 0));

        // when
        ReservationTimeResponse result = reservationTimeService.create(reservationTimeRequest);

        // then
        assertThat(result).isEqualTo(new ReservationTimeResponse(result.id(), LocalTime.of(8, 0)));

    }

    @DisplayName("예약 시간을 삭제한다.")
    @Test
    void deleteReservationTime() {
        // given
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));

        // when
        reservationTimeService.delete(reservationTime.idValue());

        // then
        assertThat(reservationTimeRepository.existsByStartAt(LocalTime.of(8, 0))).isFalse();
    }

    @DisplayName("존재하지 않는 예약 시간은 삭제할 수 없다.")
    @Test
    void deleteReservationTimeWithNonExistsTimeId() {
        assertThatThrownBy(() -> reservationTimeService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("예약 시간이 사용중이면 삭제할 수 없다.")
    @Test
    void deleteReservationTimeWhenUsing() {
        // given
        Theme theme = themeRepository.save(new Theme("테마1", "테마1", "www.m.com"));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));

        reservationRepository.save(new Reservation(member, LocalDate.now(), reservationTime, theme));
        Long timeId = reservationTime.idValue();

        // when & then
        assertThatThrownBy(() -> reservationTimeService.delete(timeId))
                .isInstanceOf(AlreadyInUseException.class);
    }
}
