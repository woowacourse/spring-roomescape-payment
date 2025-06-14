package roomescape.reservation.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import fixture.MemberFixture;
import fixture.ReservationTimeFixture;
import io.restassured.RestAssured;
import java.time.LocalTime;
import java.util.List;
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
import roomescape.reservation.dto.request.ReservationTimeCreateRequest;
import roomescape.reservation.entity.ReservationTime;
import roomescape.reservation.repository.ReservationTimeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ReservationTimeAcceptanceTest {

    @LocalServerPort
    private int port;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    private Member member;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        member = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(member);
    }

    @Test
    @DisplayName("예약 시간 생성 - 성공")
    void createTime() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        ReservationTime time = ReservationTimeFixture.create(LocalTime.of(10, 0));
        var timeRequest = new ReservationTimeCreateRequest(time.getStartAt());

        // when & then
        TestHelper.postWithToken("/admin/times", timeRequest, token)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("startAt", equalTo(time.getStartAt().toString()));
    }

    @Test
    @DisplayName("모든 예약 시간 조회")
    void getAllTimes() {
        // given
        List<ReservationTime> times = List.of(
                ReservationTimeFixture.create(LocalTime.of(10, 0)),
                ReservationTimeFixture.create(LocalTime.of(12, 0))
        );
        reservationTimeRepository.saveAll(times);

        // when & then
        TestHelper.get("/times")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2));
    }

    @Test
    @DisplayName("예약 가능 시간 조회 - 성공")
    void getAvailableTimes() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        ReservationTime time = ReservationTimeFixture.create(LocalTime.of(10, 0));
        var timeRequest = new ReservationTimeCreateRequest(time.getStartAt());
        TestHelper.postWithToken("/admin/times", timeRequest, token);

        // when & then
        TestHelper.get("/times/available?date=2024-03-20&themeId=1")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(1))
                .body("[0].startAt", equalTo(time.getStartAt().toString()))
                .body("[0].alreadyBooked", equalTo(false));
    }

    @Test
    @DisplayName("예약 시간 삭제")
    void deleteTime() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        ReservationTime time = ReservationTimeFixture.create(LocalTime.of(10, 0));
        var timeRequest = new ReservationTimeCreateRequest(time.getStartAt());
        TestHelper.postWithToken("/admin/times", timeRequest, token);

        // when & then
        TestHelper.deleteWithToken("/admin/times/1", token)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        TestHelper.get("/times")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }

    @Test
    @DisplayName("예약 시간 생성 - 운영 시간 이외 불가능으로 실패")
    void createTimeOutsideOperatingHours() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        ReservationTime time = ReservationTimeFixture.create(LocalTime.of(9, 0));
        var timeRequest = new ReservationTimeCreateRequest(time.getStartAt());
        TestHelper.postWithToken("/admin/times", timeRequest, token);

        // when & then
        TestHelper.postWithToken("/admin/times", timeRequest, token)
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @Test
    @DisplayName("예약 시간 생성 - running time 겹치는 시간으로 실패")
    void createTimeWithOverlappingRunningTime() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());

        ReservationTime time = ReservationTimeFixture.create(LocalTime.of(10, 0));
        var timeRequest = new ReservationTimeCreateRequest(time.getStartAt());
        TestHelper.postWithToken("/admin/times", timeRequest, token);

        ReservationTime failTime = ReservationTimeFixture.create(LocalTime.of(11, 0));
        var failTimeRequest = new ReservationTimeCreateRequest(failTime.getStartAt());

        // when & then
        TestHelper.postWithToken("/admin/times", failTimeRequest, token)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }
}
