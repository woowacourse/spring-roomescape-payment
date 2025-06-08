package roomescape.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import roomescape.global.AuthInterceptor;
import roomescape.global.ControllerTest;
import roomescape.theme.controller.ReservationThemeController;
import roomescape.theme.dto.ReservationThemeRequest;
import roomescape.theme.dto.ReservationThemeResponse;
import roomescape.theme.service.ReservationThemeService;

@WebMvcTest(ReservationThemeController.class)
@AutoConfigureRestDocs
class ReservationThemeControllerTestWithMock extends ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationThemeService reservationThemeService;

    @MockitoBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @MockitoBean
    private AuthInterceptor authInterceptor;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("테마 조회 API 테스트")
    class ThemeListTest {

        @DisplayName("모든 테마를 조회하면 200 OK를 반환한다")
        @Test
        void getAllThemes() throws Exception {
            // given
            final List<ReservationThemeResponse> responses = List.of(
                    new ReservationThemeResponse(1L, "공포 테마", "무서운 테마입니다", "horror.jpg"),
                    new ReservationThemeResponse(2L, "모험 테마", "모험이 가득한 테마입니다", "adventure.jpg")
            );

            given(reservationThemeService.findReservationThemes()).willReturn(responses);

            // when & then
            mockMvc.perform(get("/themes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("공포 테마"))
                    .andExpect(jsonPath("$[1].id").value(2))
                    .andExpect(jsonPath("$[1].name").value("모험 테마"))
                    .andDo(restDocs.document(
                            responseFields(
                                    fieldWithPath("[]").type(JsonFieldType.ARRAY).description("테마 목록"),
                                    fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                                    fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                    fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
                            )
                    ));
        }

        @DisplayName("인기 테마를 조회하면 200 OK를 반환한다")
        @Test
        void getPopularThemes() throws Exception {
            // given
            final List<ReservationThemeResponse> responses = List.of(
                    new ReservationThemeResponse(1L, "공포 테마", "무서운 테마입니다", "horror.jpg")
            );

            given(reservationThemeService.findPopularThemes()).willReturn(responses);

            // when & then
            mockMvc.perform(get("/themes/ranking"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$[0].id").value(1))
                    .andExpect(jsonPath("$[0].name").value("공포 테마"))
                    .andDo(restDocs.document(
                            responseFields(
                                    fieldWithPath("[]").type(JsonFieldType.ARRAY).description("인기 테마 목록"),
                                    fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                    fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                                    fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                    fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("테마 생성 API 테스트")
    class ThemeCreateTest {

        @DisplayName("정상적인 테마 생성 요청 시 201 Created를 반환한다")
        @Test
        void createThemeSuccess() throws Exception {
            // given
            final ReservationThemeResponse response = new ReservationThemeResponse(1L, "공포 테마", "무서운 테마입니다", "horror.jpg");
            final ReservationThemeRequest request = new ReservationThemeRequest("공포 테마", "무서운 테마입니다", "horror.jpg");

            given(reservationThemeService.addReservationTheme(any(ReservationThemeRequest.class)))
                    .willReturn(response);

            // when & then
            mockMvc.perform(post("/themes")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").value(1))
                    .andExpect(jsonPath("$.name").value("공포 테마"))
                    .andExpect(jsonPath("$.description").value("무서운 테마입니다"))
                    .andExpect(jsonPath("$.thumbnail").value("horror.jpg"))
                    .andDo(restDocs.document(
                            requestFields(
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                    fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                    fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL")
                            ),
                            responseFields(
                                    fieldWithPath("id").type(JsonFieldType.NUMBER).description("생성된 테마 ID"),
                                    fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                    fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                    fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 URL")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("테마 삭제 API 테스트")
    class ThemeDeleteTest {

        @DisplayName("정상적인 테마 삭제 시 204 No Content를 반환한다")
        @Test
        void deleteThemeSuccess() throws Exception {
            // given
            long themeId = 1L;

            willDoNothing()
                    .given(reservationThemeService)
                    .removeReservationTheme(themeId);

            // when & then
            mockMvc.perform(delete("/themes/{id}", themeId))
                    .andExpect(status().isNoContent())
                    .andDo(restDocs.document(
                            pathParameters(
                                    parameterWithName("id").description("삭제할 테마 ID")
                            )
                    ));
        }
    }
}
