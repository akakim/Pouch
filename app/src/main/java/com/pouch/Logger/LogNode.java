package com.pouch.Logger;

/**
 * Basic interface for a logging system that can output to one or more targets.
 * 로그 시스템의 기본 인터페이스로 하나 혹은 그이상의 타겟을 출력할 수 있다.
 * Note that in addition to classes that will output these logs in some format,
 * one can also implement this interface over a filter and insert that in the chain,
 * such that no targets further down see certain data, or see manipulated forms of the data.
 * You could, for instance, write a "ToHtmlLoggerNode" that just converted all the log data
 * it received to HTML and sent it along to the next node in the chain, without printing it
 * anywhere.
 */
public interface LogNode {

    /**
     *
     * @param priority 로그의 수준. ex> verbose ,Error ....
     * @param tag
     * @param msg
     * @param tr
     */

    public void println(int priority,String tag,String msg,Throwable tr);
}
