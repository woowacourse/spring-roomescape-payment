package roomescape.exception;

import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultMember_1;
import static roomescape.TestFixture.createDefaultTheme;
import static roomescape.TestFixture.createTimeAt_10;

import io.restassured.http.ContentType;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;

public class ExceptionControllerTest extends IntegrationTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @DisplayName("인증이 필요한 api를 인증 없이 호출 시 예외")
    @Test
    void unauthorizedException() {
        givenWithDocs("401-exception")
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("관리자 권한이 필요한 api를 일반 유저가 호출 시 예외")
    @Test
    void forbiddenException() {
        // given
        Member member = dbHelper.insertMember(createDefaultMember_1());
        String token = jwtTokenProvider.createToken(createClaims(member));

        // when & then
        givenWithDocs("403-exception")
                .cookie("token", token)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("비즈니스 규칙 상의 예외 - 과거 일시로 예약 생성")
    @Test
    void businessException() {
        // given
        Member member1 = dbHelper.insertMember(createDefaultMember_1());
        ReservationTime reservationTimeAt10 = dbHelper.insertTime(createTimeAt_10());
        Theme theme1 = dbHelper.insertTheme(createDefaultTheme());
        String token = jwtTokenProvider.createToken(createClaims(member1));

        LocalDate pastDate = LocalDate.now().minusDays(1);
        ReservationRequest reservationRequest = new ReservationRequest(
                pastDate,
                reservationTimeAt10.getId(),
                theme1.getId()
        );

        // when & then
        givenWithDocs("400-exception")
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(reservationRequest)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 리소스 요청 시 404 예외")
    @Test
    void notFoundException() {
        Member member1 = dbHelper.insertMember(createDefaultMember_1());
        String token = jwtTokenProvider.createToken(createClaims(member1));

        // when & then
        long nonExistId = 999L;
        givenWithDocs("400-exception")
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .when().get("/reservations/" + nonExistId)
                .then().log().all()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("그 외 서버 내부 오류 시 500 예외")
    @Test
    void internalServerErrorException() {
        givenWithDocs("500-exception")
                .when().get("/test-error/500")
                .then().log().all()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
