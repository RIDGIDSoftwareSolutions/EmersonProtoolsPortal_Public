import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.IOException;
import java.util.Objects;

class PlainTextRenderer implements IRender {
    @Override
    public void render(Node node, Appendable output) {
        renderDocument(node, output);
    }

    private void renderDocument(Node rootNode, Appendable output) {
        NodeWrapper currentNodeWrapper = new NodeWrapper(rootNode, false);
        while (currentNodeWrapper.node != null) {
            Class<?> previousNodeClass = currentNodeWrapper.node.getPrevious() == null ? null : currentNodeWrapper.node.getPrevious().getClass();
            if (!currentNodeWrapper.alreadyTraversed) {
                if (currentNodeWrapper.node instanceof Text) {
                    try {
                        if (Objects.equals(previousNodeClass, SoftLineBreak.class)) {
                            output.append("  ");
                        }
                        output.append(currentNodeWrapper.node.getChars());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else if (currentNodeWrapper.node instanceof Paragraph && previousNodeClass != null) {
                    try {
                        output.append("\n\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            if (currentNodeWrapper.node.hasChildren() && !currentNodeWrapper.alreadyTraversed) {
                currentNodeWrapper = new NodeWrapper(currentNodeWrapper.node.getFirstChild(), false);
            } else if (currentNodeWrapper.node.getNext() != null) {
                currentNodeWrapper = new NodeWrapper(currentNodeWrapper.node.getNext(), false);
            } else {
                currentNodeWrapper = new NodeWrapper(currentNodeWrapper.node.getParent(), true);
            }
        }
    }

    private static class NodeWrapper {
        final Node node;
        final boolean alreadyTraversed;

        private NodeWrapper(Node node, boolean alreadyTraversed) {
            this.node = node;
            this.alreadyTraversed = alreadyTraversed;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(node);
            if (alreadyTraversed) {
                sb.append("; traversed");
            }
            return sb.toString();
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
