package roomescape.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClientException;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class ReservationPaymentServiceTest {

    private static MockWebServer mockWebServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservationPaymentService reservationPaymentService;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentClientService paymentClientService;

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

    @DisplayName("토스 결제 승인 요청이 성공했을 때 예약이 완료된다.")
    @Test
    void confirmPaymentSuccess_shouldReturnReservationResponse() throws Exception {
        // given
        // Toss API mock 응답 (성공 케이스)
        String successResponse = objectMapper.writeValueAsString(new PaymentConfirmResponse(
                "paymentKey",
                "orderId",
                50000L,
                "DONE"
        ));

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .setHeader("Content-Type", "application/json")
                .setBody(successResponse));

        Theme theme = themeRepository.save(Theme.createWithoutId("성공테마", "설명", "thumb.jpg"));
        ReservationTime time = reservationTimeRepository.save(ReservationTime.createWithoutId(LocalTime.of(14, 0)));
        Member member = memberRepository.save(Member.createWithoutId("유저", "user@success.com", Role.USER, "password"));

        ReservationCreateRequest reservationCreateRequest =
                new ReservationCreateRequest(LocalDate.now().plusDays(1), theme.getId(), time.getId(), member.getId());
        PaymentConfirmRequest paymentConfirmRequest =
                new PaymentConfirmRequest("orderId", 50000, "paymentKey", "TOSS");

        // when
        var result = reservationPaymentService.confirmPaymentAndAddReservation(
                reservationCreateRequest, paymentConfirmRequest
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.theme().name()).isEqualTo("성공테마");
        assertThat(result.member().name()).isEqualTo("유저");
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

    @Disabled
    @DisplayName("토스 결제 승인 요청이 타임아웃됐을 때 예외를 발생한다.")
    @Test
    void confirmPaymentTimeout_shouldThrowTossPaymentException() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(400)
                .setHeader("Content-Type", "application/json")
                .setBodyDelay(10, TimeUnit.SECONDS)
                .setBody("test body"));

        // when & then
        var paymentConfirmRequest = new PaymentConfirmRequest("orderId", 50000, "paymentKey", "TOSS");
        assertThatThrownBy(() -> paymentClientService.confirm("token", paymentConfirmRequest))
                .isInstanceOf(RestClientException.class);
    }
}
