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


public class TimeFormat {
    static final long MILLI = 1000000L;

    static final long HUNDRED_MILLI = 1000000L * 10;
    static final long SECOND = 1000L * MILLI;

    static final long MINUTE = 60 * TimeFormat.SECOND;

    static final long MS_SECOND = 1000L;

    static final long MS_MINUTE = 60 * TimeFormat.MS_SECOND;

    public String format(final long t) {
        final int ms = (int) (t / TimeFormat.MILLI);

        if (t > TimeFormat.MINUTE) {
            final long m = t / TimeFormat.MINUTE;
            final long s = (t / TimeFormat.SECOND) - (m * 60);
            final StringBuilder b = new StringBuilder();
            b.append(m).append("m ");
            pad2(b, s);
            b.append("s");
            return b.toString();
        } else if (t > TimeFormat.SECOND) {
            final StringBuilder b = new StringBuilder();
            pad2(b, ms / 1000);
            b.append("s ");

            final int rms = (ms % 1000);
            pad3(b, rms);
            b.append("ms");
            return b.toString();
        } else if (t < TimeFormat.HUNDRED_MILLI) {
            final StringBuilder b = new StringBuilder();
            pad5(b, (int) (t / 1000));
            b.append("us");
            return b.toString();
        } else {
            final StringBuilder b = new StringBuilder();
            pad3(b, ms);
            b.append("ms");
            return b.toString();
        }
    }

    public String formatMilli(final long t) {
        int ms = (int) t;

        if (t > TimeFormat.MS_MINUTE) {
            final long m = t / TimeFormat.MS_MINUTE;
            final long s = (t / TimeFormat.MS_SECOND) - (m * 60);
            final StringBuilder b = new StringBuilder();
            b.append(m).append("m ");
            pad2(b, s);
            b.append("s");
            return b.toString();
        } else if (t > TimeFormat.MS_SECOND) {
            final StringBuilder b = new StringBuilder();
            pad2(b, ms / 1000);
            b.append("s ");

            final int rms = (ms % 1000);
            pad3(b, rms);
            b.append("ms");
            return b.toString();
        } else if (t < 1000) {
            final StringBuilder b = new StringBuilder();
            pad3(b, (int) (t / 1000));
            b.append("us");
            return b.toString();
        } else {
            final StringBuilder b = new StringBuilder();
            pad3(b, ms);
            b.append("ms");
            return b.toString();

        }
    }

    private void pad5(final StringBuilder b, final long v) {
        if (v < 10) {
            b.append("0000");
        } else if (v < 100) {
            b.append("000");
        } else if (v < 1000) {
            b.append("00");
        } else if (v < 10000) {
            b.append("0");
        }
        b.append(v);

    }

    private void pad3(final StringBuilder b, final long v) {
        if (v < 10) {
            b.append("00");
        } else if (v < 100) {
            b.append("0");
        }
        b.append(v);

    }

    private void pad2(final StringBuilder b, final long v) {
        if (v < 10) {
            b.append("0");
        }
        b.append(v);

    }

}
