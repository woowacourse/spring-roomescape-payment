package roomescape.docs.support;

public class SnippetSupport {

    private SnippetSupport() {
    }

    public static String snippet(Object object, String identifier) {
        String className = object.getClass().getSimpleName()
                .replace("DocsTest", "");
        return className + "/" + identifier;
    }
}
