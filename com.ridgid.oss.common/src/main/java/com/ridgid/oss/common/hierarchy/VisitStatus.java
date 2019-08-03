package com.ridgid.oss.common.hierarchy;

@SuppressWarnings("unused")
public enum VisitStatus {
    OK_CONTINUE(true,
            false,
            false,
            false,
            false),
    SKIP_CURRENT(false,
            true,
            false,
            true,
            false),
    SKIP_CURRENT_AND_REMAINING_SIBLINGS(false,
            true,
            true,
            true,
            false),
    SKIP_REMAINING_HANDLERS_FOR_CURRENT(false,
            false,
            false,
            true,
            false),
    SKIP_CURRENT_AND_ALL_REMAINING_AND_STOP(false,
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
        return !isOk();
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
