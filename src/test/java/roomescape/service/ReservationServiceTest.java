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
import roomescape.service.dto.request.ReservationCreateRequest;
import roomescape.service.dto.response.PersonalReservationResponse;
import roomescape.service.dto.response.ReservationResponse;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.PaymentFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationService reservationService;

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

        List<ReservationResponse> responses = reservationService.getReservationsByConditions(
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
    @DisplayName("예약을 추가한다.")
    void addReservation() {
        ReservationCreateRequest request = ReservationFixture.createValidRequest(member.getId(), time.getId(), theme.getId());

        Reservation reservation = reservationService.addReservation(request);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getDate()).isEqualTo(request.date());
            softly.assertThat(reservation.getMember().getName()).isEqualTo(member.getName());
            softly.assertThat(reservation.getTheme().getRawName()).isEqualTo(theme.getRawName());
            softly.assertThat(reservation.getTime().getStartAt()).isEqualTo(time.getStartAt());
        });
    }

    @Test
    @DisplayName("예약을 삭제하면 취소 상태가 된다.")
    void deleteReservationById() {
        Reservation reservation = reservationRepository.save(ReservationFixture.create(member, time, theme));
        long id = reservation.getId();

        Reservation canceledReservation = reservationService.cancel(id);

        assertThat(canceledReservation.isCanceled()).isTrue();
    }

    @Test
    @DisplayName("삭제한 예약을 롤백하면 다시 확정 상태가 된다.")
    void rollbackReservationById() {
        Reservation reservation = reservationRepository.save(ReservationFixture.create(member, time, theme));
        long id = reservation.getId();
        reservationService.cancel(id);

        reservationService.rollbackCancellation(reservation);

        Reservation rollbackedReservation = reservationRepository.findById(id).orElseThrow();
        assertThat(rollbackedReservation.isCanceled()).isFalse();
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

        List<PersonalReservationResponse> responses = reservationService.getReservationsByMemberId(member.getId());

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
