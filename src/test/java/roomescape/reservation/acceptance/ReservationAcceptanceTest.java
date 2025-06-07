package roomescape.reservation.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import fixture.MemberFixture;
import fixture.PaymentFixture;
import fixture.ReservationTimeFixture;
import fixture.ThemeFixture;
import io.restassured.RestAssured;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.helper.TestHelper;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.PaymentService;
import roomescape.reservation.dto.request.ReservationAdminCreateRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest.PaymentDetail;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationAcceptanceTest {

    @LocalServerPort
    private int port;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @MockitoBean
    private PaymentService paymentService;

    private final LocalDate tomorrow = LocalDate.now().plusDays(1);

    private ReservationTime reservationTime;

    private Member member;

    private Theme theme;

    private Payment payment;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);
        given(paymentService.confirmPayment(any(), any(), any()))
                .willReturn(payment);

        member = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(member);

        theme = ThemeFixture.createDefault();
        themeRepository.save(theme);

        reservationTime = ReservationTimeFixture.createDefault();
        reservationTimeRepository.save(reservationTime);
    }

    @Test
    @DisplayName("예약 생성 - 성공")
    void createReservation() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );

        // when & then
        TestHelper.postWithToken("/reservations", reservationRequest, token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("date", equalTo(tomorrow.toString()))
                .body("startAt", equalTo(reservationTime.getStartAt().toString()))
                .body("themeName", equalTo(theme.getName()));
    }

    @Test
    @DisplayName("예약 생성 - 중복 예약으로 실패")
    void createDuplicateReservation() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, token);

        // when & then
        TestHelper.postWithToken("/reservations", reservationRequest, token)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("모든 예약 조회")
    void getAllReservations() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, token);

        // when & then
        TestHelper.getWithToken("/admin/reservations", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].date", equalTo(tomorrow.toString()))
                .body("[0].startAt", equalTo(reservationTime.getStartAt().toString()))
                .body("[0].memberName", equalTo(member.getName()))
                .body("[0].themeName", equalTo(theme.getName()));
    }

    @Test
    @DisplayName("예약 삭제")
    void deleteReservation() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, token);

        // when & then
        TestHelper.deleteWithToken("/admin/reservations/1", token)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        TestHelper.getWithToken("/admin/reservations", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("예약 조회 - 필터링 성공")
    void getFilteredReservations() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, token);
        String url = String.format(
                "/admin/reservations/filtered?themeId=%d&memberId=%d&dateFrom=%s&dateTo=%s",
                1L,
                1L,
                LocalDate.now(),
                LocalDate.now().plusWeeks(1)
        );

        // when & then
        TestHelper.getWithToken(url, token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].date", equalTo(tomorrow.toString()))
                .body("[0].startAt", equalTo(reservationTime.getStartAt().toString()))
                .body("[0].memberName", equalTo(member.getName()))
                .body("[0].themeName", equalTo(theme.getName()));
    }

    @Test
    @DisplayName("예약 조회 - 필터링 (일반 유저 권한 부족) 실패")
    void getFilteredReservationsWithNonAdmin() {
        // given
        Member userMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(userMember);
        String token = TestHelper.login(userMember.getEmail(), userMember.getPassword());
        String url = String.format(
                "/admin/reservations/filtered?themeId=%d&memberId=%d&dateFrom=%s&dateTo=%s",
                1L,
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(7)
        );

        // when & then
        TestHelper.getWithToken(url, token)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }

    @Test
    @DisplayName("예약 조회 - 로그인 사용자 예약 조회")
    void getReservationsByMember() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var reservationRequest = new ReservationCreateRequest(
                tomorrow,
                reservationTime.getId(),
                theme.getId(),
                new PaymentDetail(
                        payment.getPaymentKey(),
                        payment.getOrderId(),
                        payment.getAmount(),
                        payment.getPaymentType()
                )
        );
        TestHelper.postWithToken("/reservations", reservationRequest, token);

        // when & then
        TestHelper.getWithToken("/reservations/mine", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].theme", equalTo(theme.getName()))
                .body("[0].date", equalTo(tomorrow.toString()))
                .body("[0].time", equalTo(reservationTime.getStartAt().toString()))
                .body("[0].status", equalTo("예약"));
    }

    @Test
    @DisplayName("관리자 예약 생성 - 성공")
    void createReservationByAdmin() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        Member userMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(userMember);

        var adminCreateRequest = new ReservationAdminCreateRequest(
                tomorrow,
                theme.getId(),
                reservationTime.getId(),
                userMember.getId()
        );

        // when & then
        TestHelper.postWithToken("/admin/reservations", adminCreateRequest, token)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("date", equalTo(tomorrow.toString()))
                .body("startAt", equalTo(reservationTime.getStartAt().toString()))
                .body("themeName", equalTo(theme.getName()));
    }

    @Test
    @DisplayName("관리자 예약 생성 - 일반 유저 권한 부족 실패")
    void createReservationByNonAdmin() {
        // given
        Member userMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(userMember);
        String token = TestHelper.login(userMember.getEmail(), userMember.getPassword());

        var adminCreateRequest = new ReservationAdminCreateRequest(
                tomorrow,
                theme.getId(),
                reservationTime.getId(),
                userMember.getId()
        );

        // when & then
        TestHelper.postWithToken("/admin/reservations", adminCreateRequest, token)
                .then()
                .statusCode(HttpStatus.FORBIDDEN.value());
    }
}
