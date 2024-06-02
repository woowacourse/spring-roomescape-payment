package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.http.HttpStatus;
import roomescape.fixture.ReservationFixture;
import roomescape.fixture.TimeFixture;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.is;

class ReservationTimeAcceptanceTest extends AcceptanceTest {
    @DisplayName("어드민이 시간 정보를 추가한다.")
    @Test
    void createReservationTimeByAdmin() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .contentType(ContentType.JSON)
                .body(TimeFixture.createTimeCreateRequest(LocalTime.of(11, 0)))
                .when().post("/times")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("일반 사용자가 시간 정보를 추가하려고 하면 예외가 발생한다.")
    @Test
    void createReservationTimeByGuest() {
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .contentType(ContentType.JSON)
                .body(TimeFixture.createTimeCreateRequest(LocalTime.of(11, 0)))
                .when().post("/times")
                .then().log().all()
                .assertThat().statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));
    }

    @DisplayName("중복된 시간을 생성할 수 없다.")
    @TestFactory
    Stream<DynamicTest> createDuplicateTime() {
        return Stream.of(
                DynamicTest.dynamicTest("시간을 추가한다", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .contentType(ContentType.JSON)
                            .body(TimeFixture.createTimeCreateRequest(LocalTime.of(11, 0)))
                            .when().post("/times")
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.CREATED.value());
                }),
                DynamicTest.dynamicTest("같은 시간을 추가하려고 시도하면 400 응답을 반환한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .contentType(ContentType.JSON)
                            .body(TimeFixture.createTimeCreateRequest(LocalTime.of(11, 0)))
                            .when().post("/times")
                            .then().log().all().statusCode(HttpStatus.BAD_REQUEST.value())
                            .body("message", is("이미 같은 시간이 존재합니다."));
                })
        );
    }

    @DisplayName("등록된 시간 내역을 조회한다.")
    @Test
    void findAllReservationTime() {
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().get("/times")
                .then().log().all().statusCode(HttpStatus.OK.value());
    }

    @DisplayName("어드민이 시간 정보를 id로 삭제한다.")
    @Test
    void deleteReservationTimeById() {
        //given
        long timeId = reservationDetail.getReservationTime().getId();

        //when & then
        RestAssured.given().log().all()
                .cookie("token", adminToken)
                .when().delete("/times/" + timeId)
                .then().log().all()
                .assertThat().statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("일반 사용자는 시간 정보를 삭제할 수 없다.")
    @Test
    void cannotDeleteReservationTimeByIdByGuest() {
        //given
        long timeId = reservationDetail.getReservationTime().getId();

        //when & then
        RestAssured.given().log().all()
                .cookie("token", guestToken)
                .when().delete("/times/" + timeId)
                .then().log().all()
                .assertThat().statusCode(HttpStatus.FORBIDDEN.value())
                .body("message", is("권한이 없습니다. 관리자에게 문의해주세요."));

    }

    @DisplayName("관리자는 이미 예약이 존재하는 시간은 삭제할 수 없다.")
    @Test
    Stream<DynamicTest> cannotDeleteReservationTime() {
        long timeId = reservationDetail.getReservationTime().getId();

        return Stream.of(
                DynamicTest.dynamicTest("guest가 10시 예약을 생성한다.", () -> {
                    RestAssured.given().log().all()
                            .contentType(ContentType.JSON)
                            .cookie("token", guestToken)
                            .body(ReservationFixture.createReservationRequest(reservationDetail))
                            .when().post("/reservations")
                            .then().log().all().extract().cookie("token");
                }),
                DynamicTest.dynamicTest("예약이 존재하는 시간을 삭제하려고 하면 예외가 발생한다.", () -> {
                    RestAssured.given().log().all()
                            .cookie("token", adminToken)
                            .when().delete("/times/" + timeId)
                            .then().log().all()
                            .assertThat().statusCode(HttpStatus.BAD_REQUEST.value())
                            .body("message", is("해당 시간에 예약(대기)이 존재해서 삭제할 수 없습니다."));
                })
        );
    }

    @DisplayName("날짜와 테마로 예약 가능한 시간 목록을 조회할 수 있다.")
    @TestFactory
    void findAvailableTime() {
        //given
        long themeId = 1;
        String date = LocalDate.now().plusDays(1).format(DateTimeFormatter.ISO_DATE);

        //when&then
        RestAssured.given().log().all()
                .when().get("/times/available?date=" + date + "&themeId=" + themeId)
                .then().log().all().statusCode(HttpStatus.OK.value());
    }
}
