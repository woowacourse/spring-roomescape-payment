package roomescape.api;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;
import roomescape.dto.time.ReservationTimeRequest;

class ReservationTimeApiTest extends ApiBaseTest {

    @LocalServerPort
    int port;

    @Test
    void 예약_시간_추가() {
        ReservationTimeRequest reservationTimeRequest = createReservationTimeRequest();

        RestAssured
                .given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .body(reservationTimeRequest)
                .when().post("/times")
                .then().log().all()
                .statusCode(201)
                .header("Location", "/times/4")
                .body("id", equalTo(4))
                .body("startAt", equalTo(reservationTimeRequest.startAt().toString()));
    }

    @Test
    void 예약_시간_단일_조회() {
        RestAssured
                .given().log().all()
                .port(port)
                .when().get("/times/1")
                .then().log().all()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("startAt", equalTo("10:00")
                );
    }

    @Test
    void 예약_시간_전체_조회() {
        RestAssured
                .given().log().all()
                .port(port)
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(3));
    }

    @Test
    void 예약_가능_여부가_포함된_시간_조회() {
        String targetDay = LocalDate.now().plusDays(2).toString();

        RestAssured
                .given().log().all()
                .port(port)
                .when().get("/times/available?date=" + targetDay + "&themeId=1")
                .then().log().all()
                .statusCode(200)
                .body("alreadyBooked", notNullValue());
    }

    @Sql("/reset-data.sql")
    @Test
    void 예약_시간_삭제() {
        ReservationTimeRequest reservationTimeRequest = createReservationTimeRequest();
        addReservationTime(reservationTimeRequest);

        RestAssured
                .given().log().all()
                .port(port)
                .when().delete("/times/1")
                .then().log().all()
                .statusCode(204);
    }

    private ReservationTimeRequest createReservationTimeRequest() {
        return new ReservationTimeRequest(LocalTime.parse("13:00"));
    }

    private void addReservationTime(final ReservationTimeRequest reservationTimeRequest) {
        RestAssured
                .given().log().all()
                .port(port)
                .contentType(ContentType.JSON)
                .body(reservationTimeRequest)
                .when().post("/times");
    }
}
