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

import org.ardverk.dht.lang.Identifier;
import org.ardverk.dht.routing.Contact;
import org.ardverk.lang.Age;
import org.ardverk.lang.Epoch;


/**
 * A {@link ValueTuple} is at its core a simple key-value pair
 * that holds additional information such as the creator and 
 * sender of the value, when it was created and what it's current
 * age is.
 */
public interface ValueTuple extends Identifier, Epoch, Age {
    
    /**
     * Returns the sender of the {@link ValueTuple}.
     */
    public Contact getSender();
    
    /**
     * Returns the creator of the {@link ValueTuple}.
     */
    public Contact getCreator();
    
    /**
     * Returns the value of the {@link ValueTuple}.
     */
    public Value getValue();
    
    /**
     * Returns the {@link Value}'s content length.
     * 
     * @see #getValue()
     */
    public long getContentLength();
    
    /**
     * Returns {@code true} if the value is empty.
     */
    public boolean isEmpty();
}