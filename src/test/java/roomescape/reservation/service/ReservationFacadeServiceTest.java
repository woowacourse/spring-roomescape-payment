package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import roomescape.config.TestConfig;
import roomescape.global.auth.dto.UserInfo;
import roomescape.global.auth.service.MyPasswordEncoder;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.member.repository.MemberRepository;
import roomescape.member.service.MemberService;
import roomescape.payment.infrastructure.TossApiClient;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.exception.ReservationAlreadyExistsException;
import roomescape.reservation.fixture.TestFixture;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.WaitingRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.exception.ReservationTimeNotFoundException;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.reservationtime.service.ReservationTimeService;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.theme.service.ThemeService;

@DataJpaTest
@Import(TestConfig.class)
@TestPropertySource(properties = {
        "spring.sql.init.mode=never"
})
class ReservationFacadeServiceTest {

    private static final LocalDate futureDate = TestFixture.makeFutureDate();

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private WaitingRepository waitingRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Mock
    private TossApiClient tossApiClient;

    private ReservationFacadeService reservationFacadeService;
    private ReservationTime time;
    private Theme theme;
    private Member member;

    @BeforeEach
    void setUp() {
        reservationFacadeService = new ReservationFacadeService(
                new ReservationService(reservationRepository),
                new WaitingService(waitingRepository),
                new MemberService(memberRepository, new MyPasswordEncoder()),
                new ThemeService(themeRepository, reservationRepository),
                new ReservationTimeService(reservationTimeRepository, reservationRepository),
                new PaymentService(tossApiClient)
        );

        ReservationTime time2 = ReservationTime.withUnassignedId(LocalTime.of(9, 0));
        time = reservationTimeRepository.save(time2);
        theme = themeRepository.save(TestFixture.makeTheme(1L));
        member = memberRepository.save(TestFixture.makeMember());
    }

    @Test
    void createForAdmin_shouldCreateReservation() {
        ReservationResponse response = reservationFacadeService.createForAdmin(
                new ReservationRequest(futureDate, time.getId(), theme.getId()),
                member.getId()
        );

        assertAll(
                () -> assertThat(response.member().name()).isEqualTo("Mint"),
                () -> assertThat(response.date()).isEqualTo(futureDate),
                () -> assertThat(response.time().startAt()).isEqualTo(LocalTime.of(9, 0)),
                () -> assertThat(response.reservedStatus()).isEqualTo(ReservationStatus.RESERVED.getName())
        );
    }

    @Test
    void createForAdmin_shouldThrowException_whenTimeNotFound() {
        assertThatThrownBy(() -> reservationFacadeService.createForAdmin(
                new ReservationRequest(futureDate, 999L, theme.getId()),
                member.getId()))
                .isInstanceOf(ReservationTimeNotFoundException.class)
                .hasMessageContaining("요청한 id와 일치하는 예약 시간 정보가 없습니다.");
    }

    @Test
    void createWaiting_shouldCreateWaiting() {
        ReservationResponse response = reservationFacadeService.createWaiting(
                new ReservationRequest(futureDate, time.getId(), theme.getId()),
                member.getId()
        );

        assertAll(
                () -> assertThat(response.member().name()).isEqualTo("Mint"),
                () -> assertThat(response.date()).isEqualTo(futureDate),
                () -> assertThat(response.time().startAt()).isEqualTo(LocalTime.of(9, 0)),
                () -> assertThat(response.reservedStatus()).isEqualTo(ReservationStatus.WAITING.getName())
        );
    }

    @Test
    void create_shouldThrowException_whenReservationExists() {
        reservationFacadeService.createForAdmin(
                new ReservationRequest(futureDate, time.getId(), theme.getId()),
                member.getId()
        );

        assertThatThrownBy(() -> reservationFacadeService.create(
                new ReservationCreateRequest(
                        new ReservationRequest(futureDate, time.getId(), theme.getId()),
                        new PaymentRequest(
                                "test_payment_key",
                                "test_order_id",
                                50000,
                                "CARD"
                        )),
                member.getId()))
                .isInstanceOf(ReservationAlreadyExistsException.class)
                .hasMessageContaining("이미 예약이 존재합니다.");
    }

    @Test
    void findMyReservations_shouldReturnAllMemberReservations() {
        reservationFacadeService.createForAdmin(
                new ReservationRequest(futureDate, time.getId(), theme.getId()),
                member.getId()
        );
        reservationFacadeService.createWaiting(
                new ReservationRequest(futureDate, time.getId(), theme.getId()),
                member.getId()
        );

        List<MyReservationResponse> result = reservationFacadeService.findMyReservations(
                new UserInfo(member.getId(), MemberRole.USER)
        );

        assertAll(
                () -> assertThat(result).hasSize(2),
                () -> assertThat(result.get(0).reservedStatus()).isEqualTo(ReservationStatus.RESERVED.getName()),
                () -> assertThat(result.get(1).reservedStatus()).isEqualTo("1번째 " + ReservationStatus.WAITING.getName())
        );
    }

    @Test
    void deleteReservation_shouldPromoteFirstWaiting() {
        ReservationResponse reserved = reservationFacadeService.createForAdmin(
                new ReservationRequest(futureDate, time.getId(), theme.getId()),
                member.getId()
        );
        ReservationResponse waiting = reservationFacadeService.createWaiting(
                new ReservationRequest(futureDate, time.getId(), theme.getId()),
                member.getId()
        );

        reservationFacadeService.deleteReservation(reserved.id());

        List<MyReservationResponse> result = reservationFacadeService.findMyReservations(
                new UserInfo(member.getId(), MemberRole.USER)
        );

        assertAll(
                () -> assertThat(result).hasSize(1),
                () -> assertThat(result.get(0).reservedStatus()).isEqualTo(ReservationStatus.RESERVED.getName())
        );
    }

    @Test
    void deleteReservation_shouldNotPromote_whenNoWaitingExists() {
        ReservationResponse reserved = reservationFacadeService.createForAdmin(
                new ReservationRequest(futureDate, time.getId(), theme.getId()),
                member.getId()
        );

        reservationFacadeService.deleteReservation(reserved.id());

        List<MyReservationResponse> result = reservationFacadeService.findMyReservations(
                new UserInfo(member.getId(), MemberRole.USER)
        );

        assertThat(result).isEmpty();
    }
} 