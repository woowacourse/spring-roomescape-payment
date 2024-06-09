package roomescape.acceptance;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import roomescape.client.PaymentClient;
import roomescape.dto.auth.TokenRequest;
import roomescape.dto.reservation.MemberReservationSaveRequest;
import roomescape.dto.reservation.ReservationTimeSaveRequest;
import roomescape.dto.theme.ThemeSaveRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.http.HttpDocumentation.httpRequest;
import static org.springframework.restdocs.http.HttpDocumentation.httpResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.FieldDescriptorFixture.tokenCookieDescriptor;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.TestFixture.DUMMY_PAYMENT_RESPONSE;
import static roomescape.TestFixture.MEMBER_CAT_EMAIL;
import static roomescape.TestFixture.MEMBER_PASSWORD;
import static roomescape.TestFixture.START_AT_SIX;
import static roomescape.TestFixture.THEME_COMIC_DESCRIPTION;
import static roomescape.TestFixture.THEME_COMIC_NAME;
import static roomescape.TestFixture.THEME_COMIC_THUMBNAIL;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
abstract class AcceptanceTest {

    @MockBean
    protected PaymentClient paymentClient;
    protected RequestSpecification spec;
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        given(paymentClient.pay(any()))
                .willReturn(DUMMY_PAYMENT_RESPONSE());

        Filter filter = documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(
                        modifyHeaders()
                                .remove("Host")
                                .remove("Date")
                                .remove("Keep-Alive")
                                .remove("Connection")
                                .remove("Content-Length"),
                        prettyPrint())
                .withResponseDefaults(
                        modifyHeaders()
                                .remove("Date")
                                .remove("Keep-Alive")
                                .remove("Connection")
                                .remove("Content-Length"),
                        prettyPrint())
                .and()
                .snippets().withDefaults(httpRequest(), httpResponse());

        this.spec = new RequestSpecBuilder()
                .addFilter(filter)
                .build();
    }

    protected Long saveReservationTime() {
        final ReservationTimeSaveRequest request = new ReservationTimeSaveRequest(START_AT_SIX);

        Integer id = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/times")
                .then()
                .assertThat()
                .statusCode(is(201))
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected Long saveTheme() {
        final ThemeSaveRequest request
                = new ThemeSaveRequest(THEME_COMIC_NAME, THEME_COMIC_DESCRIPTION, THEME_COMIC_THUMBNAIL);

        Integer id = given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/themes")
                .then()
                .statusCode(is(201))
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected Long saveReservation() {
        final Long timeId = saveReservationTime();
        final Long themeId = saveTheme();
        final String accessToken = getAccessToken(MEMBER_CAT_EMAIL);
        final MemberReservationSaveRequest request = new MemberReservationSaveRequest(DATE_MAY_EIGHTH, timeId, themeId, null, null, null);

        Integer id = given().log().all()
                .cookie("token", accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected Long saveWaiting(String email) {
        final String memberToken = getAccessToken(email);
        final MemberReservationSaveRequest request = new MemberReservationSaveRequest(DATE_MAY_EIGHTH, 1L, 2L, null, null, null);

        Integer id = given().log().all()
                .cookie("token", memberToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post("/waiting")
                .then().log().all()
                .statusCode(201)
                .extract()
                .jsonPath().get("id");

        return Long.valueOf(id);
    }

    protected String getAccessToken(final String email) {
        return given()
                .body(new TokenRequest(email, MEMBER_PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when()
                .post("/login")
                .detailedCookie("token")
                .getValue();
    }

    protected void assertCreateResponseWithToken(final Object request, final String email, final String path, final int statusCode) {
        final String accessToken = getAccessToken(email);

        RestAssured.given(spec)
                .filter(document("cookies",
                        requestCookies(tokenCookieDescriptor)))
                .cookie("token", accessToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    protected void assertCreateResponse(final Object request, final String path, final int statusCode) {
        RestAssured.given(spec).log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when().post(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    protected void assertGetResponse(final String path, final int statusCode) {
        given(spec).log().all()
                .when().get(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    protected void assertGetResponseWithLoginMember(final String path, final int statusCode) {
        final String accessToken = getAccessToken(ADMIN_EMAIL);

        given(spec).log().all()
                .cookie("token", accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().get(path)
                .then().log().all()
                .statusCode(statusCode);
    }

    protected void assertDeleteResponse(final String path, final Long id, final int statusCode) {
        given(spec).log().all()
                .when().delete(path + id)
                .then().log().all()
                .statusCode(statusCode);
    }
}
