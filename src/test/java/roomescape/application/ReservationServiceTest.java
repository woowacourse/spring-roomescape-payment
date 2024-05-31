package roomescape.application;

import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.application.dto.request.reservation.ReservationPaymentRequest;
import roomescape.application.dto.request.reservation.UserReservationRequest;
import roomescape.application.dto.response.reservation.ReservationResponse;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.Role;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.Status;
import roomescape.domain.reservationdetail.ReservationDetail;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.reservationdetail.ReservationTime;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.Theme;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.fixture.CommonFixture;

class ReservationServiceTest extends BaseServiceTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationDetailRepository reservationDetailRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;

    private Member user;
    private Member admin;
    private ReservationTime time;
    private ReservationDetail detail;
    private Theme theme;

    @BeforeEach
    void setUp() {
        user = memberRepository.save(MemberFixture.user());
        admin = memberRepository.save(MemberFixture.admin());
        time = reservationTimeRepository.save(TimeFixture.createTime(LocalTime.now()));
        theme = themeRepository.save(ThemeFixture.createTheme("테마1"));
        detail = reservationDetailRepository.save(DetailFixture.createReservationDetail(
                CommonFixture.tomorrow, time, theme));
    }

    @DisplayName("예약 성공 시, 결제가 진행되고, 예약 상태로 전환된다")
    @Test
    void when_userReservationSuccess_then_changeToReservedStatus() {
        // given
        UserReservationRequest request = new UserReservationRequest(
                CommonFixture.tomorrow,
                time.getId(),
                theme.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey,
                CommonFixture.paymentType);

        // when
        ReservationResponse response = reservationService.saveReservation(request, user.getId());

        // then
        Reservation reservation = reservationRepository.getReservation(response.id());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED);
            softly.assertThat(reservation.getPayment()).isNotEmpty();
        });
    }

    @DisplayName("예약 대기 시, 결제가 진행되지 않아서 결제 정보가 없다")
    @Test
    void when_userReservationWaiting_then_noPaymentInfo() {
        // given
        UserReservationRequest adminRequest = new UserReservationRequest(
                CommonFixture.tomorrow,
                time.getId(),
                theme.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey,
                CommonFixture.paymentType);

        reservationService.saveReservation(adminRequest, admin.getId());

        UserReservationRequest memberRequest = new UserReservationRequest(
                CommonFixture.tomorrow,
                time.getId(),
                theme.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey,
                CommonFixture.paymentType);

        // when
        ReservationResponse response = reservationService.saveReservation(memberRequest, user.getId());

        // then
        Reservation reservation = reservationRepository.getReservation(response.id());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getStatus()).isEqualTo(Status.WAITING);
            softly.assertThat(reservation.getPayment()).isEmpty();
        });
    }

    @DisplayName("결제 대기 상태의 예약을 결제 시, 예약 상태로 전환된다")
    @Test
    void when_paymentForPending_then_changeToReservedStatus() {
        // given
        Reservation pendingReservation = reservationRepository.save(
                new Reservation(admin, detail, Status.PAYMENT_PENDING));

        ReservationPaymentRequest request = new ReservationPaymentRequest(
                pendingReservation.getId(),
                CommonFixture.amount,
                CommonFixture.orderId,
                CommonFixture.paymentKey);

        // when
        ReservationResponse response = reservationService.paymentForPending(request, user.getId());

        // then
        Reservation reservation = reservationRepository.getReservation(response.id());
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(reservation.getStatus()).isEqualTo(Status.RESERVED);
            softly.assertThat(reservation.getPayment()).isNotEmpty();
        });
    }

    private static class TimeFixture {
        public static ReservationTime createTime(LocalTime startAt) {
            return new ReservationTime(startAt);
        }
    }

    private static class MemberFixture {
        public static Member user() {
            return new Member("mangcho", "mangcho@woowa.net", "password", Role.NORMAL);
        }

        public static Member admin() {
            return new Member("admin", "admin@woowa.net", "password", Role.ADMIN);
        }
    }

    private static class ThemeFixture {
        public static Theme createTheme(String name) {
            return new Theme(name, "테마 설명", "https://image.com/im.jpg");
        }
    }

    private static class DetailFixture {
        public static ReservationDetail createReservationDetail(LocalDate date, ReservationTime time, Theme theme) {
            return new ReservationDetail(date, time, theme);
        }
    }
}
