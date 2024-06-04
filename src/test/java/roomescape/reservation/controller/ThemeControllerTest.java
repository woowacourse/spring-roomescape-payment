package roomescape.reservation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import io.restassured.http.ContentType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import roomescape.exception.BadRequestException;
import roomescape.exception.ErrorType;
import roomescape.exception.NotFoundException;
import roomescape.reservation.controller.dto.ThemeResponse;
import roomescape.util.ControllerTest;

@DisplayName("테마 API 통합 테스트")
class ThemeControllerTest extends ControllerTest {

    @DisplayName("테마 생성 시, 201을 반환한다.")
    @Test
    void create() {
        //given
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));

        Map<String, String> params = new HashMap<>();
        params.put("name", "siso");
        params.put("description", "Hi, I am Siso");
        params.put("thumbnail", "thumbnail");
        params.put("price", "10000");

        //when
        doReturn(themeResponse)
                .when(themeService)
                .create(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .body(params)
                .when().post("/api/v1/themes")
                .then().log().all()
                .apply(document("themes/create/success"))
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("테마 조회 시, 200을 반환한다.")
    @Test
    void findAll() {
        //given
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));

        //when
        doReturn(List.of(themeResponse))
                .when(themeService)
                .findAllThemes();

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().get("/api/v1/themes")
                .then().log().all()
                .apply(document("themes/find/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("테마 삭제 시, 204를 반환한다.")
    @Test
    void deleteTest() throws Exception {
        //given
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));

        //when
        doNothing()
                .when(themeService)
                .delete(isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().delete("/api/v1/themes/" + themeResponse.id())
                .then().log().all()
                .apply(document("reservations/delete/success"))
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @DisplayName("예약이 있는 시간 삭제 시, 400을 반환한다.")
    @Test
    void delete_WithReservationTime() {
        //given
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));

        //when
        doThrow(new BadRequestException(ErrorType.RESERVATION_NOT_DELETED))
                .when(themeService)
                .delete(isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().delete("/api/v1/themes/" + themeResponse.id())
                .then().log().all()
                .apply(document("themes/delete/fail/reservation-exist"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("존재하지 않는 테마 삭제 시, 404를 반환한다.")
    @Test
    void delete_invalidThemeId() {
        //given
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));

        //when
        doThrow(new NotFoundException(ErrorType.THEME_NOT_FOUND))
                .when(themeService)
                .delete(isA(Long.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .when().delete("/api/v1/themes/" + themeResponse.id())
                .then().log().all()
                .apply(document("themes/delete/fail/theme-not-found"))
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @DisplayName("인기 테마 조회 시, 200을 반환한다.")
    @Test
    void getPopular() {
        //given
        ThemeResponse themeResponse = new ThemeResponse(3L, "이름", "설명", "썸네일", BigDecimal.valueOf(10000));

        //when
        doReturn(List.of(themeResponse))
                .when(themeService)
                .findPopularThemes(isA(LocalDate.class), isA(LocalDate.class), isA(Integer.class));

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when().get("/api/v1/themes/popular")
                .then().log().all()
                .apply(document("popular-themes/find/success"))
                .statusCode(HttpStatus.OK.value());
    }

    @DisplayName("테마 생성 시, 잘못된 테마 이름 형식에 대해 400을 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", "      "})
    void create_Invalid_Name(String name) {
        //given
        Map<String, String> params = new HashMap<>();
        params.put("name", name);
        params.put("description", "Hi, I am Siso");
        params.put("thumbnail", "thumbnail");

        //when
        doThrow(new BadRequestException(ErrorType.NAME_FORMAT_ERROR))
                .when(themeService)
                .create(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", adminToken)
                .body(params)
                .when().post("/api/v1/themes")
                .then().log().all()
                .apply(document("themes/create/fail/invalid-name"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }

    @DisplayName("적절하지 않은 limit에 대해 400을 반환한다.")
    @ParameterizedTest
    @ValueSource(ints = {0, -1, 100})
    void invalidLimit(int limit) {
        //given
        String startDate = "2024-05-03";
        String endDate = "2024-05-06";

        //when
        doThrow(new BadRequestException(ErrorType.NAME_FORMAT_ERROR))
                .when(themeService)
                .create(any());

        //then
        restDocs
                .contentType(ContentType.JSON)
                .cookie("token", memberToken)
                .when()
                .get(String.format("/api/v1/themes/popular?startDate=%s&endDate=%s&limit=%s", startDate, endDate,
                        limit))
                .then().log().all()
                .apply(document("popular-themes/find/fail/invalid-limit"))
                .statusCode(HttpStatus.BAD_REQUEST.value());
    }
}
