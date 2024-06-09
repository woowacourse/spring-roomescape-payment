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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;

class ReservationAcceptanceTest extends AcceptanceFixture {
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("모든 예약을 가져온다.")
    void findAll_ShouldReturnGetAllReservations() {
        RestDocumentationFilter filter = document("reservation/search",
                requestCookies(
                        cookieWithName("token").description("일반 사용자 권한 토큰")
                ),
                responseFields(
                        fieldWithPath("[].id").description("예약 식별자"),
                        fieldWithPath("[].member.id").description("예약 회원식별자"),
                        fieldWithPath("[].member.name").description("예약 회원명"),
                        fieldWithPath("[].date").description("예약 일자"),
                        fieldWithPath("[].theme.id").description("예약 테마 식별자"),
                        fieldWithPath("[].theme.name").description("예약 테마명"),
                        fieldWithPath("[].theme.description").description("예약 테마 설명"),
                        fieldWithPath("[].theme.thumbnail").description("예약 테마 썸네일 url"),
                        fieldWithPath("[].time.id").description("예약 시간 식별자"),
                        fieldWithPath("[].time.startAt").description("예약 시간")
                )
        );

        // given
        saveThemeRequest("name");
        saveMemberRequest("aa@aa.aa");
        saveTimeRequest("12:11");
        String token = loginRequest("aa@aa.aa", "aa");

        Map<String, String> requestBody1 = Map.of("date", LocalDate.now().plusDays(1).toString(),
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        Map<String, String> requestBody2 = Map.of("date", LocalDate.now().plusDays(2).toString(),
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(requestBody1)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/1");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(requestBody2)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/2");

        // when
        RestAssured
                .given(spec)
                .filter(filter)
                .cookie(normalToken)
                .accept(ContentType.JSON)

                .when()
                .get("/reservations")

                .then().log().all()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", is(2));
    }

    @Test
    @DisplayName("예약을 저장한다.")
    void save_ShouldSaveReservation() {
        RestDocumentationFilter filter = document("reservation/save",
                requestCookies(
                        cookieWithName("token").description("일반 권한 사용자")
                ),
                requestFields(
                        fieldWithPath("date").description("예약 날짜"),
                        fieldWithPath("themeId").description("테마 식별자"),
                        fieldWithPath("timeId").description("시간 식별자"),
                        fieldWithPath("amount").description("토스API 결제 금액"),
                        fieldWithPath("paymentKey").description("토스API 페이먼트 키"),
                        fieldWithPath("orderId").description("토스API 오더 아이디"),
                        fieldWithPath("paymentType").description("토스API 결제 타입")
                ),
                responseFields(
                        fieldWithPath("id").description("예약 식별자"),
                        fieldWithPath("member.id").description("회원 식별자"),
                        fieldWithPath("member.name").description("회원명"),
                        fieldWithPath("date").description("예약 날짜"),
                        fieldWithPath("theme.id").description("테마 식별자"),
                        fieldWithPath("theme.name").description("테마명"),
                        fieldWithPath("theme.description").description("테마 설명"),
                        fieldWithPath("theme.thumbnail").description("테마 썸네일 사진 url"),
                        fieldWithPath("time.id").description("예약 시간 식별자"),
                        fieldWithPath("time.startAt").description("예약 시간")
                )
        );

        // given
        saveThemeRequest("name");
        saveMemberRequest("aa@aa.aa");
        saveTimeRequest("12:11");
        String token = loginRequest("aa@aa.aa", "aa");

        Map<String, String> requestBody = Map.of("date", LocalDate.now().plusDays(1).toString(),
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        // when & then
        RestAssured
                .given(spec)
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .filter(filter)
                .body(requestBody)

                .when()
                .post("/reservations")

                .then().log().all()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/1");
    }

    @Test
    @DisplayName("예약을 저장한다.(어드민)")
    void saveAdminReservation_ShouldSaveReservation() {
        RestDocumentationFilter filter = document("reservation/save-admin",
                requestCookies(
                        cookieWithName("token").description("어드민 권한 사용자 토큰")
                ),
                requestFields(
                        fieldWithPath("date").description("예약 날짜"),
                        fieldWithPath("themeId").description("테마 식별자"),
                        fieldWithPath("timeId").description("시간 식별자")
                ),
                responseFields(
                        fieldWithPath("id").description("예약 식별자"),
                        fieldWithPath("member.id").description("회원 식별자"),
                        fieldWithPath("member.name").description("회원명"),
                        fieldWithPath("date").description("예약 날짜"),
                        fieldWithPath("theme.id").description("테마 식별자"),
                        fieldWithPath("theme.name").description("테마명"),
                        fieldWithPath("theme.description").description("테마 설명"),
                        fieldWithPath("theme.thumbnail").description("테마 썸네일 사진 url"),
                        fieldWithPath("time.id").description("예약 시간 식별자"),
                        fieldWithPath("time.startAt").description("예약 시간")
                )
        );

        // given
        Member admin = memberRepository.save(new Member(1L, Role.ADMIN, "admin", "admin@email.com", "admin"));
        saveThemeRequest("name");
        saveTimeRequest("12:11");
        String token = loginRequest(admin.getEmail(), admin.getPassword());

        Map<String, String> requestBody = Map.of("date", LocalDate.now().plusDays(1).toString(),
                "themeId", "1",
                "timeId", "1");

        // when & then
        RestAssured
                .given(spec)
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .filter(filter)
                .body(requestBody)

                .when()
                .post("/admin/reservations")

                .then().log().all()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/1");
    }

    @Test
    @DisplayName("조건에 따른 예약을 조회한다.")
    void findAllBySearchCond() {
        RestDocumentationFilter filter = document("reservation/search-cond",
                requestCookies(
                        cookieWithName("token").description("일반 사용자 권한 토큰")
                ),
                queryParameters(
                        parameterWithName("memberId").description("회원 식별자"),
                        parameterWithName("themeId").description("테마 식별자"),
                        parameterWithName("dateFrom").description("조회 시작 날짜"),
                        parameterWithName("dateTo").description("조회 마지막 날짜")
                ),
                responseFields(
                        fieldWithPath("[].id").description("예약 식별자"),
                        fieldWithPath("[].member.id").description("예약 회원식별자"),
                        fieldWithPath("[].member.name").description("예약 회원명"),
                        fieldWithPath("[].date").description("예약 일자"),
                        fieldWithPath("[].theme.id").description("예약 테마 식별자"),
                        fieldWithPath("[].theme.name").description("예약 테마명"),
                        fieldWithPath("[].theme.description").description("예약 테마 설명"),
                        fieldWithPath("[].theme.thumbnail").description("예약 테마 썸네일 url"),
                        fieldWithPath("[].time.id").description("예약 시간 식별자"),
                        fieldWithPath("[].time.startAt").description("예약 시간")
                )
        );

        // given
        saveThemeRequest("name");
        saveMemberRequest("aa@aa.aa");
        saveTimeRequest("12:11");
        String token = loginRequest("aa@aa.aa", "aa");

        String date1 = LocalDate.now().plusDays(1).toString();
        Map<String, String> requestBody1 = Map.of("date", date1,
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        String date2 = LocalDate.now().plusDays(2).toString();
        Map<String, String> requestBody2 = Map.of("date", date2,
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(requestBody1)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/1");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(requestBody2)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/2");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .accept(ContentType.JSON)
                .cookie(normalToken)
                .queryParam("memberId", 1)
                .queryParam("themeId", 1)
                .queryParam("dateFrom", date1)
                .queryParam("dateTo", date1)

                .when()
                .get("/reservations/search")

                .then().log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", is(1))
                .body("[0].id", is(1));
    }


    @Test
    @DisplayName("모든 예약 대기를 조회한다.")
    void findWaiting_ShouldInquiryAllReservationWait() {
        RestDocumentationFilter filter = document("reservation/waits-search",
                requestCookies(
                        cookieWithName("token").description("일반 사용자 권한 토큰")
                ),
                responseFields(
                        fieldWithPath("[].id").description("예약 식별자"),
                        fieldWithPath("[].name").description("예약자명"),
                        fieldWithPath("[].theme").description("테마명"),
                        fieldWithPath("[].date").description("예약 일자"),
                        fieldWithPath("[].startAt").description("예약 시간")
                )
        );

        // given
        saveThemeRequest("name");
        saveMemberRequest("aa@aa.aa");
        saveMemberRequest("bb@bb.bb");
        saveMemberRequest("cc@cc.cc");
        saveTimeRequest("12:11");
        String token1 = loginRequest("aa@aa.aa", "aa");
        String token2 = loginRequest("bb@bb.bb", "aa");
        String token3 = loginRequest("cc@cc.cc", "aa");
        String date1 = LocalDate.now().plusDays(1).toString();
        Map<String, String> saveRequestBody = Map.of("date", date1,
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token1)
                .body(saveRequestBody)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/1");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token2)
                .body(saveRequestBody)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/2");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token3)
                .body(saveRequestBody)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/3");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .accept(ContentType.JSON)
                .cookie(normalToken)

                .when()
                .get("/reservations/waiting")

                .then().log().all()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", is(2))
                .body("[0].id", is(2))
                .body("[1].id", is(3));
    }

    @Test
    @DisplayName("예약대기을 삭제한다.")
    void delete_ShouldRemoveReservation() {
        RestDocumentationFilter filter = document("reservation/delete",
                pathParameters(
                        parameterWithName("id").description("예약 식별자")
                ),
                requestCookies(
                        cookieWithName("token").description("일반 권한 사용자 토큰")
                )
        );
        // given
        saveThemeRequest("name");
        saveMemberRequest("aa@aa.aa");
        saveMemberRequest("bb@bb.bb");
        saveTimeRequest("12:11");
        String token = loginRequest("aa@aa.aa", "aa");
        String token2 = loginRequest("bb@bb.bb", "aa");
        String date = LocalDate.now().plusDays(1).toString();

        Map<String, String> saveRequestBody = Map.of("date", date,
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token)
                .body(saveRequestBody)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/1");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token2)
                .body(saveRequestBody)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/reservations/2");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .cookie(normalToken)

                .when()
                .delete("/reservations/{id}", 2)

                .then().log().all()
                .statusCode(is(HttpStatus.SC_NO_CONTENT));
    }

    @Test
    @DisplayName("자신의 모든 예약 정보를 가져온다.")
    void myReservations_ShouldInquiryAllReservations() {
        RestDocumentationFilter filter = document("reservation/mine",
                requestCookies(
                        cookieWithName("token").description("일반 사용자 권한 토큰")
                ),
                responseFields(
                        fieldWithPath("[].id").description("예약 식별자"),
                        fieldWithPath("[].theme").description("테마명"),
                        fieldWithPath("[].date").description("예약 일자"),
                        fieldWithPath("[].time").description("예약 시간"),
                        fieldWithPath("[].status").description("예약 상태"),
                        fieldWithPath("[].order").description("예약 대기 순위 (0은 예약완료)"),
                        fieldWithPath("[].paymentKey").description("토스API 페이먼트 키"),
                        fieldWithPath("[].amount").description("토스API 결제금액")
                )
        );

        // given
        saveThemeRequest("name");
        saveMemberRequest("aa@aa.aa");
        saveMemberRequest("bb@bb.bb");
        saveTimeRequest("12:11");
        String token1 = loginRequest("aa@aa.aa", "aa");
        String token2 = loginRequest("bb@bb.bb", "aa");
        String date1 = LocalDate.now().plusDays(1).toString();
        String date2 = LocalDate.now().plusDays(2).toString();

        Map<String, String> saveRequestBody1 = Map.of("date", date1,
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");
        Map<String, String> saveRequestBody2 = Map.of("date", date2,
                "themeId", "1",
                "timeId", "1",
                "paymentKey", "payment",
                "orderId", "orderId",
                "amount", "1000",
                "paymentType", "type");

        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token1)
                .body(saveRequestBody1)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED));
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token2)
                .body(saveRequestBody2)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED));
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token2)
                .body(saveRequestBody1)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED));
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie("token", token1)
                .body(saveRequestBody2)

                .when()
                .post("/reservations")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED));

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .cookie("token", token2)
                .accept(ContentType.JSON)

                .when()
                .get("/reservations/me")

                .then().log().all()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", is(2))
                .body("[0].id", is(2))
                .body("[0].status", is("예약"))
                .body("[1].id", is(3))
                .body("[1].status", is("대기"));
    }

    private void saveTimeRequest(String startAt) {
        RestAssured
                .given()
                .contentType(ContentType.JSON)
                .cookie(adminToken)
                .body(Map.of("startAt", startAt))

                .when()
                .post("/times")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/times/1")
                .body("startAt", is("12:11"));
    }

    private String loginRequest(String email, String password) {
        return RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(Map.of("email", email, "password", password))

                .when()
                .post("/login")

                .thenReturn().cookie("token");
    }

    private void saveMemberRequest(String email) {
        RestAssured
                .given()
                .cookie(adminToken)
                .contentType(ContentType.JSON)
                .body(Map.of("name", "aa", "email", email, "password", "aa"))

                .when()
                .post("/members")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED));
    }

    private void saveThemeRequest(String themeName) {
        RestAssured
                .given(spec)
                .cookie(adminToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("name", themeName, "description", "desc", "thumbnail", "thumbnail"))

                .when()
                .post("/themes")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/themes/1")
                .body("id", is(1))
                .body("name", is(themeName))
                .body("description", is("desc"))
                .body("thumbnail", is("thumbnail"));
    }
}
