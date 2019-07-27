package com.ridgid.oss.common.hierarchy;

@SuppressWarnings("unused")
public enum VisitStatus {
    CONTINUE_PROCESSING(true,
            false,
            false,
            false,
            false),
    SKIP_NODE(false,
            true,
            false,
            true,
            false),
    SKIP_NODE_AND_REMAINING_SIBLING_NODES(false,
            true,
            true,
            true,
            false),
    SKIP_REMAINING_SAME_HANDLERS_FOR_NODE(false,
            false,
            false,
            true,
            false),
    SKIP_NODE_AND_ALL_REMAINING_NODES(false,
            true,
            true,
            true,
            true);

    private boolean ok;
    private boolean skipNode;
    private boolean skipSiblings;
    private boolean skipRemainingHandlers;
    private boolean stop;

    VisitStatus(boolean ok,
                boolean skipNode,
                boolean skipSiblings,
                boolean skipRemainingHandlers,
                boolean stop) {
        this.ok = ok;
        this.skipNode = skipNode;
        this.skipSiblings = skipSiblings;
        this.skipRemainingHandlers = skipRemainingHandlers;
        this.stop = stop;
    }

    public boolean isOk() {
        return ok;
    }

    public boolean isNotOk() {
        return false;
    }

    public boolean isSkipNode() {
        return skipNode;
    }

    public boolean isSkipSiblings() {
        return skipSiblings;
    }

    public boolean isSkipRemainingHandlers() {
        return skipRemainingHandlers;
    }

    public boolean isStop() {
        return stop;
    }
}
