package roomescape.util;

import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import org.springframework.restdocs.restassured.RestDocumentationFilter;

public enum RestDocsFilter {

    SIGN_UP(
            document("signup",
                    requestFields(
                            fieldWithPath("name").description("회원 이름"),
                            fieldWithPath("email").description("회원 이메일"),
                            fieldWithPath("password").description("회원 비밀번호")
                    ),
                    responseHeaders(
                            headerWithName("Location").description("리소스 URI")
                    )
            )
    ),

    LOGIN(
            document("login",
                    requestFields(
                            fieldWithPath("email").description("로그인 할 회원 이메일"),
                            fieldWithPath("password").description("로그인 할 회원 비밀번호")
                    ),
                    responseCookies(cookieWithName("token").description("로그인 된 사용자 토큰")
                    )
            )
    ),

    LOGOUT(
            document("logout",
                    requestCookies(cookieWithName("token").description("로그인 된 사용자 토큰")),
                    responseCookies(cookieWithName("token").description("로그아웃 된 사용자 토큰"))
            )
    ),

    CREATE_RESERVATION_BY_USER(
            document("createReservationByUser",
                    requestCookies(cookieWithName("token").description("로그인 된 사용자 토큰")),
                    requestFields(
                            fieldWithPath("memberId").description("예약할 회원 식별자"),
                            fieldWithPath("date").description("예약 날짜"),
                            fieldWithPath("themeId").description("예약할 테마 식별자"),
                            fieldWithPath("timeId").description("예약할 시간 식별자"),
                            fieldWithPath("paymentKey").description("토스 결제를 위한 고유 key"),
                            fieldWithPath("orderId").description("토스 결제를 위한 고유 id"),
                            fieldWithPath("amount").description("결제 금액")
                    ),
                    responseHeaders(
                            headerWithName("Location").description("리소스 URI")
                    ),
                    responseFields(
                            fieldWithPath("id").description("생성된 예약 식별자"),
                            fieldWithPath("member.id").description("예약자 식별자"),
                            fieldWithPath("member.name").description("예약자 이름"),
                            fieldWithPath("date").description("예약된 날짜"),
                            fieldWithPath("theme.id").description("예약된 테마 식별자"),
                            fieldWithPath("theme.name").description("예약된 테마 이름"),
                            fieldWithPath("theme.description").description("예약된 테마 설명"),
                            fieldWithPath("theme.thumbnail").description("예약된 테마 썸네일"),
                            fieldWithPath("time.id").description("예약된 시간 식별자"),
                            fieldWithPath("time.startAt").description("예약된 시간")
                    )
            )
    ),

    CANCEL_RESERVATION_BY_USER(
            document("cancelReservationByUser",
                    requestCookies(cookieWithName("token").description("로그인 된 사용자 토큰")),
                    pathParameters(
                            parameterWithName("id").description("취소할 예약 식별자")
                    )
            )
    ),

    GET_MY_RESERVATION(
            document("getMyReservation",
                    requestCookies(cookieWithName("token").description("로그인 된 사용자 토큰")),
                    responseFields(
                            fieldWithPath("responses[].reservationId").description("예약 식별번호"),
                            fieldWithPath("responses[].theme").description("예약된 테마 이름"),
                            fieldWithPath("responses[].date").description("예약 날짜"),
                            fieldWithPath("responses[].time").description("예약 시간"),
                            fieldWithPath("responses[].status").description("예약 상태"),
                            fieldWithPath("responses[].rank").description("예약 순위"),
                            fieldWithPath("responses[].paymentKey").description("예약 결제 정보"),
                            fieldWithPath("responses[].totalAmount").description("예약 금액")
                    )

            )
    ),

    CREATE_WAITING(
            document("createWaiting",
                    requestCookies(cookieWithName("token").description("로그인 된 사용자 토큰")),
                    requestFields(
                            fieldWithPath("memberId").description("예약 대기할 회원 식별자"),
                            fieldWithPath("date").description("예약 대기 날짜"),
                            fieldWithPath("themeId").description("예약 대기할 테마 식별자"),
                            fieldWithPath("timeId").description("예약 대기할 시간 식별자"),
                            fieldWithPath("paymentKey").description("토스 결제를 위한 고유 key").ignored(),
                            fieldWithPath("orderId").description("토스 결제를 위한 고유 id").ignored(),
                            fieldWithPath("amount").description("결제 금액").ignored()
                    ),
                    responseHeaders(
                            headerWithName("Location").description("리소스 URI")
                    ),
                    responseFields(
                            fieldWithPath("id").description("생성된 예약 대기 식별자"),
                            fieldWithPath("member.id").description("예약 대기자 식별자"),
                            fieldWithPath("member.name").description("예약 대기자 이름"),
                            fieldWithPath("date").description("예약 대기된 날짜"),
                            fieldWithPath("theme.id").description("예약 대기된 테마 식별자"),
                            fieldWithPath("theme.name").description("예약 대기된 테마 이름"),
                            fieldWithPath("theme.description").description("예약 대기된 테마 설명"),
                            fieldWithPath("theme.thumbnail").description("예약 대기된 테마 썸네일"),
                            fieldWithPath("time.id").description("예약 대기된 시간 식별자"),
                            fieldWithPath("time.startAt").description("예약 대기된 시간")
                    )
            )
    ),

    DELETE_WAITING(
            document("deleteWaiting",
                    requestCookies(cookieWithName("token").description("로그인 된 사용자 토큰")),
                    pathParameters(
                            parameterWithName("id").description("삭제할 예약 대기 식별자")
                    )
            )
    ),

    GET_ENTIRE_MEMBERS(
            document("getEntireMembers",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    responseFields(
                            fieldWithPath("responses[].id").description("회원 식별번호"),
                            fieldWithPath("responses[].name").description("회원 이름")
                    )
            )
    ),

    GET_ENTIRE_RESERVATIONS(
            document("getEntireReservations",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    pathParameters(parameterWithName("reservationStatus").description("조회할 예약 상태").optional()),
                    responseFields(
                            fieldWithPath("responses[].id").description("예약 식별번호"),
                            fieldWithPath("responses[].member.id").description("예약 대기자 식별자"),
                            fieldWithPath("responses[].member.name").description("예약 대기자 이름"),
                            fieldWithPath("responses[].date").description("예약 대기된 날짜"),
                            fieldWithPath("responses[].theme.id").description("예약 대기된 테마 식별자"),
                            fieldWithPath("responses[].theme.name").description("예약 대기된 테마 이름"),
                            fieldWithPath("responses[].theme.description").description("예약 대기된 테마 설명"),
                            fieldWithPath("responses[].theme.thumbnail").description("예약 대기된 테마 썸네일"),
                            fieldWithPath("responses[].time.id").description("예약 대기된 시간 식별자"),
                            fieldWithPath("responses[].time.startAt").description("예약 대기된 시간")
                    )
            )
    ),

    SEARCH_RESERVATIONS(
            document("searchReservations",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    queryParameters(
                            parameterWithName("themeId").description("검색할 테마 식별자"),
                            parameterWithName("memberId").description("검색할 회원 식별자"),
                            parameterWithName("dateFrom").description("검색할 시작 기간"),
                            parameterWithName("dateTo").description("검색할 종료 기간")
                    ),
                    responseFields(
                            fieldWithPath("responses[].id").description("예약 식별번호"),
                            fieldWithPath("responses[].member.id").description("예약 대기자 식별자"),
                            fieldWithPath("responses[].member.name").description("예약 대기자 이름"),
                            fieldWithPath("responses[].date").description("예약 대기된 날짜"),
                            fieldWithPath("responses[].theme.id").description("예약 대기된 테마 식별자"),
                            fieldWithPath("responses[].theme.name").description("예약 대기된 테마 이름"),
                            fieldWithPath("responses[].theme.description").description("예약 대기된 테마 설명"),
                            fieldWithPath("responses[].theme.thumbnail").description("예약 대기된 테마 썸네일"),
                            fieldWithPath("responses[].time.id").description("예약 대기된 시간 식별자"),
                            fieldWithPath("responses[].time.startAt").description("예약 대기된 시간")
                    )
            )
    ),

    GET_ENTIRE_WAITINGS(
            document("getEntireWaitings",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    responseFields(
                            fieldWithPath("responses[].id").description("예약 대기 식별번호"),
                            fieldWithPath("responses[].memberName").description("예약 대기자 이름"),
                            fieldWithPath("responses[].themeName").description("예약 대기 테마 이름"),
                            fieldWithPath("responses[].date").description("예약 대기 날짜"),
                            fieldWithPath("responses[].time").description("예약 대기 시간")
                    )
            )
    ),

    CREATE_RESERVATION_BY_ADMIN(
            document("createReservationByAdmin",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    requestFields(
                            fieldWithPath("memberId").description("예약할 회원 식별자"),
                            fieldWithPath("date").description("예약 날짜"),
                            fieldWithPath("themeId").description("예약할 테마 식별자"),
                            fieldWithPath("timeId").description("예약할 시간 식별자"),
                            fieldWithPath("paymentKey").description("토스 결제를 위한 고유 key").ignored(),
                            fieldWithPath("orderId").description("토스 결제를 위한 고유 id").ignored(),
                            fieldWithPath("amount").description("결제 금액").ignored()
                    ),
                    responseHeaders(
                            headerWithName("Location").description("리소스 URI")
                    ),
                    responseFields(
                            fieldWithPath("id").description("생성된 예약 식별자"),
                            fieldWithPath("member.id").description("예약자 식별자"),
                            fieldWithPath("member.name").description("예약자 이름"),
                            fieldWithPath("date").description("예약된 날짜"),
                            fieldWithPath("theme.id").description("예약된 테마 식별자"),
                            fieldWithPath("theme.name").description("예약된 테마 이름"),
                            fieldWithPath("theme.description").description("예약된 테마 설명"),
                            fieldWithPath("theme.thumbnail").description("예약된 테마 썸네일"),
                            fieldWithPath("time.id").description("예약된 시간 식별자"),
                            fieldWithPath("time.startAt").description("예약된 시간")
                    )
            )
    ),
    CANCEL_RESERVATION_BY_ADMIN(
            document("cancelReservationByAdmin",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    pathParameters(
                            parameterWithName("id").description("취소할 예약 식별자")
                    )

            )
    ),

    DELETE_WAITING_BY_ADMIN(
            document("deleteWaitingByAdmin",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    pathParameters(
                            parameterWithName("id").description("삭제할 예약 대기 식별자")
                    )
            )
    ),

    GET_POPULAR_THEMES(
            document("getPopularThemes",
                    queryParameters(
                            parameterWithName("period").description("인기 테마 조회 기간").optional(),
                            parameterWithName("limitCount").description("인기 테마 조회 갯수").optional()
                    ),
                    responseFields(
                            fieldWithPath("responses[].name").description("테마 이름"),
                            fieldWithPath("responses[].description").description("테마 설명"),
                            fieldWithPath("responses[].thumbnail").description("테마 썸네일")
                    )
            )
    ),

    GET_ENTIRE_THEMES(
            document("getEntireThemes",
                    responseFields(
                            fieldWithPath("responses[].id").description("테마 식별자"),
                            fieldWithPath("responses[].name").description("테마 이름"),
                            fieldWithPath("responses[].description").description("테마 설명"),
                            fieldWithPath("responses[].thumbnail").description("테마 썸네일")
                    )
            )
    ),

    CREATE_THEME(
            document("createTheme",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    requestFields(
                            fieldWithPath("name").description("테마 이름"),
                            fieldWithPath("description").description("테마 설명"),
                            fieldWithPath("thumbnail").description("테마 썸네일")
                    ),
                    responseHeaders(
                            headerWithName("Location").description("리소스 URI")
                    ),
                    responseFields(
                            fieldWithPath("id").description("테마 식별자"),
                            fieldWithPath("name").description("테마 이름"),
                            fieldWithPath("description").description("테마 설명"),
                            fieldWithPath("thumbnail").description("테마 썸네일")
                    )
            )
    ),

    DELETE_THEMES(
            document("deleteTheme",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    pathParameters(
                            parameterWithName("id").description("삭제할 테마 식별자")
                    )
            )
    ),

    GET_ENTIRE_RESERVATION_TIME(
            document("getEntireReservationTime",
                    responseFields(
                            fieldWithPath("responses[].id").description("예약 시간 식별자"),
                            fieldWithPath("responses[].startAt").description("예약 시간")
                    )
            )
    ),

    GET_AVAILABLE_RESERVATION_TIME(
            document("getAvailableReservationTime",
                    queryParameters(
                            parameterWithName("date").description("예약하려는 날짜"),
                            parameterWithName("theme-id").description("예약하려는 테마의 식별자")
                    ),
                    responseFields(
                            fieldWithPath("responses[].id").description("예약 시간 식별자"),
                            fieldWithPath("responses[].startAt").description("예약 시간"),
                            fieldWithPath("responses[].booked").description("해당 시간 예약 여부")
                    )
            )
    ),

    CREATE_RESERVATION_TIME(
            document("createReservationTime",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    requestFields(
                            fieldWithPath("startAt").description("예약 시간")
                    ),
                    responseHeaders(
                            headerWithName("Location").description("리소스 URI")
                    ),
                    responseFields(
                            fieldWithPath("id").description("예약 시간 식별자"),
                            fieldWithPath("startAt").description("예약 시간")
                    )
            )
    ),

    DELETE_RESERVATION_TIME(
            document("deleteReservationTime",
                    requestCookies(cookieWithName("token").description("로그인 된 관리자 토큰")),
                    pathParameters(
                            parameterWithName("id").description("삭제할 예약 시간 식별자")
                    )
            )
    );

    private RestDocumentationFilter filter;

    RestDocsFilter(RestDocumentationFilter filter) {
        this.filter = filter;
    }

    public RestDocumentationFilter getFilter() {
        return filter;
    }
}
