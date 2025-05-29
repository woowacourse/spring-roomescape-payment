package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationPaymentServiceTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ReservationPaymentService reservationPaymentService;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @BeforeAll
    static void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void overrideBaseUrl(DynamicPropertyRegistry registry) {
        registry.add("toss.base-url", () -> mockWebServer.url("/").toString());
    }

    @DisplayName("토스 결제 승인 요청이 실패했을 때 예외를 발생한다.")
    @Test
    void confirmPaymentFail_shouldThrowTossPaymentException() throws Exception {
        // given
        String errorResponse = objectMapper.writeValueAsString(
                new TestData("NOT_FOUND_PAYMENT", "존재하지 않는 결제 입니다.")
        );

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", "application/json")
                .setBody(errorResponse));

        Theme theme = Theme.createWithoutId("테마1", "테마1 설명", "thumbnail1.jpg");
        themeRepository.save(theme);
        ReservationTime reservationTime = ReservationTime.createWithoutId(LocalTime.of(10, 0));
        reservationTimeRepository.save(reservationTime);
        Member member = Member.createWithoutId("사용자", "user@example.com", Role.USER, "password");
        memberRepository.save(member);

        // when & then
        var reservationCreateRequest = new ReservationCreateRequest(LocalDate.now().plusDays(1), theme.getId(),
                reservationTime.getId(), member.getId());
        var paymentConfirmRequest = new PaymentConfirmRequest("orderId", 50000, "paymentKey", "TOSS");
        assertThatThrownBy(() -> reservationPaymentService.confirmPaymentAndAddReservation(
                reservationCreateRequest, paymentConfirmRequest
        )).hasMessageContaining(errorResponse);
    }
}
