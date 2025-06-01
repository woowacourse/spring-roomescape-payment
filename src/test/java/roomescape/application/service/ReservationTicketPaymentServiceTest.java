package roomescape.application.service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.application.facade.ReservationTicketPaymentService;
import roomescape.common.exception.DuplicatedException;
import roomescape.common.exception.PaymentClientException;
import roomescape.dto.LoginMember;
import roomescape.dto.request.ReservationTicketPaymentWithTossRequestDto;
import roomescape.dto.request.ReservationTicketRegisterDto;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.request.TossPaymentRequestDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;
import roomescape.infrastructure.db.MemberJpaRepository;
import roomescape.infrastructure.db.ThemeJpaRepository;
import roomescape.infrastructure.db.TossPaymentJpaRepository;
import roomescape.infrastructure.payment.toss.TossPaymentWithRestClient;
import roomescape.model.Member;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Role;
import roomescape.model.Theme;
import roomescape.model.TossPayment;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ReservationTicketPaymentServiceTest {

    @Autowired
    ReservationTicketPaymentService reservationTicketPaymentService;

    @MockitoBean
    TossPaymentWithRestClient tossPaymentWithRestClient;

    @Autowired
    TossPaymentJpaRepository tossPaymentJpaRepository;

    @Autowired
    ReservationTicketRepository reservationTicketRepository;

    @Autowired
    ReservationTimeRepository reservationTimeRepository;

    @Autowired
    ThemeJpaRepository themeJpaRepository;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @DisplayName("예약을 정상적으로 저장한다.")
    @Test
    void test1() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now().plusDays(1);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
                member.getEmail(), member.getRole());
        ReservationTicketRegisterDto request = new ReservationTicketRegisterDto(date.toString(),
                time.getId(), theme.getId());
        TossPaymentRequestDto tossPaymentRequestDto = new TossPaymentRequestDto(
                "paymentKey",
                "orderId",
                1000L
        );

        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = new TossPaymentConfirmResponseDto(
                "DONE", "paymentKey", "orderId"
        );

        when(tossPaymentWithRestClient.requestConfirmation(any(TossPaymentConfirmDto.class)))
                .thenReturn(tossPaymentConfirmResponseDto);

        // when
        ReservationTicketResponseDto response = reservationTicketPaymentService.saveReservationWithTossPaymentGateWay(
                new ReservationTicketPaymentWithTossRequestDto(request, tossPaymentRequestDto), loginMember);

        // then
        List<TossPayment> tossPayments = tossPaymentJpaRepository.findAll();

        assertAll(() -> assertThat(response.member().name()).isEqualTo(member.getName()),
                () -> assertThat(response.date()).isEqualTo(date),
                () -> assertThat(tossPayments).hasSize(1));
    }

    @DisplayName("결제 승인 결과가 DONE 이 아니면 예외를 던진다")
    @Test
    void test2() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now().plusDays(1);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
                member.getEmail(), member.getRole());
        ReservationTicketRegisterDto request = new ReservationTicketRegisterDto(date.toString(),
                time.getId(), theme.getId());
        TossPaymentRequestDto tossPaymentRequestDto = new TossPaymentRequestDto(
                "paymentKey",
                "orderId",
                1000L
        );

        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = new TossPaymentConfirmResponseDto(
                "ABORTED", "paymentKey", "orderId"
        );

        when(tossPaymentWithRestClient.requestConfirmation(any(TossPaymentConfirmDto.class)))
                .thenReturn(tossPaymentConfirmResponseDto);

        // when & then
        assertThatThrownBy(
                () -> reservationTicketPaymentService.saveReservationWithTossPaymentGateWay(
                        new ReservationTicketPaymentWithTossRequestDto(request, tossPaymentRequestDto),
                        loginMember)).isInstanceOf(
                PaymentClientException.class);
    }

    @DisplayName("이미 존재하는 예약 시간에 예약한다면 예외를 던진다")
    @Test
    void test4() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now().plusDays(1);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
                member.getEmail(), member.getRole());
        ReservationTicketRegisterDto request = new ReservationTicketRegisterDto(date.toString(),
                time.getId(), theme.getId());
        TossPaymentRequestDto tossPaymentRequestDto = new TossPaymentRequestDto(
                "paymentKey",
                "orderId",
                1000L
        );

        Reservation reservation = new Reservation(
                date,
                time,
                theme,
                member,
                LocalDate.now()
        );
        reservationTicketRepository.save(new ReservationTicket(reservation));

        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = new TossPaymentConfirmResponseDto(
                "DONE", "paymentKey", "orderId"
        );

        when(tossPaymentWithRestClient.requestConfirmation(any(TossPaymentConfirmDto.class)))
                .thenReturn(tossPaymentConfirmResponseDto);

        // when && then
        assertThatThrownBy(
                () -> reservationTicketPaymentService.saveReservationWithTossPaymentGateWay(
                        new ReservationTicketPaymentWithTossRequestDto(request, tossPaymentRequestDto), loginMember))
                .isInstanceOf(DuplicatedException.class);
    }

    @DisplayName("당일 예약을 한다면 예외를 던진다")
    @Test
    void test5() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme(1L);
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now();

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
                member.getEmail(), member.getRole());
        ReservationTicketRegisterDto request = new ReservationTicketRegisterDto(date.toString(),
                time.getId(), theme.getId());
        TossPaymentRequestDto tossPaymentRequestDto = new TossPaymentRequestDto(
                "paymentKey",
                "orderId",
                1000L
        );

        TossPaymentConfirmResponseDto tossPaymentConfirmResponseDto = new TossPaymentConfirmResponseDto(
                "DONE", "paymentKey", "orderId"
        );

        when(tossPaymentWithRestClient.requestConfirmation(any(TossPaymentConfirmDto.class)))
                .thenReturn(tossPaymentConfirmResponseDto);

        // when && then
        assertThatThrownBy(
                () -> reservationTicketPaymentService.saveReservationWithTossPaymentGateWay(
                        new ReservationTicketPaymentWithTossRequestDto(request, tossPaymentRequestDto), loginMember))
                .isInstanceOf(IllegalStateException.class);
    }

    private Member saveMember(Long tmp) {
        Member member = new Member("이름" + tmp, "이메일" + tmp, "비밀번호" + tmp, Role.USER);
        memberJpaRepository.save(member);

        return member;
    }

    private Theme saveTheme(Long tmp) {
        Theme theme = new Theme("이름" + tmp, "설명" + tmp, "썸네일" + tmp);
        themeJpaRepository.save(theme);

        return theme;
    }

    private ReservationTime saveTime(LocalTime reservationTime) {
        ReservationTime time = new ReservationTime(reservationTime);
        reservationTimeRepository.save(time);

        return time;
    }
}
