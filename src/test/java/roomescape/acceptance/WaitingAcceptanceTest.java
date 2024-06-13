package roomescape.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import roomescape.dto.reservation.MemberReservationSaveRequest;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.FieldDescriptorFixture.memberReservationFieldDescriptor;
import static roomescape.FieldDescriptorFixture.memberReservationSaveFieldDescriptor;
import static roomescape.FieldDescriptorFixture.tokenCookieDescriptor;
import static roomescape.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.TestFixture.MEMBER_CAT_EMAIL;

public class WaitingAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("[사용자] 대기 생성에 성공하면 200을 응답한다.")
    void responseCreatedWhenCreateWaiting() {
        final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);
        final Long timeId = saveReservationTime();
        final Long themeId = saveTheme();
        final MemberReservationSaveRequest request = new MemberReservationSaveRequest(DATE_MAY_EIGHTH, timeId, themeId, null, null, null);

        given(spec)
                .filter(document("waiting/get",
                        requestCookies(tokenCookieDescriptor),
                        requestFields(memberReservationSaveFieldDescriptor),
                        responseFields(memberReservationFieldDescriptor)))
                .cookie("token", accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/waiting")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("[사용자] 대기 삭제에 성공하면 204를 응답한다.")
    void responseNoContentWhenCancelWaiting() {
        final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);
        final Long waitingId = saveWaiting(MEMBER_CAT_EMAIL);

        given(spec)
                .filter(document("waiting/cancel",
                        requestCookies(tokenCookieDescriptor),
                        pathParameters(parameterWithName("id").description("대기 아이디"))))
                .cookie("token", accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .delete("/waiting/{id}", waitingId)
                .then()
                .statusCode(204);
    }
}
