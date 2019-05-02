package com.ridgid.oss.restwebservices.doclet;

import java.io.IOException;
import java.io.Writer;

public interface RestApiRenderer {
    void render(RestApiModel model, Writer writer) throws IOException;

    Writer createWriter(RestApiModel model) throws IOException;

    default void render(RestApiModel model) throws IOException {
        try (Writer writer = createWriter(model)) {
            render(model, writer);
        }
    }
}
