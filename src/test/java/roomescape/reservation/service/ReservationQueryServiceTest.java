package roomescape.reservation.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import roomescape.common.exception.BadRequestException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.service.dto.request.FilteringReservationRequest;
import roomescape.reservation.service.dto.response.ReservationResponse;
import roomescape.reservation.service.dto.response.ReservationTimeWithBookedResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@DataJpaTest
@Import(ReservationQueryService.class)
class ReservationQueryServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationQueryService reservationQueryService;

    @DisplayName("모든 예약 정보를 가져온다")
    @Test
    void test1() {
        // given
        Theme savedTheme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));

        LocalTime time = LocalTime.of(8, 0);
        ReservationTime savedTime = reservationTimeRepository.save(new ReservationTime(time));
        Member member = new Member("포스티", "test@test.com", "12341234", Role.MEMBER);
        Member savedMember = memberRepository.save(member);

        LocalDate date = LocalDate.of(2024, 4, 29);
        reservationRepository.save(new Reservation(savedMember, date, savedTime, savedTheme));

        // when
        List<ReservationResponse> response = reservationQueryService.getAll();

        // then
        assertThat(response).hasSize(1);
    }

    @DisplayName("예약 정보가 없다면 빈 리스트를 반환한다.")
    @Test
    void test2() {
        List<ReservationResponse> result = reservationQueryService.getAll();

        assertThat(result).isEmpty();
    }

    @DisplayName("가능한 시간 대에 대하여 반환한다.")
    @Test
    void test9() {
        // given
        LocalDate date = nextDay();
        LocalTime time1 = LocalTime.of(8, 0);
        LocalTime time2 = LocalTime.of(9, 0);
        ReservationTime reservationTime1 = reservationTimeRepository.save(new ReservationTime(time1));
        ReservationTime reservationTime2 = reservationTimeRepository.save(new ReservationTime(time2));
        Theme savedTheme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        Long themeId = savedTheme.getId();
        Member member = new Member("포스티", "test@test.com", "12341234", Role.MEMBER);
        Member savedMember = memberRepository.save(member);
        reservationRepository.save(new Reservation(savedMember, date, reservationTime1, savedTheme));

        // when
        List<ReservationTimeWithBookedResponse> responses = reservationQueryService.getReservationTimesWithBooked(date, themeId);

        // then
        List<Boolean> booleans = responses.stream()
                .map(ReservationTimeWithBookedResponse::alreadyBooked)
                .toList();
        assertThat(booleans).containsExactlyInAnyOrder(true, false);
    }

    @DisplayName("종료 날짜가 시작 날짜보다 앞서는 경우 예외가 발생한다.")
    @Test
    void cannotDateToIsBeforeThanDateFrom() {
        // given
        LocalDate from = LocalDate.of(2025, 5, 31);
        LocalDate to = from.minusDays(1);

        FilteringReservationRequest request = new FilteringReservationRequest(1L, 1L, from, to);

        // when & then
        assertThatThrownBy(() -> {
            reservationQueryService.findReservationByFiltering(request);
        }).isInstanceOf(BadRequestException.class);
    }

    private LocalDate nextDay() {
        return LocalDate.now().plusDays(1);
    }
}
