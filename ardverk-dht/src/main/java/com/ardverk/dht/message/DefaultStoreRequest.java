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

package com.ardverk.dht.message;

import java.net.SocketAddress;

import org.ardverk.lang.Arguments;

import com.ardverk.dht.routing.IContact;
import com.ardverk.dht.storage.ValueTuple;

public class DefaultStoreRequest extends AbstractRequestMessage 
        implements StoreRequest {

    private final ValueTuple tuple;
    
    public DefaultStoreRequest(MessageId messageId, IContact contact, 
            SocketAddress address, ValueTuple tuple) {
        super(messageId, contact, address);
        
        this.tuple = Arguments.notNull(tuple, "tuple");
    }
    
    @Override
    public ValueTuple getValueTuple() {
        return tuple;
    }
}