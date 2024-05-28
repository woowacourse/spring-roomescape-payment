package roomescape.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import roomescape.domain.payment.PaymentClient;
import roomescape.domain.reservationdetail.ReservationDetailFactory;
import roomescape.domain.reservation.ReservationFactory;
import roomescape.domain.reservationdetail.ReservationDetailRepository;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationdetail.ReservationTimeRepository;
import roomescape.domain.reservationdetail.ThemeRepository;
import roomescape.infra.payment.TossPaymentClient;

@Configuration
@RequiredArgsConstructor
public class ServiceConfig {
    private final ReservationRepository reservationRepository;
    private final ReservationDetailRepository reservationDetailRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;

    @Bean
    public ReservationFactory reservationFactory() {
        return new ReservationFactory(reservationRepository);
    }

    @Bean
    public ReservationDetailFactory reservationDetailFactory() {
        return new ReservationDetailFactory(
                reservationDetailRepository,
                reservationTimeRepository,
                themeRepository
        );
    }

    @Bean
    public PaymentClient paymentRestClient() {
        return new TossPaymentClient(
                RestClient.builder().baseUrl("https://api.tosspayments.com").build()
        );
    }
}
