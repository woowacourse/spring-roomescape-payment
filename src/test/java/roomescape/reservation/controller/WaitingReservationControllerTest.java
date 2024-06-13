package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.Fixture.KAKI_EMAIL;
import static roomescape.Fixture.KAKI_NAME;
import static roomescape.Fixture.KAKI_PASSWORD;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.MEMBER_KAKI;
import static roomescape.common.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.common.util.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.payload.JsonFieldType;
import roomescape.auth.domain.Role;
import roomescape.common.config.ControllerTest;
import roomescape.common.util.CookieUtils;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberName;
import roomescape.reservation.controller.dto.request.WaitingReservationSaveRequest;

class WaitingReservationControllerTest extends ControllerTest {

    private static final String ROOT_IDENTIFIER = "waiting-reservation";

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("예약 대기 목록 조회에 성공하면 200 응답을 받는다.")
    @Test
    void findWaitings() {
        memberJdbcUtil.saveMemberAsKaki();
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();
        waitingJdbcUtil.saveWaitAsDateNow();

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .get("/reservations/wait")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("resources.$", hasSize(1));
    }

    @DisplayName("회원이 예약 대기를 성공적으로 추가하면 201 응답과 Location 헤더에 리소스 저장 경로를 받는다.")
    @Test
    void saveMemberWaitingReservation() throws JsonProcessingException {
        memberJdbcUtil.saveMember(MEMBER_JOJO);
        memberJdbcUtil.saveMember(MEMBER_KAKI);
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();
        reservationJdbcUtil.saveReservationAsDateNow();

        WaitingReservationSaveRequest saveRequest = new WaitingReservationSaveRequest(LocalDate.now(), 1L, 1L);
        String kakiToken = getToken(new Member(2L, Role.MEMBER, new MemberName(KAKI_NAME), KAKI_EMAIL, KAKI_PASSWORD));

        RestAssured.given(spec).log().all()
                .cookie(CookieUtils.TOKEN_KEY, kakiToken)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(saveRequest))
                .accept(ContentType.JSON)
                .filter(document(ROOT_IDENTIFIER + "/save",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestCookies(cookieWithName("token").description("로그인 유저 토큰")),
                        requestFields(
                                fieldWithPath("date").description("예약 날짜"),
                                fieldWithPath("themeId").description("테마 식별자"),
                                fieldWithPath("timeId").description("시간 식별자")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("식별자"),
                                fieldWithPath("member.id").type(JsonFieldType.NUMBER).description("회원 식별자"),
                                fieldWithPath("member.name").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("date").type(JsonFieldType.STRING).description("예약 날짜"),
                                fieldWithPath("theme.id").type(JsonFieldType.NUMBER).description("테마 식별자"),
                                fieldWithPath("theme.name").type(JsonFieldType.STRING).description("테마명"),
                                fieldWithPath("theme.description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("theme.thumbnail").type(JsonFieldType.STRING).description("테마 이미지 url"),
                                fieldWithPath("time.id").type(JsonFieldType.NUMBER).description("시간 식별자"),
                                fieldWithPath("time.startAt").type(JsonFieldType.STRING).description("시작 시간"),
                                fieldWithPath("status").type(JsonFieldType.STRING).description("예약 상태")
                        )
                ))
                .when()
                .post("/reservations/wait")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .header("Location", "/reservations/wait/1");
    }

    @DisplayName("예약 대기를 성공적으로 승인하면 204 응답을 받는다.")
    @Test
    void approveReservation() {
        memberJdbcUtil.saveMember(MEMBER_KAKI);
        themeJdbcUtil.saveThemeAsHorror();
        reservationTimeJdbcUtil.saveReservationTimeAsTen();
        waitingJdbcUtil.saveWaitAsDateNow();

        RestAssured.given().log().all()
                .cookie(CookieUtils.TOKEN_KEY, getMemberToken())
                .accept(ContentType.JSON)
                .when()
                .patch("/reservations/wait/{id}", 1L)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }
}
