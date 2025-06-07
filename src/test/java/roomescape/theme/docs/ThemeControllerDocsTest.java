package roomescape.theme.docs;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.config.RestDocsConfig;
import roomescape.global.auth.infrastructure.AuthorizationExtractor;
import roomescape.global.auth.infrastructure.JwtProvider;
import roomescape.global.auth.service.AuthService;
import roomescape.member.domain.MemberRole;
import roomescape.theme.controller.ThemeController;
import roomescape.theme.dto.request.ThemeCreateRequest;
import roomescape.theme.dto.response.ThemeResponse;
import roomescape.theme.service.ThemeService;

@Import({RestDocsConfig.class, AuthorizationExtractor.class})
@WebMvcTest(ThemeController.class)
@ExtendWith(RestDocumentationExtension.class)
public class ThemeControllerDocsTest {

    @Value("${application.ip}")
    private String ip;

    @Value("${application.port}")
    private int port;

    @MockitoBean
    private ThemeService themeService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private AuthService authService;

    @Autowired
    private RestDocumentationResultHandler restDocs;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider provider
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(provider)
                        .uris()
                        .withScheme("http")
                        .withHost(ip)
                        .withPort(port))
                .alwaysDo(restDocs)
                .build();
    }

    @Test
    void getThemes() throws Exception {
        List<ThemeResponse> response = List.of(
                new ThemeResponse(1L, "a", "a", "a"),
                new ThemeResponse(2L, "b", "b", "b")
        );
        when(themeService.getThemes())
                .thenReturn(response);

        mockMvc.perform(RestDocumentationRequestBuilders.get("/themes"))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("Theme Id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("Theme Name"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING)
                                        .description("Theme Description"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("Theme Thumbnail")
                        )
                ));
    }

    @Test
    void getPopularThemes() throws Exception {
        List<ThemeResponse> response = List.of(
                new ThemeResponse(1L, "a", "a", "a"),
                new ThemeResponse(2L, "b", "b", "b")
        );
        when(themeService.getPopularThemes())
                .thenReturn(response);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/themes/popular"))
                .andExpectAll(
                        status().isOk(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].id").type(JsonFieldType.NUMBER).description("Theme Id"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("Theme Name"),
                                fieldWithPath("[].description").type(JsonFieldType.STRING)
                                        .description("Theme Description"),
                                fieldWithPath("[].thumbnail").type(JsonFieldType.STRING).description("Theme Thumbnail")
                        )
                ));
    }

    @Test
    void createTheme() throws Exception {
        ThemeResponse response = new ThemeResponse(1L, "a", "a", "a");
        ThemeCreateRequest request = new ThemeCreateRequest("a", "a", "a");
        when(themeService.create(any()))
                .thenReturn(response);
        when(jwtProvider.getRole(anyString()))
                .thenReturn(MemberRole.ADMIN);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/themes")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("token", "admin_token")))
                .andExpectAll(
                        status().isCreated(),
                        content().json(objectMapper.writeValueAsString(response))
                )
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("id").type(JsonFieldType.NUMBER).description("Theme Id"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("Theme Name"),
                                fieldWithPath("description").type(JsonFieldType.STRING)
                                        .description("Theme Description"),
                                fieldWithPath("thumbnail").type(JsonFieldType.STRING).description("Theme Thumbnail")
                        )
                ));
    }

    @Test
    void deleteTheme() throws Exception {
        when(jwtProvider.getRole(anyString()))
                .thenReturn(MemberRole.ADMIN);

        mockMvc.perform(RestDocumentationRequestBuilders.delete("/themes/{themeId}", 1L)
                        .cookie(new Cookie("token", "admin_access_token")))
                .andExpectAll(
                        status().isNoContent()
                )
                .andDo(restDocs.document());
    }

}
