package roomescape.application;

import java.time.LocalDate;
import java.time.LocalTime;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.application.dto.request.member.MemberInfo;
import roomescape.domain.event.CancelEventPublisher;
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

class CancelServiceTest extends BaseServiceTest {

    @Autowired
    private CancelService cancelService;
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

    @MockBean
    private CancelEventPublisher eventPublisher;

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

    @DisplayName("예약 취소 시, 예약 상태가 취소로 변경된다")
    @Test
    void when_cancelReservation_then_changedToCancelStatus() {
        // given
        Reservation reservation = reservationRepository.save(new Reservation(user, detail, Status.RESERVED));
        MemberInfo memberInfo = new MemberInfo(user.getId());

        // when
        cancelService.cancelReservation(reservation.getId(), memberInfo);

        // then
        Reservation canceledReservation = reservationRepository.getReservation(reservation.getId());
        Assertions.assertThat(canceledReservation.getStatus()).isEqualTo(Status.CANCELED);
    }

    @DisplayName("예약 취소 시, 다음 예약 대기가 결제 대기 상태로 전환된다")
    @Test
    void when_cancelReservation_then_nextWaitingReservationChangedToWaitingStatus() {
        // given
        MemberInfo memberInfo = new MemberInfo(admin.getId());
        Reservation reservation = reservationRepository.save(new Reservation(admin, detail, Status.RESERVED));
        Reservation nextReservation = reservationRepository.save(new Reservation(user, detail, Status.WAITING));

        // when
        cancelService.cancelReservation(reservation.getId(), memberInfo);

        // then
        Reservation waitingReservation = reservationRepository.getReservation(nextReservation.getId());
        Assertions.assertThat(waitingReservation.getStatus()).isEqualTo(Status.PAYMENT_PENDING);
    }

    @DisplayName("예약 취소 시, 이벤트가 발행된다")
    @Test
    void when_cancelReservation_then_eventPublished() {
        // given
        MemberInfo memberInfo = new MemberInfo(admin.getId());
        Reservation reservation = reservationRepository.save(new Reservation(admin, detail, Status.RESERVED));
        Reservation nextReservation = reservationRepository.save(new Reservation(user, detail, Status.WAITING));

        // when
        cancelService.cancelReservation(reservation.getId(), memberInfo);

        // then
        Mockito.verify(eventPublisher, Mockito.times(1))
                .publishPaymentPendingEvent(Mockito.any());
    }

    @DisplayName("예약 취소 시, 다음 예약 대기가 없으면 아무 일도 일어나지 않는다")
    @Test
    void when_cancelReservation_then_nothingHappened() {
        // given
        MemberInfo memberInfo = new MemberInfo(admin.getId());
        Reservation reservation = reservationRepository.save(new Reservation(admin, detail, Status.RESERVED));

        // when
        cancelService.cancelReservation(reservation.getId(), memberInfo);

        // then
        Mockito.verify(eventPublisher, Mockito.times(0))
                .publishPaymentPendingEvent(Mockito.any());
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
