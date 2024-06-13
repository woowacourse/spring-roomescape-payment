package roomescape.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.domain.member.Member;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.service.theme.dto.ThemeListResponse;

class ThemeIntegrationTest extends IntegrationTest {
    @Nested
    @DisplayName("테마 목록 조회 API")
    class FindAllTheme {
        List<FieldDescriptor> themeFindAllResponseDescriptors = List.of(
                fieldWithPath("themes[].id").description("테마 ID"),
                fieldWithPath("themes[].name").description("테마 이름"),
                fieldWithPath("themes[].thumbnail").description("테마 썸네일 이미지 URL"),
                fieldWithPath("themes[].description").description("테마 설명")
        );

        @Test
        void 테마_목록을_조회할_수_있다() {
            themeFixture.createFirstTheme();

            RestAssured.given(spec).log().all()
                    .filter(document("theme-find-all-success",
                            responseFields(themeFindAllResponseDescriptors)
                    ))
                    .when().get("/themes")
                    .then().log().all()
                    .statusCode(200)
                    .body("size()", is(1));
        }
    }

    @Nested
    @DisplayName("인기 테마 목록 조회 API")
    class FindAllPopularTheme {
        List<FieldDescriptor> themePopularFindAllResponseDescriptors = List.of(
                fieldWithPath("themes[].id").description("테마 ID"),
                fieldWithPath("themes[].name").description("테마 이름"),
                fieldWithPath("themes[].thumbnail").description("테마 썸네일 이미지 URL"),
                fieldWithPath("themes[].description").description("테마 설명")
        );


        @Test
        void 최근_일주일동안_예약_건수_많은_순서대로_10개_테마를_인기_테마로_조회할_수_있다() {
            ReservationTime reservationTime = reservationTimeFixture.createPastReservationTime();
            Theme theme = themeFixture.createFirstTheme();
            Member member = memberFixture.createUserMember();
            reservationFixture.createPastReservation(reservationTime, theme, member);

            RestAssured.given(spec).log().all()
                    .filter(document("theme-popular-find-all-success",
                            responseFields(themePopularFindAllResponseDescriptors)
                    ))
                    .when().get("/themes/popular")
                    .then().log().all()
                    .statusCode(200)
                    .body("themes.size()", is(1));

            ThemeListResponse response = RestAssured.get("/themes/popular")
                    .as(ThemeListResponse.class);
            assertThat(response.getThemes().get(0).getId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("테마 추가 API")
    class SaveTheme {
        List<FieldDescriptor> themeSaveRequestDescriptors = List.of(
                fieldWithPath("name").description("테마 이름"),
                fieldWithPath("description").description("테마 설명"),
                fieldWithPath("thumbnail").description("테마 썸네일 이미지 URL")
        );
        List<FieldDescriptor> themeSaveResponseDescriptors = List.of(
                fieldWithPath("id").description("테마 ID"),
                fieldWithPath("name").description("테마 이름"),
                fieldWithPath("description").description("테마 설명"),
                fieldWithPath("thumbnail").description("테마 썸네일 이미지 URL")
        );
        Map<String, String> params = new HashMap<>();

        @BeforeEach
        void setUp() {
            params.put("name", "레벨3");
            params.put("thumbnail", "https://naver.com");
            memberFixture.createAdminMember();
        }

        @Test
        void 테마를_추가할_수_있다() {
            params.put("description", "내용이다.");

            RestAssured.given(spec).log().all()
                    .filter(document("theme-save-success",
                            requestFields(themeSaveRequestDescriptors),
                            responseFields(themeSaveResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/themes")
                    .then().log().all()
                    .statusCode(201)
                    .header("Location", "/themes/1")
                    .body("id", is(1));
        }

        @Test
        void 필드가_빈_값이면_테마를_추가할_수_없다() {
            params.put("description", null);

            RestAssured.given(spec).log().all()
                    .filter(document("theme-save-bad-request"))
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/themes")
                    .then().log().all()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("테마 삭제 API")
    class DeleteTheme {
        List<ParameterDescriptor> themeDeletePathParametersDescriptors = List.of(
                parameterWithName("themeId").description("테마 ID")
        );
        Theme theme;
        Member member;

        @BeforeEach
        void setUp() {
            theme = themeFixture.createFirstTheme();
            member = memberFixture.createAdminMember();
        }

        @Test
        void 테마를_삭제할_수_있다() {
            RestAssured.given(spec).log().all()
                    .filter(document("theme-delete-success",
                            pathParameters(themeDeletePathParametersDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/themes/{themeId}", theme.getId())
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        void 존재하지_않는_테마는_삭제할_수_없다() {
            RestAssured.given(spec).log().all()
                    .filter(document("theme-delete-not-found"))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/themes/{themeId}", 13)
                    .then().log().all()
                    .statusCode(404);
        }

        @Test
        void 예약이_존재하는_테마는_삭제할_수_없다() {
            ReservationTime reservationTime = reservationTimeFixture.createPastReservationTime();
            reservationFixture.createFutureReservation(reservationTime, theme, member);

            RestAssured.given(spec).log().all()
                    .filter(document("theme-delete-bad-request"))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/themes/{themeId}", theme.getId())
                    .then().log().all()
                    .statusCode(400);
        }
    }
}
