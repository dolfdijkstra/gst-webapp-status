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

import java.util.Collection;

public interface ConcurrencyCounter<T, E> {

    void start(T t);

    long end(T t);

    long getTotalCount();

    int getConcurrencyCount();

    int getPeakConcurrencyCount();

    Collection<E> getCurrentExecutingOperations();

    String getName();

    void reset();
}
