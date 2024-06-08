package roomescape.documentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import roomescape.presentation.AuthArgumentResolver;
import roomescape.presentation.AuthInterceptor;

@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@ActiveProfiles("test")
public abstract class AbstractDocumentTest {

    protected MockMvc mockMvc;

    protected ObjectMapper objectMapper;

    @MockBean
    private AuthInterceptor authInterceptor;

    @MockBean
    protected AuthArgumentResolver authArgumentResolver;

    @MockBean
    private Clock clock;

    @BeforeEach
    public void setUp(
            WebApplicationContext webApplicationContext,
            RestDocumentationContextProvider restDocumentation,
            @Autowired ObjectMapper objectMapper
    ) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();

        this.objectMapper = objectMapper;

        when(authInterceptor.preHandle(any(), any(), any()))
                .thenReturn(true);

        when(clock.instant())
                .thenReturn(Instant.parse("2024-04-08T00:00:00Z"));
        when(clock.getZone())
                .thenReturn(Clock.systemDefaultZone().getZone());
    }
}
