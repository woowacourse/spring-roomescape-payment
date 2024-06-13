package roomescape.docs;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.docs.ApiDocumentUtils.getDocumentRequest;
import static roomescape.docs.ApiDocumentUtils.getDocumentResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.config.handler.AdminAuthorizationInterceptor;
import roomescape.config.handler.AuthenticationArgumentResolver;
import roomescape.theme.controller.ThemeController;
import roomescape.theme.dto.ThemeCreateRequest;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.service.ThemeService;

@WebMvcTest(controllers = ThemeController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                        classes = {
                                WebMvcConfigurer.class,
                                AuthenticationArgumentResolver.class,
                                AdminAuthorizationInterceptor.class})
        })
@ExtendWith(RestDocumentationExtension.class)
class ThemeApiTest {
    private final static ThemeResponse RESPONSE1 = new ThemeResponse(1L, "우테코 레벨2 탈출", "우테코 레벨2 탈출", "thumbnail1.com");
    private final static ThemeResponse RESPONSE2 = new ThemeResponse(2L, "우테코 레벨3 탈출", "우테코 레벨3 탈출", "thumbnail2.com");

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    private ThemeService themeService;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }
    
    @DisplayName("테마를 찾는다")
    @Test
    void findThemesTest() throws Exception {
        List<ThemeResponse> responses = List.of(RESPONSE1, RESPONSE2);
        given(themeService.findThemes())
                .willReturn(responses);

        ResultActions result = mockMvc.perform(get("/themes"));

        result.andExpect(status().isOk())
                .andDo(document("themes/findThemes",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").description("테마의 id"),
                                fieldWithPath("[].name").description("테마의 이름"),
                                fieldWithPath("[].description").description("테마의 설명"),
                                fieldWithPath("[].thumbnail").description("테마의 썸네일")
                                )
                ));
    }

    @DisplayName("인기테마를 찾는다")
    @Test
    void findPopularThemes() throws Exception {
        List<ThemeResponse> responses = List.of(RESPONSE1, RESPONSE2);
        given(themeService.findPopularThemes())
                .willReturn(responses);

        ResultActions result = mockMvc.perform(get("/themes/popular"));

        result.andExpect(status().isOk())
                .andDo(document("themes/findPopularThemes",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].id").description("테마의 id"),
                                fieldWithPath("[].name").description("테마의 이름"),
                                fieldWithPath("[].description").description("테마의 설명"),
                                fieldWithPath("[].thumbnail").description("테마의 썸네일")
                        )
                ));
    }

    @DisplayName("테마을 생성한다")
    @Test
    void createTimeTest() throws Exception {
        ThemeCreateRequest request = new ThemeCreateRequest(RESPONSE1.name(), RESPONSE1.description(), RESPONSE1.thumbnail());

        given(themeService.createTheme(any()))
                .willReturn(RESPONSE1);

        ResultActions result = mockMvc.perform(post("/themes")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isCreated())
                .andDo(
                        document("themes/create",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("name").description("테마의 이름"),
                                        fieldWithPath("description").description("테마의 설명"),
                                        fieldWithPath("thumbnail").description("테마의 썸네일")
                                ),
                                responseFields(
                                        fieldWithPath("id").description("테마의 id"),
                                        fieldWithPath("name").description("테마의 이름"),
                                        fieldWithPath("description").description("테마의 설명"),
                                        fieldWithPath("thumbnail").description("테마의 썸네일")
                                )
                        )
                );
    }

    @DisplayName("테마을 삭제한다.")
    @Test
    void deleteTest() throws Exception {
        doNothing().when(themeService).deleteTheme(any());
        ResultActions result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/themes/{id}",1L)
                .contentType(MediaType.APPLICATION_JSON));

        result.andExpect(status().isNoContent())
                .andDo(
                        document("themes/delete",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(parameterWithName("id").description("시간의 id"))
                        ));
    }
    
}
