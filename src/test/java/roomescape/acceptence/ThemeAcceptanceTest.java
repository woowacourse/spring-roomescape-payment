package roomescape.acceptence;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

class ThemeAcceptanceTest extends AcceptanceFixture {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Test
    @DisplayName("테마를 저장한다.")
    void save_ShouldSaveTheme() {
        RestDocumentationFilter document = document("theme/save",
                requestCookies(
                        cookieWithName("token").description("일반 권한 사용자 토큰")
                ),
                requestFields(
                        fieldWithPath("name").description("테마명"),
                        fieldWithPath("description").description("테마 설명"),
                        fieldWithPath("thumbnail").description("테마 썸네일 사진 URL")
                ),
                responseFields(
                        fieldWithPath("id").description("테마 식별자"),
                        fieldWithPath("name").description("테마명"),
                        fieldWithPath("description").description("테마 설명"),
                        fieldWithPath("thumbnail").description("테마 썸네일 사진 URL")
                )
        );

        // given
        Map<String, String> requestBody = Map.of("name", "theme1", "description", "desc", "thumbnail", "thumbnail");

        // when & then

        RestAssured
                .given(spec)
                .filter(document)
                .cookie(normalToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)

                .when()
                .post("/themes")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED))
                .header(HttpHeaders.LOCATION, "/themes/1")
                .body("id", is(1))
                .body("name", is("theme1"))
                .body("description", is("desc"))
                .body("thumbnail", is("thumbnail"));
    }

    @Test
    @DisplayName("저장된 모든 테마들을 조회한다.")
    void findAll_ShouldInquiryAllThemes() {
        RestDocumentationFilter filter = document("theme/search",
                requestCookies(
                        cookieWithName("token").description("일반 권한 사용자 토큰")
                )
        );

        // given
        saveThemeRequest("theme1");
        saveThemeRequest("theme2");
        saveThemeRequest("theme3");

        // when & then
        RestAssured
                .given(spec)
                .filter(filter)
                .cookie(normalToken)
                .accept(ContentType.JSON)

                .when()
                .get("/themes")

                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", is(3))
                .body("[0].name", is("theme1"))
                .body("[1].name", is("theme2"))
                .body("[2].name", is("theme3"));
    }

    @Test
    @DisplayName("테마를 삭제한다.")
    void delete_ShouldRemoteTheme_ByThemeId() {
        RestDocumentationFilter filter = document("theme/delete",
                pathParameters(
                        parameterWithName("id").description("테마 식별자")
                ),
                requestCookies(
                        cookieWithName("token").description("일반 권한 사용자 토큰")
                )
        );

        // given
        saveThemeRequest("theme1");
        saveThemeRequest("theme2");

        // when
        RestAssured
                .given(spec)
                .cookie(normalToken)
                .accept(ContentType.JSON)
                .filter(filter)

                .when()
                .delete("/themes/{id}", 1)

                .then()
                .statusCode(is(HttpStatus.SC_NO_CONTENT));

        // then
        RestAssured
                .given()
                .cookie(normalToken)
                .accept(ContentType.JSON)

                .when()
                .get("/themes")

                .then()
                .statusCode(is(HttpStatus.SC_OK))
                .body("size()", is(1))
                .body("[0].id", is(2))
                .body("[0].name", is("theme2"));
    }

    @Test
    @DisplayName("주간 인기 테마 10개를 인기순으로 조회한다.")
    void findTopTenThemesOfLastWeek_ShouldGet10PopularThemes_WhileOnceAWeek() {
        RestDocumentationFilter filter = document("theme/popular"
        );

        // given
        Member member = memberRepository.save(new Member("aa", "aa@aa.aa", "aa"));
        List<ReservationTime> times = createTimes(1, 6);
        List<Theme> themes = creatThemes(12);
        createReservation(1, themes.get(0), 6, member, times);
        createReservation(2, themes.get(0), 6, member, times);
        createReservation(1, themes.get(1), 6, member, times);
        createReservation(2, themes.get(1), 5, member, times);
        createReservation(1, themes.get(2), 6, member, times);
        createReservation(2, themes.get(2), 4, member, times);
        createReservation(1, themes.get(3), 6, member, times);
        createReservation(2, themes.get(3), 3, member, times);
        createReservation(1, themes.get(4), 6, member, times);
        createReservation(2, themes.get(4), 2, member, times);
        createReservation(1, themes.get(5), 6, member, times);
        createReservation(2, themes.get(5), 1, member, times);
        createReservation(1, themes.get(6), 6, member, times);
        createReservation(1, themes.get(7), 5, member, times);
        createReservation(1, themes.get(8), 4, member, times);
        createReservation(1, themes.get(9), 3, member, times);
        createReservation(1, themes.get(10), 2, member, times);
        createReservation(1, themes.get(11), 1, member, times);

        // then
        RestAssured
                .given(spec)
                .cookie(normalToken)
                .filter(filter)
                .accept(ContentType.JSON)

                .when()
                .get("themes/popular")

                .then()
                .log().all()
                .statusCode(HttpStatus.SC_OK)
                .body("size()", is(10))
                .body("[0].name", is("0"))
                .body("[1].name", is("1"))
                .body("[2].name", is("2"))
                .body("[3].name", is("3"))
                .body("[4].name", is("4"))
                .body("[5].name", is("5"))
                .body("[6].name", is("6"))
                .body("[7].name", is("7"))
                .body("[8].name", is("8"))
                .body("[9].name", is("9"));
    }

    private void createReservation(int minusDay, Theme theme, int count, Member member, List<ReservationTime> times) {
        for (int i = 1; i <= count; i++) {
            String sql = """
                    INSERT INTO reservation(date, created_at, member_id, reservation_time_id, theme_id, status)
                    VALUES(?,?,?,?,?,?)
                    """;
            jdbcTemplate.update(sql, LocalDate.now().minusDays(minusDay), LocalDateTime.now(), member.getId(),
                    times.get(i - 1).getId(), theme.getId(), "SUCCESS");
        }
    }

    private List<Theme> creatThemes(int size) {
        List<Theme> themes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Theme theme = themeRepository.save(new Theme(String.valueOf(i), "a", "a"));
            themes.add(theme);
        }
        return themes;
    }

    private List<ReservationTime> createTimes(int startHour, int endHour) {
        List<ReservationTime> times = new ArrayList<>();
        for (int i = startHour; i <= endHour; i++) {
            times.add(reservationTimeRepository.save(new ReservationTime(LocalTime.of(startHour + i, 0))));
        }
        return times;
    }

    private void saveThemeRequest(String themeName) {
        Map<String, String> requestBody1 = Map.of("name", themeName, "description", "desc1", "thumbnail", "thumbnail1");

        RestAssured
                .given()
                .cookie(normalToken)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody1)

                .when()
                .post("/themes")

                .then()
                .statusCode(is(HttpStatus.SC_CREATED));
    }

}

