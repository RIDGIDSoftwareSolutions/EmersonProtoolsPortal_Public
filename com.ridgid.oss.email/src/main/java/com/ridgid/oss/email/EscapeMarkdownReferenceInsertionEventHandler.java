package com.ridgid.oss.email;

import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;

public class EscapeMarkdownReferenceInsertionEventHandler implements ReferenceInsertionEventHandler {
    @Override
    public Object referenceInsert(Context context, String reference, Object value) {
        if (value instanceof String) {
            return ((String) value)
                    .replaceAll("#", "&#35;")
                    .replaceAll("\\*", "&#42;")
                    .replaceAll("-", "&#45;")
                    .replaceAll("`", "&#96;")
                    .replaceAll("~~", "&#126;&#126;");
        }
        return value;
    }
}
