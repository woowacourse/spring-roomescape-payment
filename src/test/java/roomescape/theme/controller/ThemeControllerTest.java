package roomescape.theme.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.createAdminMember;
import static roomescape.TestFixture.createClaims;
import static roomescape.TestFixture.createDefaultTheme;

import io.restassured.http.ContentType;
import java.util.List;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.IntegrationTest;
import roomescape.auth.infrastructure.jwt.JwtTokenProvider;
import roomescape.member.domain.Member;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.PopularThemeResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;

class ThemeControllerTest extends IntegrationTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @DisplayName("모든 테마 조회")
    @Test
    void findAll() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        Theme theme1 = dbHelper.insertTheme(createDefaultTheme());
        Theme theme2 = dbHelper.insertTheme(createDefaultTheme());

        // when & then
        List<ThemeResponse> responses = givenWithDocs("theme-get")
                .cookie("token", token)
                .when().get("/themes")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", ThemeResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting("name")
                    .containsExactly(theme1.getName(), theme2.getName());
        });
    }

    @DisplayName("인기 테마 조회")
    @Test
    void findAllPopular() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        Theme theme1 = dbHelper.insertTheme(createDefaultTheme());
        Theme theme2 = dbHelper.insertTheme(createDefaultTheme());

        // when & then
        List<PopularThemeResponse> responses = givenWithDocs("theme-popular-get")
                .cookie("token", token)
                .when().get("/themes/ranking")
                .then().log().all()
                .statusCode(200)
                .extract().jsonPath().getList(".", PopularThemeResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            assertThat(responses).hasSize(2);
            assertThat(responses)
                    .extracting("name")
                    .containsExactly(theme1.getName(), theme2.getName());
        });
    }

    @DisplayName("테마 저장")
    @Test
    void saveTheme() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        ThemeRequest themeRequest = new ThemeRequest("새로운 테마", "테마 설명", "thumbnail.jpg");

        // when & then
        ThemeResponse response = givenWithDocs("theme-create")
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(themeRequest)
                .when().post("/themes")
                .then().log().all()
                .statusCode(201)
                .extract().as(ThemeResponse.class);

        assertThat(response.name()).isEqualTo(themeRequest.name());
        assertThat(response.description()).isEqualTo(themeRequest.description());
        assertThat(response.thumbnail()).isEqualTo(themeRequest.thumbnail());
    }

    @DisplayName("테마 삭제")
    @Test
    void delete() {
        // given
        Member adminMember = createAdminMember("관리자", "admin@naver.com", "1234");
        dbHelper.insertMember(adminMember);
        String token = jwtTokenProvider.createToken(createClaims(adminMember));

        Theme theme = dbHelper.insertTheme(createDefaultTheme());

        // when & then
        givenWithDocs("theme-delete")
                .cookie("token", token)
                .when().delete("/themes/" + theme.getId())
                .then().log().all()
                .statusCode(204);
    }
}
