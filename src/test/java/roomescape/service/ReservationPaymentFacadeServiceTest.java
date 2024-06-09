package roomescape.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.service.dto.request.ReservationCreateRequest;
import roomescape.service.dto.response.ReservationResponse;
import roomescape.support.fixture.MemberFixture;
import roomescape.support.fixture.ReservationFixture;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

class ReservationPaymentFacadeServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationPaymentFacadeService reservationPaymentFacadeService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

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
    @DisplayName("예약을 생성한다.")
    void addReservation() {
        ReservationCreateRequest request = ReservationFixture.createValidRequest(member.getId(), time.getId(), theme.getId());

        ReservationResponse response = reservationPaymentFacadeService.addReservation(request);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.id()).isEqualTo(1L);
            softly.assertThat(response.date()).isEqualTo(request.date());
            softly.assertThat(response.name()).isEqualTo(member.getName());
            softly.assertThat(response.theme()).isEqualTo(theme.getRawName());
            softly.assertThat(response.startAt()).isEqualTo(time.getStartAt());
            softly.assertThat(reservationRepository.findById(1L)).isPresent();
            softly.assertThat(paymentRepository.findById(1L)).isPresent();
        });
    }

    @Test
    @DisplayName("결제 승인에서 예외가 발생하면 저장한 예약, 결제를 롤백한다")
    void addReservationFailWhenPaymentConfirmFail() {
        ReservationCreateRequest request = ReservationFixture.createInvalidRequest(member.getId(), time.getId(), theme.getId());

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThatThrownBy(() -> reservationPaymentFacadeService.addReservation(request));
            softly.assertThat(reservationRepository.count()).isZero();
            softly.assertThat(paymentRepository.count()).isZero();
        });
    }
}
