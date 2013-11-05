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

import com.fatwire.gst.web.status.TimeFormat;

import junit.framework.TestCase;

public class TimeFormatTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMinutes() {
        TimeFormat tf = new TimeFormat();

        String x = tf.format((2 * 60000L + 30 * 1000) * 1000000);
        assertEquals("2m 30s",x);
    }

    public void testSeconds() {
        TimeFormat tf = new TimeFormat();

        String x = tf.format((30 * 1001L) * 1000000);
        assertEquals("30s 030ms",x);
    }

    public void testMilliSeconds() {
        TimeFormat tf = new TimeFormat();

        String x = tf.format(301 * 1000000L);
        assertEquals("301ms",x);
    }

    public void testMicroSeconds() {
        TimeFormat tf = new TimeFormat();

        String x = tf.format(30 * 1000L);
        assertEquals("00030us",x);
    }
    public void testMicroSeconds_100() {
        TimeFormat tf = new TimeFormat();

        String x = tf.format(1030 * 1000L);
        assertEquals("01030us",x);
    }

    
}
