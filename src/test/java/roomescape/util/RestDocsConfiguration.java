package roomescape.util;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.restassured.RestDocumentationFilter;

@ExtendWith(RestDocumentationExtension.class)
public class RestDocsConfiguration {

    protected RequestSpecification spec;
    protected RestDocumentationFilter documentationFilter;

    @LocalServerPort
    private int port;

    @BeforeEach
    public void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = this.port;
        this.documentationFilter = document("{class-name}/{method-name}",
                Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                Preprocessors.preprocessResponse(Preprocessors.prettyPrint()));

        this.spec = new RequestSpecBuilder()
                .addFilter(documentationFilter)
                .addFilter(documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }
}
