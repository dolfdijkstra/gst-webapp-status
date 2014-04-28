/*
 * Copyright (C) 2006 Dolf Dijkstra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fatwire.gst.web.status;

import java.lang.Thread.State;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * Object that holds state on the current executed request.
 * 
 * This object is thread safe, under the restriction that all WRITES are done
 * from the same thread.
 * 
 * 
 * @author Dolf Dijkstra
 * 
 */
public class RequestInfo {

    private final String threadName;

    private final WeakReference<Thread> thread;

    private final AtomicLong counter = new AtomicLong();

    private volatile String currentUri;

    private volatile long lastStartTime;

    private volatile long lastEndTime;

    private volatile long lastNanoStartTime;

    private volatile long lastExecutionTime;

    private final AtomicBoolean running = new AtomicBoolean(false);

    private volatile String method;

    private volatile String remoteHost;

    /**
     * @param t
     */
    public RequestInfo(final Thread t) {
        super();
        this.threadName = t.getName();
        this.thread = new WeakReference<Thread>(t);
    }

    /**
     * 
     * 
     * @return true is the thread is still alive
     * @see Thread#isAlive()
     */
    public boolean isAlive() {
        Thread t = thread.get();
        return t != null && t.isAlive();
    }

    protected String createUri(final HttpServletRequest request) {
        final StringBuilder b = new StringBuilder(150);
        b.append(request.getRequestURI());
        final String q = request.getQueryString();
        if (q != null) {
            b.append("?").append(q);
        }
        return b.toString();
    }

    protected void start(HttpServletRequest request) {
        if (running.get())
            return;
        running.set(true);
        counter.incrementAndGet();
        this.lastStartTime = System.currentTimeMillis();
        this.lastNanoStartTime = System.nanoTime();
        this.currentUri = createUri(request);
        this.remoteHost = request.getRemoteHost() + ":" + request.getRemotePort();
        this.method = request.getMethod();

    }

    protected void end(HttpServletRequest request) {
        if (!running.get())
            return;
        this.lastExecutionTime = System.nanoTime() - lastNanoStartTime;
        this.lastEndTime = System.currentTimeMillis();
        running.set(false);
    }

    /**
     * 
     * @return execution time in nano seconds
     */

    public long getExecutionTimeForLastRequest() {
        if (running.get())
            return (System.nanoTime() - lastNanoStartTime);
        return lastExecutionTime;
    }

    /**
     * @return the counter
     */
    public long getCounter() {
        return counter.get();
    }

    /**
     * @return the currentUri
     */
    public String getCurrentUri() {
        return currentUri;
    }

    /**
     * @return the lastEndTime in epoch
     */
    public long getLastEndTime() {
        return lastEndTime;
    }

    /**
     * @return the lastStartTime in epoch
     */
    public long getLastStartTime() {
        return lastStartTime;
    }

    /**
     * @return the threadName
     */
    public String getThreadName() {
        return threadName;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return running.get();
    }

    public void reset() {
        counter.set(0);
        currentUri = null;
        method = null;

        lastStartTime = 0L;
        lastNanoStartTime = 0L;
        lastEndTime = 0L;
        lastExecutionTime = 0L;
        running.set(false);

    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the remoteHost
     */
    public String getRemoteHost() {
        return remoteHost;
    }

    /**
     * 
     * 
     * @return the State of the thread related to this object
     */
    public State getThreadState() {
        final Thread x = thread.get();
        if (x != null) {
            return x.getState();
        } else {
            return null;
        }
    }
}
