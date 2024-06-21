package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import static roomescape.fixture.MemberFixture.memberFixture;
import static roomescape.fixture.TestFixture.THEME_HORROR_DESCRIPTION;
import static roomescape.fixture.TestFixture.THEME_HORROR_NAME;
import static roomescape.fixture.TestFixture.THEME_HORROR_THUMBNAIL;
import static roomescape.fixture.ThemeFixture.THEMES;
import static roomescape.fixture.ThemeFixture.themeFixture;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import roomescape.domain.theme.Theme;
import roomescape.dto.theme.ThemeResponse;
import roomescape.dto.theme.ThemeSaveRequest;

class ThemeAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("테마를 성공적으로 생성하면 201을 응답한다.")
    void respondCreatedWhenCreateTheme() {
        var member = saveMember(memberFixture(2L));
        var request = new ThemeSaveRequest(
                THEME_HORROR_NAME,
                THEME_HORROR_DESCRIPTION,
                THEME_HORROR_THUMBNAIL
        );

        var response = RestAssured.given().log().all()
                .cookie("token", accessToken(member.getId()))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .extract().as(ThemeResponse.class);

        assertAll(() -> {
            assertThat(response.name()).isEqualTo(THEME_HORROR_NAME);
            assertThat(response.description()).isEqualTo(THEME_HORROR_DESCRIPTION);
            assertThat(response.thumbnail()).isEqualTo(THEME_HORROR_THUMBNAIL);
        });
    }

    @Test
    @DisplayName("테마 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindThemes() {
        var admin = saveMember(memberFixture(1L));
        THEMES.forEach(this::saveTheme);

        var responses = RestAssured.given().log().all()
                .cookie("token", accessToken(admin.getId()))
                .contentType(ContentType.JSON)
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .extract().as(new TypeRef<List<ThemeResponse>>() {});

        assertThat(responses.size()).isEqualTo(THEMES.size());
    }

    @Test
    @DisplayName("인기 테마를 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindPopularThemes() {
        RestAssured.given().log().all()
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("테마를 성공적으로 삭제하면 204를 응답한다.")
    void respondNoContentWhenDeleteThemes() {
        Theme theme = saveTheme(themeFixture(1L));

        RestAssured.given().log().all()
                .when().delete("/themes/" + theme.getId())
                .then().log().all()
                .statusCode(204);
    }

    @Test
    @DisplayName("존재하지 않는 테마를 삭제하면 404를 응답한다.")
    void respondBadRequestWhenDeleteNotExistingTheme() {
        RestAssured.given().log().all()
                .when().delete("/themes/1")
                .then().log().all()
                .statusCode(404);
    }
}
