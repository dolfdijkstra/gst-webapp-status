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

import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrencyCounterLocator {

    private static final ConcurrencyCounterLocator self = new ConcurrencyCounterLocator();

    private final ConcurrentHashMap<String, ConcurrencyCounter<?, ?>> map = new ConcurrentHashMap<String, ConcurrencyCounter<?, ?>>();

    private ConcurrencyCounterLocator() {
    }

    public ConcurrencyCounter<?, ?> locate(String name) {
        return map.get(name);
    }
    
    public Iterable<String> getNames(){
        return new HashSet<String>(map.keySet());
    }

    public ConcurrencyCounter<?, ?> register(ConcurrencyCounter<?, ?> counter) {
        return map.putIfAbsent(counter.getName(), counter);
    }

    public void deregister(ConcurrencyCounter<?, ?> counter) {
        map.remove(counter.getName());
    }

    public static final ConcurrencyCounterLocator getInstance() {
        return self;
    }
    

}
