package com.pouch.Logger;

/**
 * 그냥 vervose나 이런 우선순위없이 만들고 싶을때 단순히 내가 보여줄 메세지만 쓸때 이렇게한다.
 *
 * Simple {@link LogNode} filter, removes everything except the message.
 * Useful for situations like on-screen log output where you don't want a lot of metadata displayed,
 * just easy-to-read message updates as they're happening.
 */
public class MessageOnlyLogFilter implements LogNode{
    LogNode mNext;

    /**
     * Takes the "next" LogNode as a parameter, to simplify chaining.
     *
     * @param next The next LogNode in the pipeline.
     */
    public MessageOnlyLogFilter(LogNode next) {
        mNext = next;
    }

    public MessageOnlyLogFilter() {
    }

    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        if (mNext != null) {
            getNext().println(Log.NONE, null, msg, null);
        }
    }

    /**
     * Returns the next LogNode in the chain.
     */
    public LogNode getNext() {
        return mNext;
    }

    /**
     * Sets the LogNode data will be sent to..
     */
    public void setNext(LogNode node) {
        mNext = node;
    }

}
