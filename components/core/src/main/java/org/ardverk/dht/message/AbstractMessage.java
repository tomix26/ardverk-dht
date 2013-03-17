/*
 * Copyright 2009-2012 Roger Kapsi
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

import org.ardverk.dht.routing.Contact;
import org.ardverk.lang.Epoch;

import java.net.SocketAddress;

/**
 * An abstract implementation of {@link Message}.
 */
public abstract class AbstractMessage implements Message, Epoch {

  private final long creationTime = System.currentTimeMillis();
  
  private final MessageId messageId;
  
  private final Contact contact;
  
  private final SocketAddress address;
  
  public AbstractMessage(MessageId messageId, Contact contact, 
      SocketAddress address) {
    
    this.messageId = messageId;
    this.contact = contact;
    this.address = address;
  }
  
  @Override
  public long getCreationTime() {
    return creationTime;
  }
  
  @Override
  public MessageId getMessageId() {
    return messageId;
  }

  @Override
  public Contact getContact() {
    return contact;
  }
  
  @Override
  public SocketAddress getAddress() {
    return address;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() 
      + "(" + messageId + ", " + contact + ", " + address + ")";
  }
}