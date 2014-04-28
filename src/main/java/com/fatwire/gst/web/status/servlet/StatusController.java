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
package com.fatwire.gst.web.status.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.Thread.State;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fatwire.gst.web.status.RequestCounter;
import com.fatwire.gst.web.status.RequestInfo;
import com.fatwire.gst.web.status.TimeFormat;

public class StatusController {

    private RequestCounter requestCounter;

    private boolean extendedInfo = false;

    private ServletContext servletContext;

    private final View fullTxtView = new TxtView(true);

    private final View shortTxtView = new TxtView(false);

    private final View htmlView = new HtmlView(true);

    class TxtView implements View {
        private final boolean requestInfo;

        /**
         * @param requestInfo
         */
        public TxtView(final boolean requestInfo) {
            super();
            this.requestInfo = requestInfo;
        }

        public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            response.setContentType("text/plain");

            final PrintWriter writer = response.getWriter();
            writer.println("total:" + requestCounter.getTotalCount());
            writer.println("concurrent:" + requestCounter.getConcurrencyCount());
            if (requestInfo) {
                for (final RequestInfo info : requestCounter.getCurrentExecutingOperations()) {
                    writer.print(format(info));

                }
            }
        }

        protected String format(final RequestInfo info) {
            final StringBuilder b = new StringBuilder();

            b.append(info.getThreadName());
            b.append('\t');
            b.append(info.getCounter());
            b.append('\t');
            b.append(info.isRunning() ? "R" : ".");
            b.append('\t');
            b.append(info.getExecutionTimeForLastRequest());
            b.append('\t');
            b.append(info.getRemoteHost());
            b.append('\t');
            b.append(info.getMethod());
            b.append('\t');
            b.append(info.getCurrentUri());
            b.append('\t');
            b.append(info.getLastStartTime());
            b.append('\t');
            b.append(System.currentTimeMillis() - info.getLastStartTime());
            b.append('\r');
            b.append('\n');
            return b.toString();

        }

     
    }

    class HtmlView implements View {
        private final boolean requestInfo;

        private final DateFormat df = new SimpleDateFormat("HH:mm:ss");

        private final TimeFormat tf = new TimeFormat();

        /**
         * @param requestInfo
         */
        public HtmlView(final boolean requestInfo) {
            super();
            this.requestInfo = requestInfo;
        }
      
        /* (non-Javadoc)
         * @see org.springframework.web.servlet.View#render(java.util.Map, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
         */
        public void render(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
            response.setContentType("text/html; charset=\"UTF-8\"");
            final PrintWriter writer = response.getWriter();
            writer.println("<!DOCTYPE html>");
            writer.println("<html><head><title>Status for " + getServletContext().getServerInfo() + " at "
                    + request.getLocalName() + getServletContext().getContextPath() + "</title>");
            writer.println("<style type=\"text/css\">");
            writer.println("td,th,body { font-size: small; font-family: monospace; }");
            writer.println("tr.requestinfo {white-space: nowrap}");
            writer.println("tr.notrunning {color: #CCCCCC}");
            writer.println("td.blocked {color: red}");
            writer.println("td.runnable {color: green}");
            writer.println("td.TSLS, td.counter,td.RT {text-align:right}");
            writer.println("</style>");
            writer.println("</head><body>");
            writer.print("<h1>Status for " + getServletContext().getServerInfo() + " at " + request.getLocalName()
                    + getServletContext().getContextPath() + "</h1>");
            writer.print("<div class=\"summary\">total:" + requestCounter.getTotalCount());
            writer.print("<br/>");
            writer.print("concurrent:" + requestCounter.getConcurrencyCount());
            writer.print("<br/>");
            writer.print("peak:" + requestCounter.getPeakConcurrencyCount());

            writer.print("</div>");
            final Collection<RequestInfo> c = requestCounter.getCurrentExecutingOperations();

            if (requestInfo) {
                writer.print("<table><tr><th>");
                writer.print("thread");
                writer.print("</th><th>");
                writer.print("counter");
                writer.print("</th><th>");
                writer.print("R");
                writer.print("</th><th>");
                writer.print("time");
                writer.print("</th><th>");
                writer.print("remote");
                writer.print("</th><th>");
                writer.print("method");
                writer.print("</th><th>");
                writer.print("uri");
                writer.print("</th><th>");
                writer.print("LST");
                writer.print("</th><th>");
                writer.print("TSLS");
                writer.print("</th><th>");
                writer.print("State");
                writer.print("</th></tr>");
                for (final RequestInfo info : c) {
                    if (info.isAlive()) {
                        writer.print(format(info));
                    }

                }
                writer.print("</table>");
                writer.print("<hr/>");
                writer.print("<table><tr><td>thread<td><td>name of the thread</td></tr>");
                writer.print("<tr><td>counter<td><td>number of invocations by this thread</td></tr>");
                writer.print("<tr><td>R<td><td>is currently running?</td></tr>");
                writer.print("<tr><td>time<td><td>execution time of last request</td></tr>");
                writer.print("<tr><td>remote<td><td>the remote host and port</td></tr>");
                writer.print("<tr><td>method<td><td>the http method</td></tr>");
                writer.print("<tr><td>uri<td><td>short uri of last request</td></tr>");
                writer.print("<tr><td>LST<td><td>time at the last start (Last Start Time)</td></tr>");
                writer.print("<tr><td>TSLS<td><td>time since last start</td></tr>");
                writer.print("<tr><td>State<td><td>the current thread state for this request</td></tr>");

                writer.println("</table>");
            }
            writer.print("</body></html>");
            writer.flush();
        }

        protected String format(final RequestInfo info) {
            final StringBuilder b = new StringBuilder(500);
            State s = info.getThreadState();
            boolean r = info.isRunning();
            b.append("<tr class=\"requestinfo" + (r ? "" : " notrunning") + "\">");
            b.append("<td class=\"threadname\">");
            b.append(info.getThreadName());
            b.append("</td><td class=\"counter\">");
            b.append(info.getCounter());
            b.append("</td><td class=\"running\">");
            b.append(r ? "R" : ".");
            b.append("</td><td class=\"RT\">");
            if (!r)
                b.append(tf.format(info.getExecutionTimeForLastRequest()));
            b.append("</td><td class=\"remote_host\">");
            b.append(info.getRemoteHost());
            b.append("</td><td class=\"method\">");
            b.append(info.getMethod());
            b.append("</td><td class=\"uri\">");
            String uri = info.getCurrentUri();
            b.append(uri.length() > 125 ? uri.substring(0, 122) + "..." : uri);

            b.append("</td><td class=\"LST\">");
            if (!r)
                b.append(df.format(new Date(info.getLastStartTime())));
            b.append("</td><td class=\"TSLS\">");
            if (r)
                //b.append(tf.formatMilli(System.currentTimeMillis() - info.getLastStartTime()));
                b.append(tf.format(info.getExecutionTimeForLastRequest()));
            b.append("</td><td class=\"threadstate"
                    + (s == State.BLOCKED ? " blocked" : s == State.RUNNABLE ? " runnable" : "") + "\">");

            b.append(s);
            b.append("</td></tr>");

            return b.toString();

        }

    }

    public View handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        if (request.getParameter("refresh") != null) {
            try {
                final int i = Integer.parseInt(request.getParameter("refresh"));
                if (i > 0) {
                    response.setHeader("refresh", Integer.toString(i));
                }
            } catch (final Throwable e) {
                // ignore
            }
        }

        if (request.getParameter("auto") != null) {
            if (request.getParameter("extendedInfo") != null || extendedInfo) {
                return fullTxtView;
            } else {
                return shortTxtView;
            }
        } else {
            return htmlView;
        }

    }

    /**
     * @return the extendedInfo
     */
    public boolean isExtendedInfo() {
        return extendedInfo;
    }

    /**
     * @param extendedInfo the extendedInfo to set
     */
    public void setExtendedInfo(final boolean extendedInfo) {
        this.extendedInfo = extendedInfo;
    }

    public void setServletContext(final ServletContext context) {
        this.servletContext = context;

    }

    /**
     * @return the servletContext
     */
    public ServletContext getServletContext() {
        return servletContext;
    }

    /**
     * @return the requestCounter
     */
    public RequestCounter getRequestCounter() {
        return requestCounter;
    }

    /**
     * @param requestCounter the requestCounter to set
     */
    public void setRequestCounter(final RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

}
