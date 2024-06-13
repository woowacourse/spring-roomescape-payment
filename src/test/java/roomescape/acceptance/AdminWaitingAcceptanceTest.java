package roomescape.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static roomescape.FieldDescriptorFixture.adminReservationFieldDescriptor;
import static roomescape.FieldDescriptorFixture.adminReservationListFieldDescriptor;
import static roomescape.FieldDescriptorFixture.tokenCookieDescriptor;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.MEMBER_CAT_EMAIL;

public class AdminWaitingAcceptanceTest extends AcceptanceTest {


    @Test
    @DisplayName("[관리자] 대기 조회에 성공하면 200을 응답한다.")
    void responseOkWhenFindWaiting() {
        final String accessToken = getAccessToken(ADMIN_EMAIL);

        given(spec)
                .filter(document("waiting/admin/findAll",
                        requestCookies(
                                attributes(key("title").value("Cookie for find waiting creation")),
                                tokenCookieDescriptor),
                        responseFields(
                                attributes(key("title").value("Fields for user creation")),
                                adminReservationListFieldDescriptor)))
                .cookie("token", accessToken)
                .when()
                .get("/admin/waitings")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("[관리자] 대기 승인에 성공하면 201을 응답한다.")
    void responseCreatedWhenApproveWaiting() {
        final String accessToken = getAccessToken(ADMIN_EMAIL);
        final Long waitingId = saveWaiting(MEMBER_CAT_EMAIL);

        given(spec)
                .filter(document("waiting/admin/approve",
                        requestCookies(tokenCookieDescriptor),
                        pathParameters(parameterWithName("waitingId").description("대기 아이디")),
                        responseFields(adminReservationFieldDescriptor)))
                .cookie("token", accessToken)
                .when()
                .put("/admin/waitings/{waitingId}", waitingId)
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("[관리자] 대기 거절에 성공하면 204를 응답한다.")
    void responseNoContentWhenDenyWaiting() {
        final String accessToken = getAccessToken(ADMIN_EMAIL);
        saveWaiting(ADMIN_EMAIL);
        final Long waitingId = saveWaiting(MEMBER_CAT_EMAIL);

        given(spec)
                .filter(document("waiting/admin/deny",
                        requestCookies(tokenCookieDescriptor),
                        pathParameters(parameterWithName("waitingId").description("대기 아이디"))))
                .cookie("token", accessToken)
                .when()
                .delete("/admin/waitings/{waitingId}", waitingId)
                .then()
                .statusCode(204);
    }
}
