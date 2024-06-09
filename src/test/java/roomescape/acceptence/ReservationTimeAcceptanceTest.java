package roomescape.acceptence;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import java.time.LocalDate;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

class ReservationTimeAcceptanceTest extends AcceptanceFixture {

    @Test
    @DisplayName("예약 시간을 저장한다.")
    void save_ShouldSaveReservationTime() {
        RestDocumentationFilter filter = document("time/save",
                requestCookies(
                        cookieWithName("token").description("일반 권한 사용자 토큰")
                ),
                requestFields(
                        fieldWithPath("startAt").description("예약 시간(분/초)")
                )
        );

        // given
        Map<String, String> requestBody = Map.of("startAt", "12:11");

        // when & then
        RestAssured
                .given(spec)
                .contentType(ContentType.JSON)
                .cookie(normalToken)
                .filter(filter)
                .body(requestBody)

                .when()
                .post("/times")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/times/1")
                .body("startAt", is("12:11"));
    }

    @Test
    @DisplayName("예약 시간을 삭제한다.")
    void delete_ShouldRemoveReservationTime_ByReservationId() {
        RestDocumentationFilter filter = document("time/delete",
                pathParameters(
                        parameterWithName("id").description("예약시간 식별자")
                ),
                requestCookies(
                        cookieWithName("token").description("일반 권한 사용자 토큰")
                )
        );

        // given
        createTimeRequest("12:11");
        createTimeRequest("13:11");

        // when
        RestAssured
                .given(spec)
                .accept(ContentType.JSON)
                .filter(filter)
                .cookie(normalToken)

                .when()
                .delete("/times/{id}", 1)

                .then()
                .statusCode(is(HttpStatus.SC_NO_CONTENT));

        // then
        RestAssured
                .given()
                .cookie(normalToken)
                .accept(ContentType.JSON)

                .when()
                .get("/times")

                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", is(1))
                .body("[0].id", is(2))
                .body("[0].startAt", is("13:11"));
    }

    @Test
    @DisplayName("모든 예약 시간을 조회한다.")
    void findAll_ShouldInquiryAllReservationTime() {
        RestDocumentationFilter filter = document("time/search",
                requestCookies(
                        cookieWithName("token").description("일반 권한 사용자 토큰")
                ),
                responseFields(
                        fieldWithPath("[].id").description("예약 시간 식별자"),
                        fieldWithPath("[].startAt").description("예약 시간")
                )
        );

        // given
        createTimeRequest("12:11");
        createTimeRequest("13:11");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .cookie(normalToken)
                .accept(ContentType.JSON)

                .when()
                .get("/times")

                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].startAt", is("12:11"))
                .body("[1].id", is(2))
                .body("[1].startAt", is("13:11"));
    }

    @Test
    @DisplayName("예약 가능한 모든 예약 시간을 조회한다.")
    void findAvailableTimes_ShouldInquiryAvailableAllReservationTime() {
        RestDocumentationFilter filter = document("time/available",
                requestCookies(
                        cookieWithName("token").description("일반 사용자 권한 토큰")
                ),
                queryParameters(
                        parameterWithName("date").description("조회할 날짜"),
                        parameterWithName("theme-id").description("조회할 테마 식별자")
                ),
                responseFields(
                        fieldWithPath("[].id").description("예약시간 식별자"),
                        fieldWithPath("[].startAt").description("시간"),
                        fieldWithPath("[].booked").description("예약 가능 상태(참/거짓)")
                )
        );

        // given
        String date = LocalDate.now().plusDays(1).toString();

        // 시간생성
        createTimeRequest("12:11");
        createTimeRequest("11:11");

        // 테마생성
        RestAssured
                .given()
                .cookie(normalToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("name", "theme1", "description", "desc", "thumbnail", "thumbnail"))

                .when()
                .post("/themes")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/themes/1")
                .body("id", is(1))
                .body("name", is("theme1"))
                .body("description", is("desc"))
                .body("thumbnail", is("thumbnail"));

        // 회원가입
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("name", "aa", "email", "aa@aa.aa", "password", "aa"))

                .when()
                .post("/members")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/members/1");

        // 로그인
        String token = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", "aa@aa.aa", "password", "aa"))

                .when()
                .post("/login")

                .thenReturn()
                .cookie("token");

        // 예약
        Map<String, String> requestBody = Map.of("date", date,
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        // 예약 생성
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(requestBody)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/1");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .accept(ContentType.JSON)
                .queryParam("date", date)
                .queryParam("theme-id", "1")
                .cookie(normalToken)

                .when()
                .get("/times/available")

                .then().log().all()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", is(2))
                .body("[0].id", is(1))
                .body("[0].startAt", is("12:11"))
                .body("[0].booked", is(true))
                .body("[1].id", is(2))
                .body("[1].startAt", is("11:11"))
                .body("[1].booked", is(false));
    }

    private void createTimeRequest(String date) {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie(normalToken)
                .body(Map.of("startAt", date))

                .when()
                .post("/times")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .body("startAt", is(date));
    }
}
