package roomescape.theme.acceptance;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import fixture.MemberFixture;
import fixture.ThemeFixture;
import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.helper.TestHelper;
import roomescape.member.entity.Member;
import roomescape.member.entity.RoleType;
import roomescape.member.repository.MemberRepository;
import roomescape.theme.dto.request.ThemeCreateRequest;
import roomescape.theme.entity.Theme;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ThemeAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private MemberRepository memberRepository;

    Member member;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        member = MemberFixture.create(RoleType.ADMIN);
        memberRepository.save(member);
    }

    @Test
    @DisplayName("테마를 생성한다.")
    void createTheme() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        Theme theme = ThemeFixture.createDefault();
        var request = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );

        // when & then
        TestHelper.postWithToken("/admin/themes", request, token)
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("name", equalTo(theme.getName()))
                .body("description", equalTo(theme.getDescription()))
                .body("thumbnail", equalTo(theme.getThumbnail()));
    }

    @Test
    @DisplayName("중복되는 테마 이름이 있을 경우 생성할 수 없다.")
    void createThemeWithDuplicateName() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        Theme theme = ThemeFixture.createDefault();
        var request1 = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription(),
                theme.getThumbnail()
        );
        var request2 = new ThemeCreateRequest(
                theme.getName(),
                theme.getDescription() + "diff",
                theme.getThumbnail() + "diff"
        );

        TestHelper.postWithToken("/admin/themes", request1, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // when & then
        TestHelper.postWithToken("/admin/themes", request2, token)
                .then()
                .statusCode(HttpStatus.CONFLICT.value());
    }

    @Test
    @DisplayName("모든 테마를 조회한다.")
    void getAllThemes() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        List<Theme> themes = ThemeFixture.createDefaultList(2);
        var request1 = new ThemeCreateRequest(
                themes.get(0).getName(),
                themes.get(0).getDescription(),
                themes.get(0).getThumbnail()
        );
        var request2 = new ThemeCreateRequest(
                themes.get(1).getName(),
                themes.get(1).getDescription(),
                themes.get(1).getThumbnail()
        );

        TestHelper.postWithToken("/admin/themes", request1, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        TestHelper.postWithToken("/admin/themes", request2, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // when & then
        TestHelper.get("/themes")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2))
                .body("[0].name", equalTo(themes.get(0).getName()))
                .body("[1].name", equalTo(themes.get(1).getName()));
    }

    @Test
    @DisplayName("인기 있는 테마를 조회한다.")
    void getPopularThemes() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var request1 = new ThemeCreateRequest(
                "미소",
                "미소 테마",
                "https://miso.com"
        );
        var request2 = new ThemeCreateRequest(
                "우테코",
                "우테코 테마",
                "https://wooteco.com"
        );

        TestHelper.postWithToken("/admin/themes", request1, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        TestHelper.postWithToken("/admin/themes", request2, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // when & then
        TestHelper.get("/themes/popular?limit=2")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(2));
    }

    @Test
    @DisplayName("테마를 삭제한다.")
    void deleteTheme() {
        // given
        String token = TestHelper.login(member.getEmail(), member.getPassword());
        var request = new ThemeCreateRequest(
                "미소",
                "미소 테마",
                "https://miso.com"
        );

        TestHelper.postWithToken("/admin/themes", request, token)
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // when & then
        TestHelper.deleteWithToken("/admin/themes/1", token)
                .then()
                .statusCode(HttpStatus.NO_CONTENT.value());

        TestHelper.get("/themes")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("$", hasSize(0));
    }
}
