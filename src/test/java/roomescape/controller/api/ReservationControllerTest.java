package roomescape.controller.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import roomescape.domain.member.Member;
import roomescape.domain.member.Role;
import roomescape.dto.auth.LoginRequest;
import roomescape.dto.auth.SignUpRequest;
import roomescape.dto.reservation.AdminReservationCreateRequest;
import roomescape.dto.theme.ThemeCreateRequest;
import roomescape.dto.time.ReservationTimeCreateRequest;
import roomescape.repository.MemberRepository;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ReservationControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    @DisplayName("Reservation 목록 내용 갯수를 검사한다")
    @Test
    void reservationTest() {
        RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Nested
    @DisplayName("예약 삭제")
    class ReservationDeleteTest {

        String loginToken;

        @DisplayName("예약을 삭제할 수 있다")
        @Test
        void reservationDeleteTest() {
            Member admin = Member.createWithoutId("가이온", "hello@woowa.com", Role.ADMIN, "password");
            memberRepository.save(admin);

            LoginRequest loginRequest = new LoginRequest("hello@woowa.com", "password");

            Map<String, String> cookies = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(loginRequest)
                    .when().post("/login")
                    .getCookies();

            loginToken = cookies.get("token");

            LocalTime reservationTime = LocalTime.of(15, 30);
            ReservationTimeCreateRequest requestTime = new ReservationTimeCreateRequest(reservationTime);

            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(requestTime)
                    .when().post("/times")
                    .then().log().all()
                    .statusCode(201);

            ThemeCreateRequest themeCreateRequest = new ThemeCreateRequest("테마1", "설명1", "url");
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(themeCreateRequest)
                    .when().post("/themes")
                    .then().log().all()
                    .statusCode(201);

            AdminReservationCreateRequest dto = new AdminReservationCreateRequest(
                    LocalDate.now().plusDays(1), 1L, 1L, 1L);
            RestAssured.given().cookie("token", loginToken).log().all()
                    .contentType(ContentType.JSON)
                    .body(dto)
                    .when().post("/admin/reservations")
                    .then().log().all().statusCode(201);

            RestAssured.given().cookie("token", loginToken).log().all()
                    .when().delete("/reservations/1")
                    .then().log().all()
                    .statusCode(204);
        }


        @DisplayName("존재하지 않는 예약을 삭제할 수 없다")
        @Test
        void invalidReservationIdDeleteTest() {
            RestAssured.given().cookie("token", loginToken).log().all()
                    .when().delete("/reservations/5")
                    .then().log().all()
                    .statusCode(404);
        }
    }
}
