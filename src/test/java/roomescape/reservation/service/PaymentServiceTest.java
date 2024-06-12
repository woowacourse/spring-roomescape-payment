package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import static roomescape.member.domain.Role.USER;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;

import roomescape.client.PaymentException;
import roomescape.config.ClientConfig;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.repository.PaymentRepository;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@Import(value = ClientConfig.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PaymentServiceTest {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private DatabaseCleaner databaseCleaner;

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanUp();
    }

    @Test
    @DisplayName("예약을 생성한다.")
    void saveReservationWhenAccountIsCompleted() {
        Theme theme = themeRepository.save(new Theme("t", "d", "t"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(1, 0)));
        Member member = memberRepository.save(new Member("n", "e", "p"));
        ReservationCreateRequest request
                = new ReservationCreateRequest(LocalDate.now().plusDays(1), theme.getId(), time.getId(), "paymentKey",
                "orderId", 1000L, "paymentType");
        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(member.getId(), member.getRole(),
                member.getName(), member.getEmail());

        ReservationResponse response = paymentService.purchase(request, loginMemberInToken);

        assertThat(reservationRepository.findById(response.id())).isPresent();
    }

    @Test
    @DisplayName(" 지난 날짜에 대한 예약 시 예외를 발생 시킨다.")
    void saveShouldThrowExceptionWhenReservationDateIsExpire() {
        Theme theme = themeRepository.save(new Theme("t", "d", "t"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(1, 0)));
        Member member = memberRepository.save(new Member("n", "e", "p"));

        ReservationCreateRequest request
                = new ReservationCreateRequest(LocalDate.now().minusDays(1), theme.getId(), time.getId(), "payment-key",
                "order-id", 1000L, "type");
        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(member.getId(), member.getRole(),
                member.getName(), member.getEmail());

        assertThatThrownBy(() -> paymentService.purchase(request, loginMemberInToken))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간에 예약을 하면 예외가 발생한다.")
    void notExistReservationTimeIdExceptionTest() {
        Theme theme = new Theme("공포", "호러 방탈출", "http://asdf.jpg");
        themeRepository.save(theme);

        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(1L, USER, "카키", "kaki@email.com");
        ReservationCreateRequest request
                = new ReservationCreateRequest(LocalDate.now().plusDays(1), theme.getId(), 1L, "payment-key",
                "order-id", 1000L, "type");

        assertThatThrownBy(() -> paymentService.purchase(request, loginMemberInToken))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("예약 생성 중 결제에 실패하면 예외를 발생시킨다.")
    void saveShouldThrowExceptionWhenAccountFailed() {
        Theme theme = themeRepository.save(new Theme("t", "d", "t"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(1, 0)));
        Member member = memberRepository.save(new Member("n", "e", "p"));
        ReservationCreateRequest request
                = new ReservationCreateRequest(LocalDate.now().plusDays(1), theme.getId(), time.getId(), "payment-key",
                "order-id", -1000L, "type");

        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(member.getId(), member.getRole(),
                member.getName(), member.getEmail());

        assertThatThrownBy(() -> paymentService.purchase(request, loginMemberInToken))
                .isInstanceOf(PaymentException.class);

    }
}
