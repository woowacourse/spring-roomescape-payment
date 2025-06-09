package roomescape.unit.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.auth.Role;
import roomescape.domain.Member;
import roomescape.domain.Payment;
import roomescape.domain.PaymentInfo;
import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.repository.MemberRepository;
import roomescape.domain.repository.PaymentRepository;
import roomescape.domain.repository.ReservationRepository;
import roomescape.domain.repository.ReservationTimeRepository;
import roomescape.domain.repository.ThemeRepository;
import roomescape.domain.repository.WaitingRepository;
import roomescape.dto.PaymentRequest;
import roomescape.dto.request.ReservationCondition;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.response.MyReservationsResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationTimeResponse;
import roomescape.dto.response.ReservationWithPaymentResponse;
import roomescape.exception.ExistedReservationException;
import roomescape.exception.ReservationNotFoundException;
import roomescape.service.PaymentClient;
import roomescape.service.ReservationService;
import roomescape.unit.fake.FakeMemberRepository;
import roomescape.unit.fake.FakePaymentRepository;
import roomescape.unit.fake.FakeReservationRepository;
import roomescape.unit.fake.FakeReservationTimeRepository;
import roomescape.unit.fake.FakeThemeRepository;
import roomescape.unit.fake.FakeWaitingRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ReservationServiceTest.TestConfig.class)
class ReservationServiceTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationService reservationService;

    @MockitoBean
    private PaymentClient paymentClient;

    @BeforeEach
    void setUp() {
        ((FakeReservationRepository) reservationRepository).clear();
        ((FakeReservationTimeRepository) reservationTimeRepository).clear();
        ((FakeThemeRepository) themeRepository).clear();
        ((FakeMemberRepository) memberRepository).clear();
    }

    @Test
    void 사용자가_예약을_생성한다() {
        // given
        ReservationTime reservationTime = new ReservationTime(null, LocalTime.of(10, 0));
        ReservationTime savedReservationTime = reservationTimeRepository.save(reservationTime);

        Theme theme = new Theme(null, "themeName1", "des", "th");
        Theme savedTheme = themeRepository.save(theme);

        Member member = memberRepository.save(new Member(null, "포라", "email1@domain.com", "password1", Role.MEMBER));

        ReservationCreateRequest request = new ReservationCreateRequest(
                LocalDate.now().plusDays(1), savedReservationTime.getId(), savedTheme.getId(), "paymentKey", "normal", "1", 1000);

        PaymentInfo paymentInfo = new PaymentInfo("paymentKey", 1000, "order_id");
        given(paymentClient.postPaymentInfo(any())).willReturn(paymentInfo);

        ReservationWithPaymentResponse actualResponse = reservationService.createReservationForMember(member.getId(), request);

        ReservationWithPaymentResponse response = new ReservationWithPaymentResponse(actualResponse.id(), member.getName(), request.date(),
                new ReservationTimeResponse(savedReservationTime.getId(), reservationTime.getStartAt()), theme.getName(), "paymentKey", 1000);

        // when & then
        assertThat(response).isEqualTo(actualResponse);
    }

    @Test
    void 예약을_조회할_수_있다() {
        // given
        ReservationTime reservationTime1 = new ReservationTime(null, LocalTime.of(10, 0));
        ReservationTime savedReservationTime1 = reservationTimeRepository.save(reservationTime1);

        Theme theme1 = new Theme(null, "themeName1", "des", "th");
        Theme savedTheme1 = themeRepository.save(theme1);

        Member savedMember1 = memberRepository.save(new Member(null, "포라", "email1@domain.com", "password1", Role.MEMBER));
        Member savedMember2 = memberRepository.save(new Member(null, "아마", "email2@domain.com", "password2", Role.MEMBER));
        Reservation reservation1 = Reservation.of(null, savedMember1, LocalDate.of(2025, 7, 25),
                savedReservationTime1, savedTheme1);
        reservationRepository.save(reservation1);

        Reservation reservation2 = Reservation.of(null, savedMember2, LocalDate.of(2025, 7, 26),
                savedReservationTime1, savedTheme1);
        reservationRepository.save(reservation2);
        // when
        List<ReservationResponse> all = reservationService.findReservations(
                new ReservationCondition(
                        savedTheme1.getId(),
                        savedMember1.getId(),
                        LocalDate.of(2025, 7, 2),
                        LocalDate.of(2025, 7, 25)
                )
        );

        // then
        assertThat(all.size()).isEqualTo(1);
        assertThat(all.get(0).memberName()).isEqualTo("포라");
    }

    @Test
    void 샤용자가_예약을_조회할_수_있다() {
        // given
        ReservationTime reservationTime1 = new ReservationTime(1L, LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime1);

        Theme theme1 = new Theme(1L, "themeName1", "des", "th");
        themeRepository.save(theme1);

        Member member1 = new Member(1L, "포라", "email1@domain.com", "password1", Role.MEMBER);
        memberRepository.save(member1);

        Reservation reservation1 = Reservation.of(null, member1, LocalDate.of(2025, 7, 25),
                reservationTime1, theme1);

        Reservation savedReservation = reservationRepository.save(reservation1);

        Payment payment = new Payment(
                new PaymentRequest("paymentKey", 1000, "orderId"),
                savedReservation
        );

        paymentRepository.save(payment);

        // when
        List<MyReservationsResponse> memberReservations = reservationService.findBookingHistory(1L);

        // then
        assertThat(memberReservations.size()).isEqualTo(1);
        assertThat(memberReservations.get(0).memberName()).isEqualTo("포라");
    }

    @Test
    void 예약을_추가할_수_있다() {
        // given
        ReservationTime reservationTime1 = new ReservationTime(1L, LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime1);
        Theme theme1 = new Theme(1L, "themeName1", "des", "th");
        themeRepository.save(theme1);
        Member member1 = new Member(1L, "name1", "email1@domain.com", "password1", Role.MEMBER);
        memberRepository.save(member1);
        Reservation reservation1 = Reservation.of(null, member1, LocalDate.of(2025, 7, 25),
                reservationTime1, theme1);
        // when
        reservationRepository.save(reservation1);

        // then
        List<ReservationResponse> all = reservationService.findReservations(
                new ReservationCondition(
                        theme1.getId(),
                        member1.getId(),
                        LocalDate.of(2025, 7, 25),
                        LocalDate.of(2025, 7, 25)
                )
        );
        assertThat(all.size()).isEqualTo(1);
        assertThat(all.getLast().memberName()).isEqualTo("name1");
    }

    @Test
    void 예약을_삭제할_수_있다() {
        // given
        ReservationTime reservationTime1 = new ReservationTime(1L, LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime1);
        Theme theme1 = new Theme(1L, "themeName1", "des", "th");
        themeRepository.save(theme1);
        Member member1 = new Member(1L, "name1", "email1@domain.com", "password1", Role.MEMBER);
        memberRepository.save(member1);
        Reservation reservation1 = Reservation.of(null, member1, LocalDate.of(2025, 7, 25),
                reservationTime1, theme1);
        Reservation savedReservation = reservationRepository.save(reservation1);
        // when
        reservationService.deleteReservationById(savedReservation.getId());

        // then
        List<ReservationResponse> all = reservationService.findReservations(
                new ReservationCondition(null, null, null, null));
        assertThat(all.size()).isEqualTo(0);
    }

    @Test
    void id에_대한_예약이_없을_경우_예외가_발생한다() {
        // when & then
        Assertions.assertThatThrownBy(() -> reservationService.deleteReservationById(10L))
                .isInstanceOf(ReservationNotFoundException.class);
    }

    @Test
    void 중복_예약하면_예외가_발생한다() {
        // given
        ReservationTime savedTime = reservationTimeRepository.save(
                new ReservationTime(null, LocalTime.of(10, 0)));

        Theme savedTheme = themeRepository.save(new Theme(null, "themeName1", "des", "th"));

        Member savedMember = memberRepository.save(
                new Member(null, "name1", "email1@domain.com", "password1", Role.MEMBER));

        Reservation reservation1 = Reservation.of(null, savedMember, LocalDate.of(2025, 7, 25),
                savedTime, savedTheme);
        reservationRepository.save(reservation1);

        // when & then
        assertThatThrownBy(
                () -> reservationService.createReservation(savedMember.getId(), savedTime.getId(), savedTheme.getId(),
                        LocalDate.of(2025, 7, 25)))
                .isInstanceOf(ExistedReservationException.class);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ReservationRepository reservationRepository() {
            return new FakeReservationRepository();
        }

        @Bean
        public ReservationTimeRepository reservationTimeRepository() {
            return new FakeReservationTimeRepository(reservationRepository());
        }

        @Bean
        public ThemeRepository themeRepository() {
            return new FakeThemeRepository();
        }

        @Bean
        public WaitingRepository waitingRepository() {
            return new FakeWaitingRepository();
        }

        @Bean
        public MemberRepository memberRepository() {
            return new FakeMemberRepository();
        }

        @Bean
        public PaymentRepository paymentRepository() {
            return new FakePaymentRepository();
        }

        @Bean
        public ReservationService reservationService(PaymentClient paymentClient) {
            return new ReservationService(
                    reservationRepository(),
                    reservationTimeRepository(),
                    themeRepository(),
                    memberRepository(),
                    waitingRepository(),
                    paymentRepository(),
                    paymentClient
            );
        }
    }
}
