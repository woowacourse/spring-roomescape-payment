package roomescape.controller.doc;

import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.restdocs.snippet.Snippet;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;

public enum DocumentFilter {
    SIGN_UP("signUp",
            requestFields(
                    fieldWithPath("email").description("회원 가입 할 사용자의 이메일"),
                    fieldWithPath("password").description("회원 가입 할 사용자의 비밀번호"),
                    fieldWithPath("name").description("회원 가입 할 사용자의 이름")
            )
    ),
    SING_UP_FAIL("signUpFail",
            requestFields(
                    fieldWithPath("email").description("회원 가입 할 사용자의 이메일"),
                    fieldWithPath("password").description("회원 가입 할 사용자의 비밀번호"),
                    fieldWithPath("name").description("회원 가입 할 사용자의 이름")
            )
    ),
    LOGIN("login",
            requestFields(
                    fieldWithPath("email").description("로그인 할 사용자의 이메일"),
                    fieldWithPath("password").description("로그인 할 사용자의 비밀번호")
            ),
            responseCookies(cookieWithName("token").description("로그인된 사용자의 토큰"))
    ),
    LOGOUT("logout",
            requestCookies(cookieWithName("token").description("유효한 사용자 토큰")),
            responseCookies(cookieWithName("token").description("토큰 제거를 위한 헤더"))
    ),
    ADMIN_SAVE_RESERVATION("adminSaveReservation",
            requestCookies(cookieWithName("token").description("예약을 생성하는 사용자의 토큰")),
            requestFields(
                    fieldWithPath("date").description("방탈출 예약 날짜"),
                    fieldWithPath("timeId").description("방탈출 예약 시간 ID"),
                    fieldWithPath("themeId").description("방탈출 예약 테마 ID"),
                    fieldWithPath("memberId").description("예약한 사용자 ID")
            )
    ),
    USER_SAVE_RESERVATION("userSaveReservation",
            requestCookies(cookieWithName("token").description("예약을 생성하는 사용자의 토큰")),
            requestFields(
                    fieldWithPath("date").description("방탈출 예약 날짜"),
                    fieldWithPath("timeId").description("방탈출 예약 시간 ID"),
                    fieldWithPath("themeId").description("방탈출 예약 테마 ID"),
                    fieldWithPath("memberId").description("예약한 사용자 ID"),
                    fieldWithPath("paymentKey").description("예약 결제의 paymentKey"),
                    fieldWithPath("orderId").description("예약 결제의 orderId"),
                    fieldWithPath("amount").description("결제된 금액")
            )
    ),
    SAVE_TIME("saveTime",
            requestCookies(cookieWithName("token").description("시간을 생성하는 어드민의 토큰")),
            requestFields(
                    fieldWithPath("startAt").description("저장할 시간")
            ),
            responseFields(
                    fieldWithPath("id").description("저장된 시간 ID"),
                    fieldWithPath("startAt").description("저장된 시간"),
                    fieldWithPath("booked").description("예약 여부")
            )
    ),
    SAVE_THEME("saveTheme",
            requestCookies(cookieWithName("token").description("테마를 생성하는 어드민의 토큰")),
            requestFields(
                    fieldWithPath("name").description("저장할 테마 이름"),
                    fieldWithPath("description").description("저장할 테마 설명"),
                    fieldWithPath("thumbnail").description("저장할 테마 썸네일")
            ),
            responseFields(
                    fieldWithPath("id").description("저장된 테마 ID"),
                    fieldWithPath("name").description("저장된 테마 이름"),
                    fieldWithPath("description").description("저장된 테마 설명"),
                    fieldWithPath("thumbnail").description("저장된 테마 썸네일")
            )
    ),
    GET_RESERVATIONS("getReservations",
            requestCookies(cookieWithName("token").description("로그인된 사용자의 토큰")),
            responseFields(
                    fieldWithPath("[].id").description("방탈출 예약 ID"),
                    fieldWithPath("[].member.name").description("방탈출 예약 멤버의 이름"),
                    fieldWithPath("[].date").description("방탈출 예약 날짜"),
                    fieldWithPath("[].time.id").description("방탈출 예약 시간 ID"),
                    fieldWithPath("[].time.startAt").description("방탈출 예약 시작 시간"),
                    fieldWithPath("[].time.booked").description("방탈출 예약 여부"),
                    fieldWithPath("[].theme.name").description("방탈출 예약 테마의 이름")
            )
    ),
    GET_TIMES("getTimes",
            requestCookies(cookieWithName("token").description("로그인된 사용자의 토큰")),
            responseFields(
                    fieldWithPath("[].id").description("방탈출 예약 시간 ID"),
                    fieldWithPath("[].startAt").description("방탈출 예약 시작 시간")
            )
    ),
    GET_THEMES("getThemes",
            requestCookies(cookieWithName("token").description("로그인된 사용자의 토큰")),
            responseFields(
                    fieldWithPath("[].id").description("방탈출 테마 ID"),
                    fieldWithPath("[].name").description("방탈출 테마 이름"),
                    fieldWithPath("[].description").description("방탈출 테마 설명"),
                    fieldWithPath("[].thumbnail").description("방탈출 테마 썸네일 URL")
            )
    ),
    GET_MEMBERS("getMembers",
            requestCookies(cookieWithName("token").description("로그인된 사용자의 토큰")),
            responseFields(
                    fieldWithPath("[].id").description("회원 ID"),
                    fieldWithPath("[].name").description("회원 이름")
            )
    ),
    DELETE_RESERVATION("deleteReservation",
            requestCookies(cookieWithName("token").description("어드민 토큰"))
    ),
    DELETE_TIME("deleteTime",
            requestCookies(cookieWithName("token").description("어드민 토큰"))
    ),
    DELETE_THEME("deleteTheme",
            requestCookies(cookieWithName("token").description("어드민 토큰"))
    ),
    DELETE_RESERVATION_FAIL("deleteReservationFail",
            requestCookies(cookieWithName("token").description("어드민 토큰"))
    ),
    DELETE_TIME_FAIL("deleteTimeFail",
            requestCookies(cookieWithName("token").description("어드민 토큰"))
    ),
    DELETE_THEME_FAIL("deleteThemeFail",
            requestCookies(cookieWithName("token").description("어드민 토큰"))
    ),
    ;

    private final RestDocumentationFilter value;

    DocumentFilter(String identifier, Snippet... snippets) {
        this.value = RestAssuredRestDocumentation.document(identifier, snippets);
    }

    public RestDocumentationFilter getValue() {
        return value;
    }
}
