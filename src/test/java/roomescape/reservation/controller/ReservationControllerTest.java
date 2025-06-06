package roomescape.reservation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import roomescape.IntegrationTest;
import roomescape.member.domain.Member;
import roomescape.member.domain.Password;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.RoomEscapeInformation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.RoomEscapeInformationRepository;
import roomescape.reservation.repository.WaitingReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@Sql("/member.sql")
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class ReservationControllerTest extends IntegrationTest {

    @Autowired
    ReservationTimeRepository timeRepository;

    @Autowired
    ThemeRepository themeRepository;

    @Autowired
    ReservationRepository reservationRepository;

    @Autowired
    WaitingReservationRepository waitingReservationRepository;

    @Autowired
    RoomEscapeInformationRepository roomEscapeInformationRepository;

    @Autowired
    MemberRepository memberRepository;

    private String adminToken;

    @BeforeEach
    void setUp() {
        // IntegrationTest에서 이미 RestAssured.port를 설정해주지 않는다면 필요합니다.
        // RestAssured.port = port; // port가 IntegrationTest에 의해 주입되는 경우

        // 테스트용 회원(admin) 로그인하여 token 쿠키를 발급받음
        Map<String, String> adminUser = Map.of("email", "admin@naver.com", "password", "1234");
        this.adminToken = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(adminUser)
                .when()
                .post("/login")
                .then()
                .statusCode(200)
                .extract()
                .cookie("token");
    }

    @Test
    void 유저_예약_생성_조회_삭제() {
        // given
        Map<String, String> params = new HashMap<>();
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(1);
        params.put("date", localDate.toString());
        ReservationTime time = timeRepository.save(ReservationTime.from(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(Theme.of("name", "desc", "thumb"));
        params.put("timeId", time.getId().toString());
        params.put("themeId", theme.getId().toString());

        Map<String, String> adminUser = Map.of("email", "admin@naver.com", "password", "1234");
        String token = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(adminUser)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("token");

        // when
        // then
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("id", is(1));

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().delete("/reservations/1")
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    @DisplayName("여러 건의 동시 요청이 들어올 때 하나의 예약만 생성된다")
    void 여러건의_동일_조건에_대한_동시_요청이_들어올_때_하나의_예약만_생성된다() throws InterruptedException {
        int threadCount = 5;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        // 이미 member.sql로 admin@naver.com 계정이 들어 있는 상태
        Member member = memberRepository.findByEmailAndPassword("admin@naver.com",
                        Password.createForMember("1234"))
                .orElseThrow();

        // 예약 시간, 테마 3개 세팅
        ReservationTime time1 = timeRepository.save(ReservationTime.from(LocalTime.of(10, 0)));
        ReservationTime time2 = timeRepository.save(ReservationTime.from(LocalTime.of(10, 0)));
        Theme theme1 = themeRepository.save(Theme.of("name", "desc", "thumb"));
        Theme theme2 = themeRepository.save(Theme.of("name2", "desc2", "thumb2"));
        Theme theme3 = themeRepository.save(Theme.of("name3", "desc3", "thumb3"));

        // 동시 요청 대상은 theme1 슬롯
        Map<String, String> params = new HashMap<>();
        LocalDate targetDate1 = LocalDate.of(2999, 1, 5);
        LocalDate targetDate2 = LocalDate.of(2999, 1, 5);
        params.put("date", targetDate1.toString());
        params.put("timeId", time1.getId().toString());
        params.put("themeId", theme1.getId().toString());

        // theme2, theme3에는 이미 예약 1건씩 선점
        RoomEscapeInformation info2 = RoomEscapeInformation.builder()
                .date(targetDate2)
                .time(time2)
                .theme(theme2)
                .build();
        RoomEscapeInformation saved2 = roomEscapeInformationRepository.save(info2);
        reservationRepository.save(Reservation.builder()
                .roomEscapeInformation(saved2)
                .member(member)
                .build());

        RoomEscapeInformation info3 = RoomEscapeInformation.builder()
                .date(targetDate2)
                .time(time2)
                .theme(theme3)
                .build();
        RoomEscapeInformation saved3 = roomEscapeInformationRepository.save(info3);
        reservationRepository.save(Reservation.builder()
                .roomEscapeInformation(saved3)
                .member(member)
                .build());

        // 기존 예약 건수는 2건
        long existingCount = reservationRepository.findByMember(member).size();
        assertThat(existingCount).isEqualTo(2);

        // when: 5개의 스레드가 거의 동시에 예약 요청을 보냄
        for (int i = 0; i < threadCount; i++) {
            new Thread(() -> {
                try {
                    startLatch.await();
                    RestAssured.given()
                            .cookie("token", adminToken)
                            .contentType(ContentType.JSON)
                            .body(params)
                            .when()
                            .post("/reservations")
                            .then()
                            .statusCode(201)
                            .log().ifValidationFails();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    doneLatch.countDown();
                }
            }).start();
        }
        // 모든 스레드가 준비된 후 동시에 실행
        startLatch.countDown();
        doneLatch.await();

        // 잠시 대기하여 트랜잭션 커밋 및 락 해제 보장
        Thread.sleep(500);

        // then: 총 예약 건수가 3건이어야 함 (기존 2 + 신규 1)
        long finalCount = reservationRepository.findByMember(member).size();
        assertThat(finalCount).isEqualTo(3);
    }
}
