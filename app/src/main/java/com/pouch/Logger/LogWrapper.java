package com.pouch.Logger;

import android.util.Log;


/**
 * 주로 database에 출력으로 이용된다.
 * Helper class which wraps Android's native Log utility in the Logger interface.  This way
 * normal DDMS output can be one of the many targets receiving and outputting logs simultaneously.
 */

public class LogWrapper implements LogNode{

    // For piping : The next node to receive Log data after this one has done its work;
    private LogNode mNext;

    /**
     * returns the nextLogNode in the linked list
     *
     */
    public LogNode getNext() { return mNext;}

    public void setNext(LogNode node){mNext = node;}

    /**
     * 콘솔창에 표시하는 로그
     * Prints data out to the console using Android's native log mechanism.
     * @param priority Log level of the data being logged.  Verbose, Error, etc.
     * @param tag Tag for for the log data.  Can be used to organize log statements.
     * @param msg The actual message to be logged. The actual message to be logged.
     * @param tr If an exception was thrown, this can be sent along for the logging facilities
     *           to extract and print useful information.
     */
    @Override
    public void println(int priority, String tag, String msg, Throwable tr) {
        String useMsg = msg;
        if(useMsg == null){
            useMsg = "";
        }

        // if and exception was provided, convert that exception to a usable string and attach
        // it to the end of the msg method
        if(tr != null){
            msg+="\n" +Log.getStackTraceString(tr);
        }

        Log.println(priority,tag,useMsg);
        if(mNext != null){
            mNext.println(priority,tag,msg,tr);
        }
    }
}
