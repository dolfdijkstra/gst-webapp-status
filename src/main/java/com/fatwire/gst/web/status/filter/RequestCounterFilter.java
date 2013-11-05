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
package com.fatwire.gst.web.status.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.fatwire.gst.web.status.RequestCounter;


/**
 * @deprecated use {@link StatusRequestListener}.
 *
 */
public class RequestCounterFilter implements Filter {
    private RequestCounter requestCounter;

    public void destroy() {
        requestCounter = null;

    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        requestCounter.start((HttpServletRequest) request);
        try {
            chain.doFilter(request, response);
        } finally {
            requestCounter.end((HttpServletRequest) request);
        }
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
    public void setRequestCounter(RequestCounter requestCounter) {
        this.requestCounter = requestCounter;
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        // do nothing
    }

}
