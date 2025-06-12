package roomescape.approval.application.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.approval.domain.AdminApproval;
import roomescape.approval.domain.Approval;
import roomescape.approval.domain.ApprovalType;
import roomescape.approval.domain.Onsite;
import roomescape.approval.domain.Payment;
import roomescape.approval.infrastructure.toss.TossPaymentCommandService;
import roomescape.fixture.MemberFixture;
import roomescape.member.domain.Member;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationDate;
import roomescape.reservation.domain.ReservationSpec;
import roomescape.reservationTime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

@ExtendWith(MockitoExtension.class)
class ApprovalCommandServiceProviderTest {

    @Mock
    private AdminApprovalCommandService adminApprovalCommandService;

    @Mock
    private OnSiteCommandService onSiteCommandService;

    @Mock
    private TossPaymentCommandService tossPaymentCommandService;

    private List<ApprovalCommandService<? extends Approval>> approvalCommandServices;

    private ApprovalCommandServiceProvider approvalCommandServiceProvider;

    @BeforeEach
    void setUp() {
        approvalCommandServices = List.of(
                onSiteCommandService,
                tossPaymentCommandService,
                adminApprovalCommandService
        );
        approvalCommandServiceProvider = new ApprovalCommandServiceProvider(approvalCommandServices);
    }

    @DisplayName("AdminApproval 타입에 맞는 서비스를 찾는다")
    @Test
    void findServiceForAdminApproval() {
        // given
        Member member = MemberFixture.createMember("test", "test@example.com", "password");
        Theme theme = new Theme("테마", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationTime time = new ReservationTime(LocalTime.of(10, 0));
        ReservationSpec spec = new ReservationSpec(new ReservationDate(LocalDate.now().plusDays(1)), time, theme);
        Reservation reservation = new Reservation(member, spec);
        AdminApproval adminApproval = new AdminApproval(reservation, member);

        lenient().when(adminApprovalCommandService.supports(adminApproval.getType())).thenReturn(true);
        lenient().when(onSiteCommandService.supports(adminApproval.getType())).thenReturn(false);
        lenient().when(tossPaymentCommandService.supports(adminApproval.getType())).thenReturn(false);

        // when
        ApprovalCommandService<AdminApproval> service = approvalCommandServiceProvider.findService(adminApproval);

        // then
        assertThat(service).isEqualTo(adminApprovalCommandService);
    }

    @DisplayName("Onsite 타입에 맞는 서비스를 찾는다")
    @Test
    void findServiceForOnsite() {
        // given
        Member member = MemberFixture.createMember("test", "test@example.com", "password");
        Theme theme = new Theme("테마", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationTime time = new ReservationTime(LocalTime.of(10, 0));
        ReservationSpec spec = new ReservationSpec(new ReservationDate(LocalDate.now().plusDays(1)), time, theme);
        Reservation reservation = new Reservation(member, spec);
        Onsite onsite = new Onsite(reservation, BigDecimal.valueOf(10000));

        lenient().when(adminApprovalCommandService.supports(onsite.getType())).thenReturn(false);
        lenient().when(onSiteCommandService.supports(onsite.getType())).thenReturn(true);
        lenient().when(tossPaymentCommandService.supports(onsite.getType())).thenReturn(false);

        // when
        ApprovalCommandService<Onsite> service = approvalCommandServiceProvider.findService(onsite);

        // then
        assertThat(service).isEqualTo(onSiteCommandService);
    }

    @DisplayName("Payment 타입에 맞는 서비스를 찾는다")
    @Test
    void findServiceForPayment() {
        // given
        Member member = MemberFixture.createMember("test", "test@example.com", "password");
        Theme theme = new Theme("테마", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationTime time = new ReservationTime(LocalTime.of(10, 0));
        ReservationSpec spec = new ReservationSpec(new ReservationDate(LocalDate.now().plusDays(1)), time, theme);
        Reservation reservation = new Reservation(member, spec);
        Payment payment = new Payment(reservation, "orderId", "paymentKey", BigDecimal.valueOf(10000));

        lenient().when(adminApprovalCommandService.supports(payment.getType())).thenReturn(false);
        lenient().when(onSiteCommandService.supports(payment.getType())).thenReturn(false);
        lenient().when(tossPaymentCommandService.supports(payment.getType())).thenReturn(true);

        // when
        ApprovalCommandService<Payment> service = approvalCommandServiceProvider.findService(payment);

        // then
        assertThat(service).isEqualTo(tossPaymentCommandService);
    }

    @DisplayName("지원하지 않는 승인 타입인 경우 예외가 발생한다")
    @Test
    void findServiceForUnsupportedType() {
        // given
        Member member = MemberFixture.createMember("test", "test@example.com", "password");
        Theme theme = new Theme("테마", "설명", "썸네일", BigDecimal.valueOf(10000));
        ReservationTime time = new ReservationTime(LocalTime.of(10, 0));
        ReservationSpec spec = new ReservationSpec(new ReservationDate(LocalDate.now().plusDays(1)), time, theme);
        Reservation reservation = new Reservation(member, spec);

        Approval mockApproval = new Approval(reservation) {
            @Override
            public ApprovalType getType() {
                return null;
            }
        };

        lenient().when(adminApprovalCommandService.supports(mockApproval.getType())).thenReturn(false);
        lenient().when(onSiteCommandService.supports(mockApproval.getType())).thenReturn(false);
        lenient().when(tossPaymentCommandService.supports(mockApproval.getType())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> approvalCommandServiceProvider.findService(mockApproval))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 승인 방식입니다");
    }
}
