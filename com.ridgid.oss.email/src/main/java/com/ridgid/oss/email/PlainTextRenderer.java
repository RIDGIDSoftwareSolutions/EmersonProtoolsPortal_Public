package com.ridgid.oss.email;

import com.vladsch.flexmark.ast.*;
import com.vladsch.flexmark.ext.tables.*;
import com.vladsch.flexmark.util.ast.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

class PlainTextRenderer implements IRender {
    private static final Map<Class<?>, NodeHandler> NODE_HANDLERS;

    static {
        Map<Class<?>, NodeHandler> nodeHandlers = new HashMap<>();
        nodeHandlers.put(Text.class, (node, data, output) -> {
            if (Objects.equals(data.previousNodeClass, SoftLineBreak.class)) {
                output.append(" ");
            }
            output.append(node.getChars());
        });
        NodeHandler blockHandler = (node, data, output) -> {
            if (data.previousNodeClass != null) {
                output.append("\n\n");
            }
        };
        nodeHandlers.put(Paragraph.class, blockHandler);
        nodeHandlers.put(Heading.class, blockHandler);
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
        registerListHandlers(nodeHandlers);
        registerTableHandlers(nodeHandlers);
        NODE_HANDLERS = Collections.unmodifiableMap(nodeHandlers);
    }

    private static void registerListHandlers(Map<Class<?>, NodeHandler> nodeHandlers) {
        ListBlockHandler listBlockHandler = new ListBlockHandler();
        nodeHandlers.put(BulletList.class, listBlockHandler);
        nodeHandlers.put(OrderedList.class, listBlockHandler);
        NodeHandler listItemHandler = (node, data, output) -> {
            if (data.previousNodeClass != null) {
                output.append("\n");
            }
            ListItem bulletListItem = (ListItem) node;
            output.append(StringUtils.repeat(' ', 2 * data.numberOfIndents - 1)).append(bulletListItem.getOpeningMarker()).append(" ");
        };
        nodeHandlers.put(BulletListItem.class, listItemHandler);
        nodeHandlers.put(OrderedListItem.class, listItemHandler);
    }

    private static void registerTableHandlers(Map<Class<?>, NodeHandler> nodeHandlers) {
        nodeHandlers.put(TableBlock.class, new NodeHandler() {
            @Override
            public void startNode(Node node, HandlerData data, Appendable output) {
                data.headerRow = new ArrayList<>();
                data.dataRows = new ArrayList<>();
            }

            @Override
            public void endNode(Node node, HandlerData data, Appendable output) throws IOException {
                String pattern = "| " + data.headerRow.stream().map(column -> "%-" + column.length() + "s").collect(Collectors.joining(" | ")) + " |";
                output.append(String.format(pattern, data.headerRow.toArray())).append("\n");
                output.append("|-").append(data.headerRow.stream().map(column -> StringUtils.repeat('-', column.length())).collect(Collectors.joining("-|-"))).append("-|\n");
                output.append(data.dataRows.stream().map(row -> String.format(pattern, row.toArray())).collect(Collectors.joining("\n")));

                data.headerRow = null;
                data.dataRows = null;
            }
        });
        nodeHandlers.put(TableHead.class, new NodeHandler() {
            @Override
            public void startNode(Node node, HandlerData data, Appendable output) {
                data.inHeaderRow = true;
            }

            @Override
            public void endNode(Node node, HandlerData data, Appendable output) {
                data.inHeaderRow = false;
            }
        });
        nodeHandlers.put(TableBody.class, new NodeHandler() {
            @Override
            public void startNode(Node node, HandlerData data, Appendable output) {
                data.inBodyRow = true;
            }

            @Override
            public void endNode(Node node, HandlerData data, Appendable output) {
                data.inBodyRow = false;
            }
        });
        nodeHandlers.put(TableRow.class, (node, data, output) -> {
            if (data.inBodyRow) {
                data.dataRows.add(new ArrayList<>());
            }
        });
        nodeHandlers.put(TableCell.class, new NodeHandler() {
            @Override
            public void startNode(Node node, HandlerData data, Appendable output) {
                data.outputStack.push(new StringBuilder());
            }

            @Override
            public void endNode(Node node, HandlerData data, Appendable output) {
                if (data.inHeaderRow) {
                    data.headerRow.add(data.outputStack.pop().toString());
                } else if (data.inBodyRow) {
                    data.dataRows.get(data.dataRows.size() - 1).add(data.outputStack.pop().toString());
                } else {
                    data.outputStack.pop();
                }
            }
        });
    }

    @Override
    public void render(Node node, Appendable output) {
        NodeWrapper wrapper = new NodeWrapper(node, true);
        HandlerData data = new HandlerData();
        data.outputStack.push(output);
        while (wrapper.node != null) {
            NodeHandler handler = NODE_HANDLERS.getOrDefault(wrapper.node.getClass(), NodeHandler.NULL);
            data.previousNodeClass = wrapper.node.getPrevious() == null ? null : wrapper.node.getPrevious().getClass();
            try {
                if (wrapper.beforeTraversal) {
                    handler.startNode(wrapper.node, data, data.outputStack.peek());
                } else {
                    handler.endNode(wrapper.node, data, data.outputStack.peek());
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
        List<String> headerRow;
        List<List<String>> dataRows;
        boolean inHeaderRow;
        boolean inBodyRow;
        Deque<Appendable> outputStack = new ArrayDeque<>();
    }

    private interface NodeHandler {
        NodeHandler NULL = (node, previousNodeClass, output) -> {};

        void startNode(Node node, HandlerData data, Appendable output) throws IOException;

        default void endNode(Node node, HandlerData data, Appendable output) throws IOException {
        }
    }

    private static class ListBlockHandler implements NodeHandler {
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
    }
}
