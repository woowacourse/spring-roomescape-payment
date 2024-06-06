package roomescape.acceptance;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.HeadersModifyingOperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.UriModifyingOperationPreprocessor;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentationConfigurer;
import roomescape.application.config.TestConfig;
import roomescape.support.DatabaseCleanerExtension;

@SpringBootTest(
        classes = TestConfig.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(AcceptanceFixture.class)
@ExtendWith({DatabaseCleanerExtension.class, RestDocumentationExtension.class})
abstract class AcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    protected AcceptanceFixture fixture;

    private RequestSpecification spec;

    @BeforeEach
    void setPortAndDocs(RestDocumentationContextProvider provider) {
        UriModifyingOperationPreprocessor uriModifier = modifyUris().scheme("http").host("woowa.hoony.me").port(8080);
        HeadersModifyingOperationPreprocessor requestHeaderModifier = modifyHeaders()
                .remove("Content-Length");
        HeadersModifyingOperationPreprocessor responseHeaderModifier = modifyHeaders()
                .remove("Date")
                .remove("Keep-Alive")
                .remove("Connection")
                .remove("Transfer-Encoding")
                .remove("Content-Length")
                .set(CONTENT_TYPE, APPLICATION_JSON_VALUE);

        RestAssured.port = port;
        RestAssuredRestDocumentationConfigurer configurer = documentationConfiguration(provider);
        configurer.operationPreprocessors()
                .withRequestDefaults(prettyPrint(), uriModifier, requestHeaderModifier)
                .withResponseDefaults(prettyPrint(), responseHeaderModifier);
        spec = new RequestSpecBuilder()
                .addFilter(configurer)
                .build();
    }

    protected RequestSpecification givenWithSpec() {
        return RestAssured.given(spec);
    }
}
