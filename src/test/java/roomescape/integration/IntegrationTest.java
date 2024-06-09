package roomescape.integration;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ActiveProfiles;
import roomescape.helper.CookieProvider;
import roomescape.helper.DatabaseCleaner;
import roomescape.helper.domain.DomainSupplier;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension.class)
public abstract class IntegrationTest extends DomainSupplier {
    protected RequestSpecification spec;

    @LocalServerPort
    int port;

    @Autowired
    protected DatabaseCleaner databaseCleaner;

    @Autowired
    protected CookieProvider cookieProvider;

    @MockBean
    protected Clock clock;

    @BeforeEach
    protected void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
        RestAssured.port = port;
        databaseCleaner.execute();
        given(clock.instant()).willReturn(Instant.parse("2000-04-07T02:00:00Z"));
        given(clock.getZone()).willReturn(ZoneOffset.UTC);
        subclassSetUp();
    }

    protected void subclassSetUp() {
    }
}
