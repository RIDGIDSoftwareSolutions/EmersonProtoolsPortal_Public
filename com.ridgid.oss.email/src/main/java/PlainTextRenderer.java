import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.util.ast.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class PlainTextRenderer implements IRender {
    private static final Map<Class<?>, NodeHandler> NODE_HANDLERS;

    static {
        Map<Class<?>, NodeHandler> nodeHandlers = new HashMap<>();
        nodeHandlers.put(Text.class, (node, data, output) -> {
            if (Objects.equals(data.previousNodeClass, SoftLineBreak.class)) {
                output.append("  ");
            }
            output.append(node.getChars());
        });
        nodeHandlers.put(Paragraph.class, (node, data, output) -> {
            if (data.previousNodeClass != null) {
                output.append("\n\n");
            }
        });
        nodeHandlers.put(Link.class, new NodeHandler() {
            @Override
            public void startNode(Node node, HandlerData data, Appendable output) {
            }

            @Override
            public void endNode(Node node, HandlerData data, Appendable output) throws IOException {
                Link link = (Link) node;
                if (!link.getText().equals(link.getUrl())) {
                    output.append(" (").append(link.getUrl()).append(")");
                }
            }
        });
        nodeHandlers.put(BulletList.class, new NodeHandler() {
            @Override
            public void startNode(Node node, HandlerData data, Appendable output) throws IOException {
                if (data.previousNodeClass != null) {
                    if (data.numberOfIndents == 0) {
                        output.append("\n\n");
                    } else {
                        output.append("\n");
                    }
                }
                data.numberOfIndents++;
            }

            @Override
            public void endNode(Node node, HandlerData data, Appendable output) {
                data.numberOfIndents--;
            }
        });
        nodeHandlers.put(BulletListItem.class, (node, data, output) -> {
            if (data.previousNodeClass != null) {
                output.append("\n");
            }
            BulletListItem bulletListItem = (BulletListItem) node;
            output.append(StringUtils.repeat(' ', 2 * data.numberOfIndents - 1)).append(bulletListItem.getOpeningMarker()).append(" ");
        });
        NODE_HANDLERS = Collections.unmodifiableMap(nodeHandlers);
    }

    @Override
    public void render(Node node, Appendable output) {
        renderDocument(node, output);
    }

    private void renderDocument(Node rootNode, Appendable output) {
        NodeWrapper wrapper = new NodeWrapper(rootNode, true);
        HandlerData data = new HandlerData();
        while (wrapper.node != null) {
            NodeHandler handler = NODE_HANDLERS.getOrDefault(wrapper.node.getClass(), NodeHandler.NULL);
            data.previousNodeClass = wrapper.node.getPrevious() == null ? null : wrapper.node.getPrevious().getClass();
            try {
                if (wrapper.beforeTraversal) {
                    handler.startNode(wrapper.node, data, output);
                } else {
                    handler.endNode(wrapper.node, data, output);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
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

    private static class HandlerData {
        Class<? extends Node> previousNodeClass;
        int numberOfIndents;
    }

    private interface NodeHandler {
        NodeHandler NULL = (node, previousNodeClass, output) -> {};

        void startNode(Node node, HandlerData data, Appendable output) throws IOException;

        default void endNode(Node node, HandlerData data, Appendable output) throws IOException {
        }
    }
}
