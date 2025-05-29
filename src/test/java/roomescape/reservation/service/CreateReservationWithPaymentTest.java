package roomescape.reservation.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.auth.service.dto.LoginMember;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.payment.service.TossPaymentService;
import roomescape.payment.service.dto.ConfirmPaymentRequest;
import roomescape.payment.service.dto.ConfirmPaymentResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.service.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.service.dto.response.ReservationResponse;
import roomescape.reservation.service.dto.response.ReservationTimeResponse;
import roomescape.theme.domain.Theme;
import roomescape.theme.service.dto.response.ThemeResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

@ActiveProfiles("test")
@DataJpaTest
@Import({CreateReservationService.class})
public class CreateReservationWithPaymentTest {

    private final LocalDateTime now = LocalDateTime.now();
    private final Theme theme = new Theme("포스티", "공포", "wwww.um.com");
    private final ReservationTime time = new ReservationTime(LocalTime.of(8, 0));
    private final Member member = new Member("포스티", "test@test.com", "12341234", Role.MEMBER);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CreateReservationService reservationService;

    @MockitoBean
    private TossPaymentService mockTossPaymentService = Mockito.mock(TossPaymentService.class);

    @BeforeEach
    void setup() {
        entityManager.persist(theme);
        entityManager.persist(time);
        entityManager.persist(member);
    }

    @DisplayName("멤버의 결제 및 예약을 생성한다.")
    @Test
    void createMembersPaymentAndReservation() {
        // given
        ConfirmPaymentRequest paymentRequest = new ConfirmPaymentRequest("paymentKey", "1234", 1000);
        ConfirmPaymentResponse paymentResponse = new ConfirmPaymentResponse(1000, "paymentKey", null);
        Mockito.when(mockTossPaymentService.postConfirmPayment(paymentRequest)).thenReturn(paymentResponse);

        LocalDate date = now.plusDays(1).toLocalDate();
        ReservationWithPaymentRequest reservationWithPaymentRequest = new ReservationWithPaymentRequest(
                date,
                time.getId(),
                theme.getId(),
                paymentResponse.paymentKey(),
                paymentRequest.orderId(),
                1000
        );

        // when
        ReservationResponse result = reservationService.createWithPayment(reservationWithPaymentRequest, LoginMember.of(member));

        // then
        assertSoftly(softly -> {
            softly.assertThat(entityManager.find(Reservation.class, result.id())).isNotNull();
            softly.assertThat(result.member().name()).isEqualTo(member.getName());
            softly.assertThat(result.date()).isEqualTo(date);
            softly.assertThat(result.time()).isEqualTo(new ReservationTimeResponse(time.getId(), time.getStartAt()));
            softly.assertThat(result.theme()).isEqualTo(new ThemeResponse(theme.getId(), theme.getName(), theme.getDescription(), theme.getThumbnail()));
        });
    }
}
