package roomescape.reservation.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import fixture.MemberFixture;
import fixture.PaymentFixture;
import fixture.ReservationFixture;
import fixture.ReservationTimeFixture;
import fixture.ThemeFixture;
import io.restassured.RestAssured;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.helper.TestHelper;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.entity.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.dto.request.WaitingCreateRequest;
import roomescape.reservation.entity.Reservation;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.entity.Theme;
import roomescape.theme.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class WaitingAcceptanceTest {

    @LocalServerPort
    private int port;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    private LocalDate tomorrow = LocalDate.now().plusDays(1);

    private ReservationTime time;

    private Member adminMember;

    private Theme theme;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        time = ReservationTimeFixture.create(LocalTime.of(10, 0));
        reservationTimeRepository.save(time);

        adminMember = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(adminMember);

        theme = ThemeFixture.createDefault();
        themeRepository.save(theme);
    }

    @Test
    @DisplayName("대기 예약 생성 - 성공")
    void createWaiting() {
        // given
        String token = TestHelper.login(adminMember.getEmail(), adminMember.getPassword());

        Member otherMember = MemberFixture.createDefault();
        memberRepository.save(otherMember);

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);

        Reservation reservation = ReservationFixture.create(tomorrow, time, theme, otherMember, payment);
        reservationRepository.save(reservation);

        var waitingRequest = new WaitingCreateRequest(tomorrow, time.getId(), theme.getId());

        // when & then
        TestHelper.postWithToken("/waitings", waitingRequest, token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("date", equalTo(tomorrow.toString()))
                .body("startAt", equalTo(time.getStartAt().toString()))
                .body("themeName", equalTo(theme.getName()))
                .body("memberName", equalTo(adminMember.getName()));
    }

    @Test
    @DisplayName("대기 예약 생성 - 이미 예약한 사용자가 대기 예약 생성시 실패")
    void createWaitingByReservedMember() {
        // given
        String token = TestHelper.login(adminMember.getEmail(), adminMember.getPassword());

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);

        Reservation reservation = ReservationFixture.create(tomorrow, time, theme, adminMember, payment);
        reservationRepository.save(reservation);

        var waitingRequest = new WaitingCreateRequest(tomorrow, time.getId(), theme.getId());

        // when & then
        TestHelper.postWithToken("/waitings", waitingRequest, token)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("모든 대기 예약 조회")
    void getAllWaitings() {
        // given
        String token = TestHelper.login(adminMember.getEmail(), adminMember.getPassword());

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);

        Reservation reservation = ReservationFixture.create(tomorrow, time, theme, adminMember, payment);
        reservationRepository.save(reservation);

        Member firstWaitingMember = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(firstWaitingMember);
        String firstToken = TestHelper.login(firstWaitingMember.getEmail(), firstWaitingMember.getPassword());
        var waitingRequest = new WaitingCreateRequest(tomorrow, time.getId(), theme.getId());
        TestHelper.postWithToken("/waitings", waitingRequest, firstToken);

        Member secondWaitingMember = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(secondWaitingMember);
        String secondToken = TestHelper.login(secondWaitingMember.getEmail(), secondWaitingMember.getPassword());
        TestHelper.postWithToken("/waitings", waitingRequest, secondToken);

        // when & then
        TestHelper.getWithToken("/admin/waitings", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2));
    }

    @Test
    @DisplayName("어드민 권한 대기 예약 삭제 - 성공")
    void deleteWaiting() {
        // given
        String token = TestHelper.login(adminMember.getEmail(), adminMember.getPassword());

        Member otherMember = MemberFixture.createDefault();
        memberRepository.save(otherMember);

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);

        Reservation reservation = ReservationFixture.create(tomorrow, time, theme, otherMember, payment);
        reservationRepository.save(reservation);

        var waitingRequest = new WaitingCreateRequest(tomorrow, time.getId(), theme.getId());
        TestHelper.postWithToken("/waitings", waitingRequest, token);

        // when
        TestHelper.deleteWithToken("/waitings/1", token)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        // then
        TestHelper.getWithToken("/admin/waitings", token)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("유저 권한 예약 대기 삭제 - 다른 사용자 예약 삭제시 실패")
    void deleteWaitingByOtherMember() {
        // given
        Member otherMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(otherMember);

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);
        // 예약 생성
        Reservation reservation = ReservationFixture.create(tomorrow, time, theme, otherMember, payment);
        reservationRepository.save(reservation);
        // 대기 예약 생성
        String adminMemberToken = TestHelper.login(adminMember.getEmail(), adminMember.getPassword());
        var waitingRequest = new WaitingCreateRequest(tomorrow, time.getId(), theme.getId());
        TestHelper.postWithToken("/waitings", waitingRequest, adminMemberToken);

        String otherMemberToken = TestHelper.login(otherMember.getEmail(), otherMember.getPassword());

        // when & then
        TestHelper.deleteWithToken("/waitings/1", otherMemberToken)
                .then()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    @DisplayName("대기 예약 승인 - 성공")
    void approveWaiting() {
        // given
        Member otherMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(otherMember);

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);
        // 예약 생성
        Reservation reservation = ReservationFixture.create(tomorrow, time, theme, otherMember, payment);
        reservationRepository.save(reservation);
        // 대기 예약 생성
        String adminMemberToken = TestHelper.login(adminMember.getEmail(), adminMember.getPassword());
        var waitingRequest = new WaitingCreateRequest(tomorrow, time.getId(), theme.getId());
        TestHelper.postWithToken("/waitings", waitingRequest, adminMemberToken);
        // 대기 예약을 승인하기 위해 미리 존재하는 예약 삭제
        reservationRepository.delete(reservation);

        // when
        TestHelper.postWithToken("/admin/waitings/1/approve", adminMemberToken)
                .then()
                .statusCode(HttpStatus.OK.value());

        // then
        TestHelper.getWithToken("/admin/waitings", adminMemberToken)
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("대기 예약 승인 - 이미 예약이 존재시 실패")
    void approveWaitingWithExistingReservation() {
        // given
        Member otherMember = MemberFixture.create(RoleType.USER);
        memberRepository.save(otherMember);

        Payment payment = PaymentFixture.createDefault();
        paymentRepository.save(payment);
        // 예약 생성
        Reservation reservation = ReservationFixture.create(tomorrow, time, theme, otherMember, payment);
        reservationRepository.save(reservation);
        // 대기 예약 생성
        String adminMemberToken = TestHelper.login(adminMember.getEmail(), adminMember.getPassword());
        var waitingRequest = new WaitingCreateRequest(tomorrow, time.getId(), theme.getId());
        TestHelper.postWithToken("/waitings", waitingRequest, adminMemberToken);

        // when & then
        TestHelper.postWithToken("/admin/waitings/1/approve", adminMemberToken)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
