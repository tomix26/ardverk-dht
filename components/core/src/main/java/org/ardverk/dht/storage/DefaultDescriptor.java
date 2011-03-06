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

package org.ardverk.dht.storage;

import org.ardverk.dht.KUID;
import org.ardverk.dht.routing.Contact;
import org.ardverk.version.VectorClock;

public class DefaultDescriptor extends AbstractDescriptor {

    private final Contact sender;
    
    private final Contact creator;
    
    private final Resource resource;
    
    private final VectorClock<KUID> clock;
    
    public DefaultDescriptor(Contact contact, Resource resource,
            VectorClock<KUID> clock) {
        this (contact, contact, resource, clock);
    }
    
    public DefaultDescriptor(Contact sender, 
            Contact creator, Resource resource,
            VectorClock<KUID> clock) {
        this.sender = sender;
        this.creator = pickCreator(sender, creator);
        this.resource = resource;
        this.clock = clock;
    }

    @Override
    public KUID getId() {
        return resource.getId();
    }
    
    @Override
    public Resource getResource() {
        return resource;
    }
    
    @Override
    public VectorClock<KUID> getVectorClock() {
        return clock;
    }

    @Override
    public Contact getSender() {
        return sender;
    }
    
    @Override
    public Contact getCreator() {
        return creator;
    }
    
    /**
     * To save memory we're trying to re-use the {@link Contact}
     * instance if sender and creator are the same.
     */
    private static Contact pickCreator(Contact sender, Contact creator) {
        if (creator == null || sender.equals(creator)) {
            return sender;
        }
        
        return creator;
    }
}
