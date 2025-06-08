package roomescape.reservation.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import fixture.MemberFixture;
import fixture.PaymentFixture;
import fixture.ReservationTimeFixture;
import fixture.ThemeFixture;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.global.error.exception.BadRequestException;
import roomescape.global.error.exception.ConflictException;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationAdminCreateRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationFindFilteredRequest;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.service.ReservationService;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationIntegrationTest {

    @Autowired
    private ReservationService reservationService;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @MockitoBean
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);
        given(paymentService.confirmPayment(any(), any(), any()))
                .willReturn(payment);
    }

    @Test
    @DisplayName("예약을 생성한다.")
    void createReservation() {
        // given
        var member = memberRepository.save(new Member("미소", "miso@email.com", "password", RoleType.USER));
        var theme = themeRepository.save(new Theme("테마", "설명", "썸네일"));
        var time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        var date = LocalDate.now().plusDays(1);
        var request = new ReservationCreateRequest(
                date,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail("any", "1", 100L, "any")
        );

        // when
        var response = reservationService.createReservation(member.getId(), request);

        // then
        assertAll(
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(response.startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.themeName()).isEqualTo(theme.getName())
        );
    }

    @Test
    @DisplayName("관리자가 예약을 생성한다.")
    void createReservationByAdmin() {
        // given
        var member = memberRepository.save(new Member("미소", "miso@email.com", "password", RoleType.USER));
        var theme = themeRepository.save(new Theme("테마", "설명", "썸네일"));
        var time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        var date = LocalDate.now().plusDays(1);
        var request = new ReservationAdminCreateRequest(date, theme.getId(), time.getId(), member.getId());

        // when
        var response = reservationService.createReservationByAdmin(request);

        // then
        assertAll(
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(response.startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.themeName()).isEqualTo(theme.getName())
        );
    }

    @Test
    @DisplayName("과거 날짜로 예약을 생성하면 예외가 발생한다.")
    void createReservationWithPastDate() {
        // given
        var member = memberRepository.save(new Member("미소", "miso@email.com", "password", RoleType.USER));
        var theme = themeRepository.save(new Theme("테마", "설명", "썸네일"));
        var time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        var date = LocalDate.now().minusDays(1);
        var request = new ReservationCreateRequest(
                date,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail("any", "1", 100L, "any")
        );

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(member.getId(), request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage("과거 날짜는 예약할 수 없습니다.");
    }

    @Test
    @DisplayName("중복된 시간에 예약을 생성하면 예외가 발생한다.")
    void createReservationWithDuplicateTime() {
        // given
        var member = memberRepository.save(new Member("미소", "miso@email.com", "password", RoleType.USER));
        var theme = themeRepository.save(new Theme("테마", "설명", "썸네일"));
        var time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        var date = LocalDate.now().plusDays(1);
        var request = new ReservationCreateRequest(
                date,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail("any", "1", 100L, "any")
        );
        reservationService.createReservation(member.getId(), request);

        // when & then
        assertThatThrownBy(() -> reservationService.createReservation(member.getId(), request))
                .isInstanceOf(ConflictException.class)
                .hasMessage("중복된 예약입니다.");
    }

    @Test
    @DisplayName("모든 예약을 조회한다.")
    void getAllReservations() {
        // given
        var member = memberRepository.save(new Member("미소", "miso@email.com", "password", RoleType.USER));
        var theme = themeRepository.save(new Theme("테마", "설명", "썸네일"));
        var time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        var date = LocalDate.now().plusDays(1);
        var request = new ReservationCreateRequest(
                date,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail("any", "1", 100L, "any")
        );
        reservationService.createReservation(member.getId(), request);

        // when
        var responses = reservationService.getAllReservations();

        // then
        var response = responses.getFirst();
        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(response.id()).isNotNull(),
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(response.startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.memberName()).isEqualTo(member.getName()),
                () -> assertThat(response.themeName()).isEqualTo(theme.getName())
        );
    }

    @Test
    @DisplayName("필터링된 예약을 조회한다.")
    void getFilteredReservations() {
        // given
        var member = memberRepository.save(new Member("미소", "miso@email.com", "password", RoleType.USER));
        var theme = themeRepository.save(new Theme("테마", "설명", "썸네일"));
        var time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        var date = LocalDate.now().plusDays(1);
        var request = new ReservationCreateRequest(
                date,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail("any", "1", 100L, "any")
        );
        reservationService.createReservation(member.getId(), request);

        var filterRequest = new ReservationFindFilteredRequest(
                theme.getId(),
                member.getId(),
                date,
                date
        );

        // when
        var responses = reservationService.getFilteredReservations(filterRequest);

        // then
        var response = responses.getFirst();
        assertAll(
                () -> assertThat(responses).hasSize(1),
                () -> assertThat(response.id()).isNotNull(),
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(response.startAt()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.memberName()).isEqualTo(member.getName()),
                () -> assertThat(response.themeName()).isEqualTo(theme.getName())
        );
    }

    @Test
    @DisplayName("예약을 삭제한다.")
    void deleteReservation() {
        // given
        var member = memberRepository.save(new Member("미소", "miso@email.com", "password", RoleType.USER));
        var theme = themeRepository.save(new Theme("테마", "설명", "썸네일"));
        var time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));
        var date = LocalDate.now().plusDays(1);
        var request = new ReservationCreateRequest(
                date,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail("any", "1", 100L, "any")
        );
        var response = reservationService.createReservation(member.getId(), request);

        // when
        reservationService.deleteReservation(response.id());

        // then
        var reservations = reservationService.getAllReservations();
        assertThat(reservations).isEmpty();
    }

    @Test
    @DisplayName("사용자 예약 기록 조회 - 성공")
    void getReservationsByMember() {
        // given
        Member member = MemberFixture.createDefault();
        memberRepository.save(member);

        ReservationTime time = ReservationTimeFixture.create(LocalTime.of(10, 0));
        reservationTimeRepository.save(time);

        Theme theme = ThemeFixture.createDefault();
        themeRepository.save(theme);

        LocalDate tomorrow = LocalDate.now().plusDays(1);

        var request = new ReservationCreateRequest(
                tomorrow,
                time.getId(),
                theme.getId(),
                new ReservationCreateRequest.PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        reservationService.createReservation(member.getId(), request);

        // when
        var response = reservationService.getReservationsByMember(member.getId());

        // then
        assertAll(
                () -> assertThat(response).hasSize(1),
                () -> assertThat(response.get(0).theme()).isEqualTo(theme.getName()),
                () -> assertThat(response.get(0).date()).isEqualTo(tomorrow),
                () -> assertThat(response.get(0).time()).isEqualTo(time.getStartAt()),
                () -> assertThat(response.get(0).payment().paymentKey()).isEqualTo(payment.getPaymentKey()),
                () -> assertThat(response.get(0).payment().amount()).isEqualTo(payment.getAmount())
        );
    }
}
