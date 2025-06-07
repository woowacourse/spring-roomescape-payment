package roomescape.presentation.rest;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.Map;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

class AdminControllerTest {

    @Nested
    @DisplayName("어드민 권한으로 예약을 추가한다.")
    class CreateReservationWithAdminPrivileges extends RestDocsTestBase {

        @Test
        @DisplayName("id를 포함한 예약 내용과 CREATED를 응답한다")
        void createReservationWithAdminPrivileges() {
            Map<String, String> reservationBody = Map.of(
                    "date", "3000-03-17",
                    "timeId", "1",
                    "themeId", "1",
                    "userId", "2"
            );

            String token = getAdminToken();

            RestAssured.given(spec).filter(createReservationWithAdminPrivileges_document()).log().all()
                    .contentType(ContentType.JSON)
                    .cookie("token", token)
                    .body(reservationBody)
                    .when().post("/admin/reservations")
                    .then().log().all()
                    .statusCode(HttpStatus.CREATED.value())
                    .body("date", Matchers.equalTo("3000-03-17"));

        }

        RestDocumentationFilter createReservationWithAdminPrivileges_document() {
            FieldDescriptor[] requestFields = {
                    fieldWithPath("date").description("예약 날짜 (YYYY-MM-DD)"),
                    fieldWithPath("timeId").description("시간 ID"),
                    fieldWithPath("themeId").description("테마 ID"),
                    fieldWithPath("memberId").description("사용자 ID"),
                    fieldWithPath("paymentKey").description("결제 요청 key"),
                    fieldWithPath("orderId").description("주문 ID"),
                    fieldWithPath("amount").description("결제 금액")
            };

            FieldDescriptor[] responseFields = {
                    fieldWithPath("id").description("예약 ID"),
                    fieldWithPath("user").description("예약 사용자 정보"),
                    fieldWithPath("user.id").description("사용자 ID"),
                    fieldWithPath("user.name").description("사용자 이름"),
                    fieldWithPath("date").description("예약 날짜"),
                    fieldWithPath("time").description("시간 정보"),
                    fieldWithPath("time.id").description("예약 시간 ID"),
                    fieldWithPath("time.startAt").description("방탈출 예약 시간"),
                    fieldWithPath("theme").description("테마 정보"),
                    fieldWithPath("theme.id").description("테마 정보"),
                    fieldWithPath("theme.name").description("테마 정보"),
                    fieldWithPath("theme.description").description("테마 정보"),
                    fieldWithPath("theme.thumbnail").description("테마 정보")
            };

            CookieDescriptor[] cookieFields = {cookieWithName("token").description("사용자 인증 토큰")};

            return document(
                    "reservation-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(requestFields),
                    responseFields(responseFields),
                    requestCookies(cookieFields)
            );
        }
    }

    @Nested
    @DisplayName("어드민 권한으로 유저를 조회한다.")
    class ReadAllUsers extends RestDocsTestBase {

        @Test
        @DisplayName("존재하는 유저들과 OK를 응답한다.")
        void readAllUsers() {
            var token = getAdminToken();

            RestAssured.given(spec).filter(readAllUsers_document()).log().all()
                    .cookie("token", token)
                    .when().get("/admin/users")
                    .then().log().all()
                    .statusCode(HttpStatus.OK.value())
                    .body("size()", Matchers.is(3));
        }

        RestDocumentationFilter readAllUsers_document() {
            FieldDescriptor[] responseFields = {
                    fieldWithPath("[]").description("조회된 사용자 목록"),
                    fieldWithPath("[].id").description("사용자 ID"),
                    fieldWithPath("[].name").description("사용자 이름"),
            };

            CookieDescriptor[] cookieFields = {cookieWithName("token").description("사용자 인증 토큰")};

            return document(
                    "reservation-create",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(responseFields),
                    requestCookies(cookieFields)
            );
        }
    }
}
