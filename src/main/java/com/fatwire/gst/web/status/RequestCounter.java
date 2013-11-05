/*
 * Copyright 2006 Dolf Dijkstra. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fatwire.gst.web.status;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletRequest;

public class RequestCounter implements ConcurrencyCounter<HttpServletRequest, RequestInfo> {

    private final AtomicLong counter = new AtomicLong();

    private final AtomicInteger concurrencyCounter = new AtomicInteger();
    private final AtomicInteger peakConcurrencyCounter = new AtomicInteger();

    private final WeakHashMap<Thread, RequestInfo> threadMap = new WeakHashMap<Thread, RequestInfo>();

    private final String name;

    private final ThreadLocal<RequestInfo> threadLocal = new ThreadLocal<RequestInfo>() {

        /* (non-Javadoc)
         * @see java.lang.ThreadLocal#initialValue()
         */
        @Override
        protected RequestInfo initialValue() {
            final RequestInfo i = new RequestInfo(Thread.currentThread());
            synchronized (threadMap) {
                threadMap.put(Thread.currentThread(), i);
            }
            return i;
        }

    };

    private final Comparator<RequestInfo> requestInfoComparator = new Comparator<RequestInfo>() {

        @Override
        public int compare(final RequestInfo o1, final RequestInfo o2) {
            return o1.getThreadName().compareTo(o2.getThreadName());
        }

    };

    public RequestCounter(final String name) {
        this.name = name;
    }

    @Override
    public long getTotalCount() {
        return counter.get();
    }

    @Override
    public int getConcurrencyCount() {
        return concurrencyCounter.get();
    }

    public int getPeakConcurrencyCount() {
        return peakConcurrencyCounter.get();
    }

    @Override
    public Collection<RequestInfo> getCurrentExecutingOperations() {
        final Set<RequestInfo> s = new TreeSet<RequestInfo>(requestInfoComparator);
        RequestInfo[] r;
        synchronized (threadMap) {
            r = threadMap.values().toArray(new RequestInfo[threadMap.size()]);
        }
        for (final RequestInfo info : r) {
            if ((info != null) && info.isAlive()) {// filter out inactive
                                                   // threads
                s.add(info);
            }
        }
        return s;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public synchronized void reset() {
        counter.set(0);
        concurrencyCounter.set(0);
        peakConcurrencyCounter.set(0);
        for (final RequestInfo info : threadMap.values()) {
            info.reset();
        }
    }

    @Override
    public void start(final HttpServletRequest request) {
        final RequestInfo current = threadLocal.get();
        if (current.isRunning()) {
            throw new IllegalStateException("Can't call start twice");
        }
        current.start(request);
        final int c = concurrencyCounter.incrementAndGet();
        int max = peakConcurrencyCounter.get();

        while ((c > max) && !peakConcurrencyCounter.compareAndSet(max, c)) {
            max = peakConcurrencyCounter.get();
        }
        counter.incrementAndGet();

    }

    /**
     * signal end of the request
     * 
     * @return the execution time for this requests in nano seconds
     */
    @Override
    public long end(final HttpServletRequest request) {
        final RequestInfo current = threadLocal.get();
        if (current.isRunning()) {
            current.end(request);
            concurrencyCounter.decrementAndGet();
            return current.getExecutionTimeForLastRequest();
        }
        return -1;

    }

}
