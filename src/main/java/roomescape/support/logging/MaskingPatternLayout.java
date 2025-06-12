package roomescape.support.logging;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MaskingPatternLayout extends PatternLayout {

    private Pattern multilinePattern;
    private final List<String> maskPatterns = new ArrayList<>();

    @Override
    public void start() {
        if (!maskPatterns.isEmpty()) {
            multilinePattern = Pattern.compile(String.join("|", maskPatterns), Pattern.MULTILINE);
        }
        super.start();
    }

    public void addMaskPattern(String maskPattern) {
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
            for (int group = 1; group <= matcher.groupCount(); group++) {
                if (matcher.group(group) == null) {
                    continue;
                }
                handleMaskingGroup(matcher, group, sb);
            }
        }

        return sb.toString();
    }

    private void handleMaskingGroup(Matcher matcher, int group, StringBuilder sb) {
        String matchedValue = matcher.group(group);
        String fullMatch = matcher.group();
        int groupStart = matcher.start(group);
        int groupEnd = matcher.end(group);

        if (fullMatch.startsWith("email")) {
            maskEmail(matchedValue, sb, groupStart, groupEnd);
        } else {
            maskMessage(groupStart, groupEnd, sb);
        }
    }

    private void maskEmail(String matchedValue, StringBuilder sb, int groupStart, int groupEnd) {
        String maskedEmail = maskEmail(matchedValue);
        replaceRange(sb, groupStart, groupEnd, maskedEmail);
    }

    private static void maskMessage(int groupStart, int groupEnd, StringBuilder sb) {
        for (int i = groupStart; i < groupEnd; i++) {
            sb.setCharAt(i, '*');
        }
    }

    private String maskEmail(String email) {
        int atIndex = email.indexOf("@");
        if (atIndex > 1) {
            return email.charAt(0)
                    + "*".repeat(atIndex - 1)
                    + email.substring(atIndex);
        }
        return "***";
    }

    private void replaceRange(StringBuilder sb, int start, int end, String replacement) {
        sb.replace(start, end, replacement);
    }
}
