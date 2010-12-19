/*
 * Copyright 2009-2010 Roger Kapsi
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

package com.ardverk.dht;

import java.io.Closeable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import com.ardverk.dht.concurrent.ArdverkFuture;
import com.ardverk.dht.concurrent.ArdverkProcess;
import com.ardverk.dht.config.Config;
import com.ardverk.dht.config.PingConfig;
import com.ardverk.dht.entity.PingEntity;

/**
 * An abstract implementation of {@link DHT}.
 */
abstract class AbstractDHT implements DHT, Closeable {

    private final FutureManager futureManager = new FutureManager();
    
    @Override
    public void close() {
        futureManager.close();
    }
    
    @Override
    public ArdverkFuture<PingEntity> ping(InetAddress address, 
            int port, PingConfig config) {
        return ping(new InetSocketAddress(address, port), config);
    }
    
    @Override
    public ArdverkFuture<PingEntity> ping(String address, 
            int port, PingConfig config) {
        return ping(new InetSocketAddress(address, port), config);
    }
    
    @Override
    public <V> ArdverkFuture<V> submit(ArdverkProcess<V> process, Config config) {
        ExecutorKey executorKey = config.getExecutorKey();
        long timeout = config.getOperationTimeoutInMillis();
        return submit(executorKey, process, timeout, TimeUnit.MILLISECONDS);
    }

    @Override
    public <V> ArdverkFuture<V> submit(ExecutorKey executorKey,
            ArdverkProcess<V> process, long timeout, TimeUnit unit) {
        return futureManager.submit(executorKey, process, timeout, unit);
    }
    
    @Override
    public String toString() {
        return getRouteTable().getLocalhost().toString();
    }
}