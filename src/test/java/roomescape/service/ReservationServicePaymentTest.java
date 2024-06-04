package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.*;
import roomescape.domain.repository.*;
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.request.ReservationSaveDto;
import roomescape.service.response.ReservationPaymentDto;
import roomescape.service.response.PaymentDto;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static roomescape.Fixture.VALID_RESERVATION_TIME;
import static roomescape.Fixture.VALID_THEME;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql(scripts = "/truncate.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Transactional
@ExtendWith(MockitoExtension.class)
public class ReservationServicePaymentTest {

    private static final List<Member> MEMBERS = List.of(
            new Member(
                    new MemberName("감자"),
                    new MemberEmail("111@aaa.com"),
                    new MemberPassword("asd"),
                    MemberRole.USER
            ),
            new Member(
                    new MemberName("고구마"),
                    new MemberEmail("222@aaa.com"),
                    new MemberPassword("asd"),
                    MemberRole.USER
            ),
            new Member(
                    new MemberName("단호박"),
                    new MemberEmail("333@aaa.com"),
                    new MemberPassword("asd"),
                    MemberRole.USER
            ),
            new Member(
                    new MemberName("밤"),
                    new MemberEmail("444@aaa.com"),
                    new MemberPassword("asd"),
                    MemberRole.USER
            )
    );

    @MockBean
    private PaymentManager paymentManager;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Test
    @DisplayName("올바르게 결제된 예약을 생성한다.")
    void save() {
        Member member = memberRepository.save(MEMBERS.get(0));
        String date = LocalDate.now().plusDays(1).toString();
        Theme theme = themeRepository.save(VALID_THEME);
        ReservationTime time = reservationTimeRepository.save(VALID_RESERVATION_TIME);
        ReservationSaveDto reservationSaveDto = new ReservationSaveDto(date, time.getId(), theme.getId(), member.getId());

        PaymentDto paymentDto = new PaymentDto("paymentKey", "orderId", 1000L);
        when(paymentManager.approve(any())).thenReturn(paymentDto);
        PaymentApproveDto paymentApproveDto = new PaymentApproveDto("paymentKey", "orderId", 1000L);

        ReservationPaymentDto reservationPaymentDto = reservationService.save(reservationSaveDto, paymentApproveDto);

        assertAll(
                () -> assertThat(paymentRepository.findById(reservationPaymentDto.paymentDto().id())).isNotEmpty(),
                () -> assertThat(reservationRepository.findById(reservationPaymentDto.reservationDto().id())).isNotEmpty(),
                () -> assertThat(reservationPaymentDto.reservationDto().name()).isEqualTo(member.getName().getName()),
                () -> assertThat(reservationPaymentDto.paymentDto().paymentKey()).isEqualTo(paymentDto.paymentKey())
        );
    }

}
