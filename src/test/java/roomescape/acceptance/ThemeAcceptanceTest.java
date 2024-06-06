package roomescape.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.fixture.ThemeFixture.SPACE_THEME;
import static roomescape.fixture.ThemeFixture.SPOOKY_THEME;
import static roomescape.fixture.ThemeFixture.TEST_THEME;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.application.reservation.dto.request.ThemeRequest;

class ThemeAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("관리자가 테마를 생성한다.")
    void createThemeTest() {
        ThemeRequest request = TEST_THEME.request();

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("name").description("테마명"),
                fieldWithPath("description").description("테마 설명"),
                fieldWithPath("thumbnail").description("이미지 URL"),
                fieldWithPath("price").description("가격")
        };

        RestDocumentationFilter docsFilter = document(
                "theme-create",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", fixture.getAdminToken())
                .contentType(ContentType.JSON)
                .body(request)
                .filter(docsFilter)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("테마 목록을 조회한다.")
    void findAllThemesTest() {
        fixture.createTheme(SPOOKY_THEME.request());
        fixture.createTheme(SPACE_THEME.request());

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].id").description("ID"),
                fieldWithPath("[].name").description("테마명"),
                fieldWithPath("[].description").description("테마 설명"),
                fieldWithPath("[].thumbnail").description("이미지 URL"),
                fieldWithPath("[].price").description("가격")
        };

        RestDocumentationFilter docsFilter = document(
                "theme-find-all",
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .accept(ContentType.JSON)
                .filter(docsFilter)
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .body("size()", equalTo(2));
    }

    @Test
    @DisplayName("관리자가 테마를 삭제한다.")
    void deleteThemeTest() {
        long themeId = fixture.createTheme(SPACE_THEME.request()).id();

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        ParameterDescriptor[] pathParameterDescriptors = {
                parameterWithName("id").description("테마 ID")
        };

        RestDocumentationFilter docsFilter = document(
                "theme-delete",
                requestCookies(cookieDescriptors),
                pathParameters(pathParameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", fixture.getAdminToken())
                .pathParam("id", themeId)
                .filter(docsFilter)
                .when().delete("/themes/{id}")
                .then().log().all()
                .statusCode(204);
    }
}
