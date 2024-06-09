package roomescape.utils;

import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.Filter;
import io.restassured.specification.RequestSpecification;
import org.springframework.restdocs.RestDocumentationContextProvider;

public class DocumentHelper {
    public static RequestSpecification specification(final RestDocumentationContextProvider restDocumentation) {
        final Filter documentationConfiguration = documentationConfiguration(restDocumentation)
                .operationPreprocessors()
                .withRequestDefaults(prettyPrint())
                .withResponseDefaults(prettyPrint());

        return new RequestSpecBuilder()
                .addFilter(documentationConfiguration)
                .build();
    }
}
