package roomescape.reservationtime;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.model.Reservation;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservationtime.model.ReservationTime;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.model.Theme;
import roomescape.theme.repository.ThemeRepository;
import roomescape.util.IntegrationTest;
import roomescape.util.RestDocsConfiguration;

@IntegrationTest
class ReservationTimeIntegrationTest extends RestDocsConfiguration {

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @LocalServerPort
    private int port;


    @BeforeEach
    void init() {
        RestAssured.port = this.port;
    }

    @Test
    @DisplayName("방탈출 시간대 생성 성공")
    void createReservationTime() {
        Map<String, Object> params = new HashMap<>();
        params.put("startAt", "10:00");

        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()

                .statusCode(201)
                .body("id", equalTo(1))
                .body("startAt", equalTo("10:00"));
    }

    @Test
    @DisplayName("방탈출 시간대 생성 실패: 시간 형식 다름")
    void createReservationTime_WhenTimeIsInvalidType() {
        Map<String, Object> params = new HashMap<>();
        params.put("startAt", "10:0--");

        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()

                .statusCode(400)
                .body("detail", equalTo("startAt 필드의 형식이 잘못되었습니다."));
    }

    @Test
    @DisplayName("방탈출 시간대 생성 실패: 중복 시간")
    void createReservationTime_WhenTimeIsExist() {
        createReservationTime();
        Map<String, Object> params = new HashMap<>();
        params.put("startAt", "10:00");

        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/times")
                .then().log().all()

                .statusCode(400)
                .body("detail", equalTo("생성하려는 시간 10:00가 이미 존재합니다. 시간을 생성할 수 없습니다."));
    }

    @Test
    @DisplayName("방탈출 테마 목록 조회 성공")
    void getReservationTimes() {
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(20, 0)));
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(10, 0)));

        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .when().get("/times")
                .then().log().all()

                .statusCode(200)
                .body("id", hasItems(1, 2))
                .body("startAt", hasItems("20:00", "10:00"));
    }

    @Test
    @DisplayName("방탈출 시간 하나 조회 성공")
    void getReservationTime() {
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(20, 0)));

        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .when().get("/times/1")
                .then().log().all()

                .statusCode(200)
                .body("id", equalTo(1))
                .body("startAt", equalTo("20:00"));
    }

    @Test
    @DisplayName("방탈출 시간 조회 실패: 없는 시간")
    void getReservationTime_WhenTimeNotExist() {
        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .when().get("/times/1")
                .then().log().all()

                .statusCode(404)
                .body("detail", equalTo("식별자 1에 해당하는 시간이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("방탈출 시간 하나 삭제 성공")
    void deleteReservationTime() {
        reservationTimeRepository.save(new ReservationTime(LocalTime.of(20, 0)));

        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .when().delete("/times/1")
                .then().log().all()

                .statusCode(204);
    }

    @Test
    @DisplayName("방탈출 시간 조회 실패: 시간 없음")
    void deleteReservationTime_WhenTimeNotExist() {
        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .when().delete("/times/1")
                .then().log().all()

                .statusCode(404)
                .body("detail", equalTo("식별자 1에 해당하는 시간이 존재하지 않습니다. 삭제가 불가능합니다."));
    }

    @Test
    @DisplayName("방탈출 시간 조회 실패: 사용 중인 시간")
    void deleteReservationTime_WhenTimeInUsage() {
        Theme theme = themeRepository.save(new Theme("테마이름", "설명", "썸네일"));
        ReservationTime reservationTime = reservationTimeRepository.save(
                new ReservationTime(LocalTime.of(20, 0)));
        Member member = memberRepository.save(new Member("몰리", Role.USER, "login@naver.com", "hihi"));
        reservationRepository.save(
                new Reservation(member, LocalDate.parse("2024-04-23"), reservationTime, theme));

        RestAssured.given(this.spec).log().all()
                .contentType(ContentType.JSON)
                .when().delete("/times/1")
                .then().log().all()

                .statusCode(409)
                .body("detail", equalTo("식별자 1인 시간을 사용 중인 예약이 존재합니다. 삭제가 불가능합니다."));
    }
}
