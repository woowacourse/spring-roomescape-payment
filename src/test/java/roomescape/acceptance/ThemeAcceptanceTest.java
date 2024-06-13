package roomescape.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import roomescape.dto.theme.ThemeSaveRequest;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.snippet.Attributes.attributes;
import static org.springframework.restdocs.snippet.Attributes.key;
import static roomescape.FieldDescriptorFixture.themeFieldDescriptor;
import static roomescape.FieldDescriptorFixture.themeFieldDescriptorWithoutId;
import static roomescape.FieldDescriptorFixture.themeListFieldDescriptor;
import static roomescape.TestFixture.THEME_COMIC_DESCRIPTION;
import static roomescape.TestFixture.THEME_COMIC_NAME;
import static roomescape.TestFixture.THEME_COMIC_THUMBNAIL;

class ThemeAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("테마를 성공적으로 생성하면 201을 응답한다.")
    void respondCreatedWhenCreateTheme() {
        final ThemeSaveRequest request
                = new ThemeSaveRequest(THEME_COMIC_NAME, THEME_COMIC_DESCRIPTION, THEME_COMIC_THUMBNAIL);

        given(spec)
                .filter(document("theme/admin/create",
                        requestFields(
                                attributes(key("title").value("Fields for theme creation")),
                                themeFieldDescriptorWithoutId()),
                        responseFields(themeFieldDescriptor)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/themes")
                .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("테마 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindThemes() {
        given(spec)
                .filter(document("theme/admin/find/all",
                        responseFields(themeListFieldDescriptor)))
                .when()
                .get("/themes")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("인기 테마를 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindPopularThemes() {
        given(spec)
                .filter(document("theme/admin/find/popular",
                        responseFields(themeListFieldDescriptor)))
                .when()
                .get("/themes/popular")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("테마를 성공적으로 삭제하면 204를 응답한다.")
    void respondNoContentWhenDeleteThemes() {
        final Long themeId = saveTheme();

        given(spec)
                .filter(document("theme/admin/delete/success",
                        pathParameters(parameterWithName("id").description("테마 아이디"))))
                .when()
                .delete("/themes/{id}", themeId)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("존재하지 않는 테마를 삭제하면 400을 응답한다.")
    void respondBadRequestWhenDeleteNotExistingTheme() {
        saveTheme();

        given(spec)
                .when().delete("/themes/{id}", 0)
                .then()
                .statusCode(400);
    }
}
