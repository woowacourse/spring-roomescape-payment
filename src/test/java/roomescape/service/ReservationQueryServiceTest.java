package roomescape.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.service.dto.response.PersonalReservationResponse;
import roomescape.service.dto.response.ReservationResponse;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.PaymentFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

import java.time.LocalDate;
import java.util.List;

class ReservationQueryServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationQueryService reservationQueryService;
    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    private Member member;

    private Theme theme;

    private ReservationTime time;

    @BeforeEach
    void setUp() {
        member = memberRepository.save(MemberFixture.user());
        theme = themeRepository.save(ThemeFixture.theme());
        time = reservationTimeRepository.save(ReservationTimeFixture.ten());
    }

    @Test
    @DisplayName("멤버, 테마, 예약 날짜 범위로 예약들을 조회한다.")
    void getReservations() {
        reservationRepository.save(ReservationFixture.create("2024-04-09", member, time, theme));
        reservationRepository.save(ReservationFixture.create("2024-04-10", member, time, theme));
        reservationRepository.save(ReservationFixture.create("2024-04-11", member, time, theme));

        List<ReservationResponse> responses = reservationQueryService.getReservationsByConditions(
                member.getId(),
                theme.getId(),
                LocalDate.of(2024, 4, 9),
                LocalDate.of(2024, 4, 10)
        );

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(responses).hasSize(2);
            softly.assertThat(responses.get(0).date()).isEqualTo("2024-04-09");
            softly.assertThat(responses.get(1).date()).isEqualTo("2024-04-10");
        });
    }

    @Test
    @DisplayName("나의 예약들을 조회한다.")
    void getReservationsByMemberId() {
        Reservation reservation1 = reservationRepository.save(ReservationFixture.create("2024-04-09", member, time, theme));
        paymentRepository.save(PaymentFixture.create(reservation1));
        Reservation reservation2 = reservationRepository.save(ReservationFixture.create("2024-04-10", member, time, theme));
        paymentRepository.save(PaymentFixture.create(reservation2));
        Reservation reservation3 = reservationRepository.save(ReservationFixture.create("2024-04-11", member, time, theme));
        paymentRepository.save(PaymentFixture.create(reservation3));

        List<PersonalReservationResponse> responses = reservationQueryService.getMyReservations(member);

        SoftAssertions.assertSoftly(
                softly -> {
                    softly.assertThat(responses).hasSize(3);
                    softly.assertThat(responses.get(0).date()).isEqualTo("2024-04-09");
                    softly.assertThat(responses.get(1).date()).isEqualTo("2024-04-10");
                    softly.assertThat(responses.get(2).date()).isEqualTo("2024-04-11");
                }
        );
    }
}
