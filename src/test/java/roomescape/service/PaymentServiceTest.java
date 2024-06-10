package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.*;
import roomescape.domain.repository.*;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.request.PaymentSaveDto;
import roomescape.service.response.PaymentDto;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;
import static roomescape.Fixture.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
class PaymentServiceTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private PaymentManager paymentManager;

    @DisplayName("올바르게 예약에 대한 결제를 생성한다.")
    @Test
    void save() {
        Member member = memberRepository.save(VALID_MEMBER);
        Theme theme = themeRepository.save(VALID_THEME);
        ReservationTime time = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        String date = LocalDate.now().plusMonths(1).toString();
        Reservation reservation = reservationRepository.save(new Reservation(member, new ReservationDate(date), time, theme));
        PaymentSaveDto paymentSaveDto = new PaymentSaveDto(member.getId(), reservation.getId(), "paymentKey", "orderId", theme.getPrice());
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "orderId", theme.getPrice());
        when(paymentManager.approve(paymentApproveDto))
                .thenReturn(new PaymentDto("paymentKey", "orderId", theme.getPrice()));


        PaymentDto paymentDto = paymentService.save(paymentSaveDto);
        Payment savedPayment = paymentRepository.findById(paymentDto.id()).orElseThrow();

        assertThat(PaymentDto.from(savedPayment)).isEqualTo(new PaymentDto(1L, "paymentKey", "orderId", theme.getPrice()));
    }

    @DisplayName("결제가 있는 예약에 대한 결제 생성 시 예외가 발생한다.")
    @Test
    void saveWithReservationPaymentExist() {
        Member member = memberRepository.save(VALID_MEMBER);
        Theme theme = themeRepository.save(VALID_THEME);
        ReservationTime time = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        String date = LocalDate.now().plusMonths(1).toString();
        Reservation reservation = reservationRepository.save(new Reservation(member, new ReservationDate(date), time, theme));
        paymentRepository.save(new Payment(reservation, "paymentKey", "orderId"));
        PaymentSaveDto paymentSaveDto = new PaymentSaveDto(member.getId(), reservation.getId(), "paymentKey", "orderId", theme.getPrice());
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "orderId", theme.getPrice());
        when(paymentManager.approve(paymentApproveDto))
                .thenReturn(new PaymentDto("paymentKey", "orderId", theme.getPrice()));


        assertThatThrownBy(() -> paymentService.save(paymentSaveDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("이미 결제가 존재하는 예약입니다.");
    }

    @DisplayName("테마와 일치하지 않는 금액으로 결제 생성 시 예외가 발생한다.")
    @Test
    void saveWithInvalidAmount() {
        Member member = memberRepository.save(VALID_MEMBER);
        Theme theme = themeRepository.save(VALID_THEME);
        ReservationTime time = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        String date = LocalDate.now().plusMonths(1).toString();
        Reservation reservation = reservationRepository.save(new Reservation(member, new ReservationDate(date), time, theme));
        PaymentSaveDto paymentSaveDto = new PaymentSaveDto(member.getId(), reservation.getId(), "paymentKey", "orderId", theme.getPrice() + 100);

        assertThatThrownBy(() -> paymentService.save(paymentSaveDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("테마 가격과 결제 금액이 일치하지 않습니다.");
    }

    @DisplayName("예약하지 않은 다른 사용자가 결제 생성 시 예외가 발생한다.")
    @Test
    void saveWithInvalidLoginMember() {
        Member reservationMember = memberRepository.save(VALID_MEMBER);
        Member loginMember = memberRepository.save(new Member(new MemberName("name"), new MemberEmail("emmm@naver.com"), new MemberPassword("asd"), MemberRole.USER));
        Theme theme = themeRepository.save(VALID_THEME);
        ReservationTime time = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        String date = LocalDate.now().plusMonths(1).toString();
        Reservation reservation = reservationRepository.save(new Reservation(reservationMember, new ReservationDate(date), time, theme));
        PaymentSaveDto paymentSaveDto = new PaymentSaveDto(loginMember.getId(), reservation.getId(), "paymentKey", "orderId", theme.getPrice() + 100);

        assertThatThrownBy(() -> paymentService.save(paymentSaveDto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("예약에 대한 결제 권한이 없는 사용자입니다.");
    }
}
