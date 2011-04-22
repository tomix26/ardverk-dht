/*
 * Copyright 2009-2011 Roger Kapsi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ardverk.dht.message;

import java.net.SocketAddress;

import org.ardverk.dht.routing.Contact;
import org.ardverk.dht.storage.Status;
import org.ardverk.lang.Arguments;


public class DefaultStoreResponse extends AbstractResponseMessage 
        implements StoreResponse {

    private final Status status;
    
    public DefaultStoreResponse(MessageId messageId, Contact contact, 
            SocketAddress address, Status status) {
        super(messageId, contact, address);
        
        this.status = Arguments.notNull(status, "status");
    }
    
    @Override
    public Status getStatus() {
        return status;
    }
}