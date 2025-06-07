package roomescape.infrastructure.log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PasswordMasker {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("(?<=\"password\":\\s?\")[^\"]+");
    
    private PasswordMasker() {
    }
    
    public static Object mask(Object data) {
        if (data == null) {
            return null;
        }
        try {
            String jsonString = objectMapper.writeValueAsString(data);
            Matcher matcher = PASSWORD_PATTERN.matcher(jsonString);
            String maskedString = matcher.replaceAll("****");
            return objectMapper.readValue(maskedString, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            return data;
        }
    }
}
