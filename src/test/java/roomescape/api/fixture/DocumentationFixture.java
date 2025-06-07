package roomescape.api.fixture;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import org.springframework.restdocs.snippet.Snippet;

public class DocumentationFixture {

    public static RequestSpecification createDefaultDocumentationSpecification(
            final RestDocumentationContextProvider restDocumentation) {
        return new RequestSpecBuilder()
                .addFilter(RestAssuredRestDocumentation.documentationConfiguration(restDocumentation)
                        .operationPreprocessors()
                        .withRequestDefaults(Preprocessors.prettyPrint())
                        .withResponseDefaults(Preprocessors.prettyPrint())
                )
                .build();
    }

    public static RestDocumentationFilter createDocumentWithDefaultPath(final Snippet... snippets) {
        return RestAssuredRestDocumentation.document("{class-name}/{method-name}", snippets);
    }
}
