import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.ast.Paragraph;
import com.vladsch.flexmark.ast.SoftLineBreak;
import com.vladsch.flexmark.ast.Text;
import com.vladsch.flexmark.util.ast.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class PlainTextRenderer implements IRender {
    private static final Map<Class<?>, NodeHandler> NODE_HANDLERS;

    static {
        Map<Class<?>, NodeHandler> nodeHandlers = new HashMap<>();
        nodeHandlers.put(Text.class, (node, previousNodeClass, output) -> {
            if (Objects.equals(previousNodeClass, SoftLineBreak.class)) {
                writeTo(output, "  ");
            }
            writeTo(output, node.getChars());
        });
        nodeHandlers.put(Paragraph.class, (node, previousNodeClass, output) -> {
            if (previousNodeClass != null) {
                writeTo(output, "\n\n");
            }
        });
        nodeHandlers.put(Link.class, new NodeHandler() {
            @Override
            public void startNode(Node node, Class<? extends Node> previousNodeClass, Appendable output) {
            }

            @Override
            public void endNode(Node node, Class<? extends Node> previousNodeClass, Appendable output) {
                Link link = (Link) node;
                if (!link.getText().equals(link.getUrl())) {
                    writeTo(output, " (" + link.getUrl() + ")");
                }
            }
        });
        NODE_HANDLERS = Collections.unmodifiableMap(nodeHandlers);
    }

    @Override
    public void render(Node node, Appendable output) {
        renderDocument(node, output);
    }

    private void renderDocument(Node rootNode, Appendable output) {
        NodeWrapper wrapper = new NodeWrapper(rootNode, true);
        while (wrapper.node != null) {
            NodeHandler handler = NODE_HANDLERS.getOrDefault(wrapper.node.getClass(), NodeHandler.NULL);
            Class<? extends Node> previousNodeClass = wrapper.node.getPrevious() == null ? null : wrapper.node.getPrevious().getClass();
            if (wrapper.beforeTraversal) {
                handler.startNode(wrapper.node, previousNodeClass, output);
            } else {
                handler.endNode(wrapper.node, previousNodeClass, output);
            }

            if (wrapper.node.hasChildren() && wrapper.beforeTraversal) {
                wrapper = new NodeWrapper(wrapper.node.getFirstChild(), true);
            } else if (wrapper.node.getNext() != null) {
                wrapper = new NodeWrapper(wrapper.node.getNext(), true);
            } else {
                wrapper = new NodeWrapper(wrapper.node.getParent(), false);
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

    private static void writeTo(Appendable output, CharSequence value) {
        try {
            output.append(value);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class NodeWrapper {
        final Node node;
        final boolean beforeTraversal;

        private NodeWrapper(Node node, boolean beforeTraversal) {
            this.node = node;
            this.beforeTraversal = beforeTraversal;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(node);
            if (beforeTraversal) {
                sb.append("; traversed");
            }
            return sb.toString();
        }
    }

    private interface NodeHandler {
        NodeHandler NULL = (node, previousNodeClass, output) -> {};

        void startNode(Node node, Class<? extends Node> previousNodeClass, Appendable output);

        default void endNode(Node node, Class<? extends Node> previousNodeClass, Appendable output) {
        }
    }
}
