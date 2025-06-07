package roomescape.docs;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.util.ApiDocumentUtils.getDocumentRequest;
import static roomescape.util.ApiDocumentUtils.getDocumentResponse;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import roomescape.config.TestWebMvcConfig;
import roomescape.mock.TestAdminInterceptor;
import roomescape.mock.TestAuthenticationPrincipalArgumentResolver;
import roomescape.theme.controller.ThemeController;
import roomescape.theme.dto.PopularThemeResponse;
import roomescape.theme.dto.ThemeRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@AutoConfigureRestDocs
@Import(TestWebMvcConfig.class)
@ExtendWith({RestDocumentationExtension.class, MockitoExtension.class})
class ThemeDocsTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private ThemeService themeService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new ThemeController(themeService))
                .setCustomArgumentResolvers(new TestAuthenticationPrincipalArgumentResolver())
                .addInterceptors(new TestAdminInterceptor())
                .apply(documentationConfiguration(restDocumentation))
                .build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * @PostMapping
     * @ResponseStatus(HttpStatus.CREATED) public ThemeResponse saveTheme(@Valid @RequestBody final ThemeRequest request) {
     * return themeService.saveTheme(request);
     * }
     */
    @Test
    void saveTheme() throws Exception {
        var request = new ThemeRequest("테마명", "테마 설명", "thumbnail.jpg");
        var response = new ThemeResponse(1L, "테마명", "테마 설명", "thumbnail.jpg");

        when(themeService.saveTheme(any()))
                .thenReturn(response);

        ResultActions result = mockMvc.perform(
                post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(document("테마 생성",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 이미지 URL")
                        ),
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 이미지 URL")
                        )));
    }

    @Test
    void findAllThemes() throws Exception {
        var response = List.of(
                new ThemeResponse(1L, "테마명1", "테마 설명1", "thumbnail1.jpg"),
                new ThemeResponse(2L, "테마명2", "테마 설명2", "thumbnail2.jpg")
        );

        when(themeService.findAll())
                .thenReturn(response);

        ResultActions result = mockMvc.perform(
                get("/themes")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document("테마 조회",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("테마 ID"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 이미지 URL")
                        )));
    }

    @Test
    void findAllPopularThemes() throws Exception {
        var response = List.of(
                new PopularThemeResponse("테마명1", "테마 설명1", "thumbnail1.jpg"),
                new PopularThemeResponse("테마명2", "테마 설명2", "thumbnail2.jpg"));

        when(themeService.findAllPopular())
                .thenReturn(response);

        ResultActions result = mockMvc.perform(
                get("/themes/ranking")
                        .accept(MediaType.APPLICATION_JSON));

        result.andExpect(status().isOk())
                .andDo(document("인기 테마 조회",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("테마 이름"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING).description("테마 설명"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("테마 썸네일 이미지 URL")
                        )));
    }

    @Test
    void deleteTheme() throws Exception {
        Long themeId = 1L;

        ResultActions result = mockMvc.perform(
                delete("/themes/{id}", themeId));

        result.andExpect(status().isNoContent())
                .andDo(document("테마 삭제",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("테마 ID")
                        )
                ));
    }
}
