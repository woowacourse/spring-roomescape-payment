package roomescape.documentaion;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import roomescape.auth.application.AuthService;
import roomescape.auth.presentation.LoginMemberArgumentResolver;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;

@ExtendWith(RestDocumentationExtension.class)
abstract class DocumentTest {
    protected static ObjectMapper objectMapper;

    protected AuthService authService;
    protected LoginMemberArgumentResolver loginMemberArgumentResolver;
    protected MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() {
        objectMapper = new ObjectMapper();
        JavaTimeModule module = new JavaTimeModule();
        module.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ISO_LOCAL_TIME));
        objectMapper.registerModule(module);
    }

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        this.authService = Mockito.mock(AuthService.class);
        this.loginMemberArgumentResolver = new LoginMemberArgumentResolver(authService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(initController())
                .apply(documentationConfiguration(provider))
                .setCustomArgumentResolvers(loginMemberArgumentResolver)
                .build();
    }

    protected abstract Object initController();
}
