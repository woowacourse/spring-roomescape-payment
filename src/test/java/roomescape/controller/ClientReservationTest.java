package roomescape.controller;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import roomescape.domain.Role;
import roomescape.dto.response.TimeSlotResponse;
import roomescape.infrastructure.TokenGenerator;

import static roomescape.fixture.TestFixture.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {"spring.config.location=classpath:/application.properties"})
class ClientReservationTest {


    @Autowired
    TokenGenerator tokenGenerator;

    private static final String EMAIL = "test2DB@email.com";
    private static final int RESERVATION_COUNT = 4;
    private static final int WAITING_COUNT = 4;

    @LocalServerPort
    private int port;
    private String accessToken;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        accessToken = tokenGenerator.createToken(EMAIL, Role.USER.name());
    }

    private int getTotalTimeSlotsCount() {
        List<TimeSlotResponse> timeSlots = RestAssured.given().port(port)
                .when().get("/times")
                .then().extract().body()
                .jsonPath().getList("", TimeSlotResponse.class);
        return timeSlots.size();
    }

    @DisplayName("날짜와 테마를 선택하면 예약 가능한 시간을 확인할 수 있다.")
    @Test
    void given_dateThemeId_when_books_then_statusCodeIsOk() {
        RestAssured.given().log().all()
                .when().get("/books/2099-04-30/1")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(getTotalTimeSlotsCount()));
    }

    @DisplayName("사용자 예약 시 부적절한 입력값이 들어 올 경우 400오류를 반환한다.")
    @ParameterizedTest
    @CsvSource({"2099-01-11,test", "1111-22-33,1", "1111-22-33,test", ","})
    void given_when_booksWithInvalidDateAndThemeId_then_statusCodeIsBadRequest(String invalidDate,
                                                                               String invalidThemeId) {
        RestAssured.given().log().all()
                .when().get("/books/%s/%s".formatted(invalidDate, invalidThemeId))
                .then().log().all()
                .statusCode(400);
    }

    /* 예약 현황
        testdb@email.com 3개
        testdb2@email.com 4개
   */
    @DisplayName("로그인 된 유저의 예약 내역을 조회하면 200을 응답한다.")
    @Test
    void given_when_find_my_reservations_then_statusCodeIsOk() {
        RestAssured.given().log().all()
                .cookies(TOKEN, accessToken)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200);
    }

    @DisplayName("로그인 된 유저의 예약 내역을 조회하면 예약 내역과 예약 대기 내역을 모두 응답한다.")
    @Test
    void given_when_find_my_reservations_then_responseWithReservationAndWaiting() {
        RestAssured.given().log().all()
                .cookies(TOKEN, accessToken)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(RESERVATION_COUNT + WAITING_COUNT));
    }
}
