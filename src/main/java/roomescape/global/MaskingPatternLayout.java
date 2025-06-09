package roomescape.global;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class MaskingPatternLayout extends PatternLayout {

    private static final String EMAIL = "email";

    private Pattern multilinePattern;
    private List<String> maskPatterns = new ArrayList<>();

    public void addMaskPattern(String maskPattern) {
        System.out.println("add mask pattern: " + maskPattern + "");
        maskPatterns.add(maskPattern);
        multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }

    private String maskMessage(String message) {
        if (multilinePattern == null) {
            return message;
        }
        StringBuilder sb = new StringBuilder(message);
        Matcher matcher = multilinePattern.matcher(sb);
        while (matcher.find()) {
            if (matcher.group().contains(EMAIL)) {
                maskEmail(matcher, sb);
            } else {
                maskDefault(matcher, sb);
            }
        }
        return sb.toString();
    }

    private void maskEmail(Matcher matcher, StringBuilder sb) {
        int emailIdGroup = 1;
        IntStream.range(matcher.start(emailIdGroup), matcher.end(emailIdGroup)).forEach(i -> sb.setCharAt(i, '*'));
    }

    private void maskDefault(final Matcher matcher, final StringBuilder sb) {
        IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
            if (matcher.group(group) != null) {
                IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> sb.setCharAt(i, '*'));
            }
        });
    }
}
