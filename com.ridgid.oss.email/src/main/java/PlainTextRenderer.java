import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.IOException;
import java.util.Objects;

class PlainTextRenderer implements IRender {
    private Class<?> previousNodeClass;

    @Override
    public void render(Node node, Appendable output) {
        if (node instanceof Text) {
            try {
                if (Objects.equals(previousNodeClass, SoftLineBreak.class)) {
                    output.append("  ");
                }
                output.append(node.getChars());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (node instanceof Paragraph && previousNodeClass != Document.class) {
            try {
                output.append("\n\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        previousNodeClass = node.getClass();
        if (node.hasChildren()) {
            for (Node childNode : node.getChildren()) {
                render(childNode, output);
            }
        }
    }

    @Override
    public String render(Node node) {
        StringBuilder sb = new StringBuilder();
        render(node, sb);
        return sb.toString();
    }

    @Override
    public IRender withOptions(DataHolder options) {
        return this;
    }

    @Override
    public DataHolder getOptions() {
        return new MutableDataSet();
    }
}
