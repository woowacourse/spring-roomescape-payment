package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestClient;

import roomescape.auth.dto.LoginMember;
import roomescape.common.exception.AlreadyInUseException;
import roomescape.common.exception.EntityNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.dto.MemberResponse;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Waiting;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.BookedReservationTimeResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.WaitingRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;

@ActiveProfiles("test")
@DataJpaTest
class ReservationServiceTest {

    private final LocalDateTime now = LocalDateTime.now();

    private final RestClient.Builder testBuilder = RestClient.builder()
            .baseUrl("https://api.tosspayments.com/v1/payments/confirm");

    private MockRestServiceServer server = MockRestServiceServer.bindTo(testBuilder).build();
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private WaitingRepository waitingRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    private PaymentService paymentService;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService(paymentRepository, reservationRepository, testBuilder);
        reservationService = new ReservationService(
                paymentService, reservationRepository, reservationTimeRepository,
                themeRepository, memberRepository, waitingRepository);
    }

    @DisplayName("모든 예약 정보를 가져온다.")
    @Test
    void getAllReservations() {
        // given
        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        LocalDate date = nextDay();

        reservationRepository.save(new Reservation(member, date, time, theme));

        // when
        List<ReservationResponse> response = reservationService.getAll();

        // then
        assertThat(response).hasSize(1);
    }

    @DisplayName("예약 정보가 없다면 빈 리스트를 반환한다.")
    @Test
    void getAllReservationsWhenEmpty() {
        List<ReservationResponse> result = reservationService.getAll();

        assertThat(result).isEmpty();
    }

    @DisplayName("예약을 추가한다.")
    @Test
    void createReservation() {
        // given
        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        LocalDate date = nextDay();

        ReservationCreateRequest requestDto =
                new ReservationCreateRequest(date, time.getId(), theme.getId(), LoginMember.of(member));

        // when
        ReservationResponse result = reservationService.create(requestDto);

        // then
        assertThat(result).isEqualTo(new ReservationResponse(
                result.id(),
                MemberResponse.fromEntity(member),
                date,
                ReservationTimeResponse.from(time),
                ThemeResponse.from(theme)
        ));
    }

    @DisplayName("결제 예약을 추가한다.")
    @Test
    void createReservationWithPayment() {
        // given
        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        LocalDate date = nextDay();

        ReservationCreateRequest requestDto =
                new ReservationCreateRequest(date, time.getId(), theme.getId(), LoginMember.of(member));
        PaymentRequest paymentRequest = new PaymentRequest("paymentKey", "orderId", 1_000L);

        server.expect(MockRestRequestMatchers.requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andRespond(MockRestResponseCreators.withSuccess());

        // when
        ReservationResponse result = reservationService.createWithPayment(requestDto, paymentRequest);

        // then
        assertAll(() -> {
            assertThat(result).isEqualTo(new ReservationResponse(
                    result.id(),
                    MemberResponse.fromEntity(member),
                    date,
                    ReservationTimeResponse.from(time),
                    ThemeResponse.from(theme)
            ));
            assertThat(paymentRepository.findAll()).hasSize(1);
        });
    }

    @DisplayName("해당 날짜, 시간, 테마에 예약 대기가 존재하는 상황에서 예약을 생성할 수 없다.")
    @Test
    void createReservationInWaitingExists() {
        // given
        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        LocalDate date = nextDay();

        waitingRepository.save(new Waiting(date, member, time, theme));

        ReservationCreateRequest requestDto =
                new ReservationCreateRequest(date, time.getId(), theme.getId(), LoginMember.of(member));

        // when & then
        assertThatThrownBy(() -> reservationService.create(requestDto))
                .isInstanceOf(AlreadyInUseException.class);
    }

    @DisplayName("과거 날짜에 예약을 추가할 수 없다.")
    @Test
    void createReservationWhenPastTimes() {
        // given
        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        LocalDate date = now.toLocalDate();
        ReservationTime pastTime = reservationTimeRepository.save(
                new ReservationTime(now.toLocalTime().minusMinutes(1))
        );

        ReservationCreateRequest requestDto =
                new ReservationCreateRequest(date, pastTime.getId(), theme.getId(), LoginMember.of(member));

        // when & then
        assertThatThrownBy(() -> reservationService.create(requestDto))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("존재하지 않는 예약 시간으로 예약할 수 없다.")
    @Test
    void createReservationWithNonExistsTimeId() {
        // given
        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        LocalDate date = nextDay();
        Long notExistId = 0L;
        ReservationCreateRequest requestDto =
                new ReservationCreateRequest(date, notExistId, theme.getId(), LoginMember.of(member));

        // when & then
        assertThatThrownBy(() -> reservationService.create(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("존재하지 않는 테마로 예약할 수 없다.")
    @Test
    void createReservationWithNonExistsThemeId() {
        // given
        LocalDate date = nextDay();
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        Long notExistId = 0L;
        ReservationCreateRequest requestDto =
                new ReservationCreateRequest(date, time.getId(), notExistId, LoginMember.of(member));

        // when & then
        assertThatThrownBy(() -> reservationService.create(requestDto))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("예약을 삭제한다.")
    @Test
    void deleteReservation() {
        // given
        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        LocalDate date = nextDay();
        Reservation reservation = reservationRepository.save(new Reservation(member, date, time, theme));
        Long reservationId = reservation.getId();

        // when & then
        assertThatCode(() -> reservationService.delete(reservationId))
                .doesNotThrowAnyException();
    }

    @DisplayName("예약을 삭제했을 때 예약대기가 존재하면 자동 승인한다.")
    @Test
    void deleteReservationWhenExistsWaiting() {
        // given
        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        Member member1 = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        Member member2 = memberRepository.save(new Member("로키", "test1@test.com", "12341234", Role.MEMBER));

        LocalDate date = nextDay();
        Reservation reservation1 = reservationRepository.save(new Reservation(member1, date, time, theme));
        waitingRepository.save(new Waiting(date, member2, time, theme));

        // when
        reservationService.delete(reservation1.getId());

        // then
        assertAll(() -> {
            assertThat(reservationRepository.findAll()).hasSize(1);
            assertThat(waitingRepository.findAll()).isEmpty();
        });

    }

    @DisplayName("존재하지 않는 예약은 삭제할 수 없다.")
    @Test
    void deleteReservationWithNonExistsId() {
        assertThatThrownBy(() -> reservationService.delete(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("예약 가능한 시간을 가져온다.")
    @Test
    void getAvailableReservationTimes() {
        // given
        ReservationTime reservationTime1 = reservationTimeRepository.save(new ReservationTime(LocalTime.of(8, 0)));
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(9, 0)));

        Theme theme = themeRepository.save(new Theme("포스티", "공포", "wwww.um.com"));
        Member member = memberRepository.save(new Member("포스티", "test@test.com", "12341234", Role.MEMBER));
        LocalDate date = nextDay();
        reservationRepository.save(new Reservation(member, date, reservationTime1, theme));

        // when
        List<BookedReservationTimeResponse> responses = reservationService.getSortedAvailableTimes(date, theme.getId());

        // then
        List<Boolean> alreadyBookeds = responses.stream()
                .map(BookedReservationTimeResponse::alreadyBooked)
                .toList();
        assertThat(alreadyBookeds).containsExactlyInAnyOrder(true, false);
    }

    private LocalDate nextDay() {
        return LocalDate.now().plusDays(1);
    }
}
