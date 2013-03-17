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

package org.ardverk.dht.codec.bencode;

import org.ardverk.coding.BencodingOutputStream;
import org.ardverk.dht.KUID;
import org.ardverk.dht.lang.IntegerValue;
import org.ardverk.dht.lang.StringValue;
import org.ardverk.dht.message.*;
import org.ardverk.dht.routing.Contact;
import org.ardverk.dht.rsrc.Key;
import org.ardverk.dht.rsrc.Value;
import org.ardverk.version.Vector;
import org.ardverk.version.VectorClock;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;


/**
 * The {@link MessageOutputStream} writes {@link Message}s to a
 * {@link BencodingOutputStream}.
 */
public class MessageOutputStream extends BencodingOutputStream {
  
  public MessageOutputStream(OutputStream out) {
    super(out);
  }

  @Override
  protected void writeCustom(Object obj) throws IOException {
    if (obj instanceof InetAddress) {
      writeInetAddress((InetAddress)obj);
    } else if (obj instanceof SocketAddress) {
      writeSocketAddress((SocketAddress)obj);
    } else if (obj instanceof KUID) {
      writeKUID((KUID)obj);
    } else if (obj instanceof MessageId) {
      writeMessageId((MessageId)obj);
    } else if (obj instanceof Contact) {
      writeContact((Contact)obj);
    } else if (obj instanceof Message) {
      writeMessage((Message)obj);
    } else if (obj instanceof Key) {
      writeKey((Key)obj);
    } else {
      super.writeCustom(obj);
    }
  }
  
  @Override
  public void writeEnum(Enum<?> value) throws IOException {
    if (value instanceof IntegerValue) {
      writeInt(((IntegerValue)value).intValue());
    } else if (value instanceof StringValue) {
      writeString(((StringValue)value).stringValue());
    } else {
      super.writeEnum(value);
    }
  }

  public void writeInetAddress(InetAddress address) throws IOException {
    writeString(address.getHostName());
  }
  
  public void writeSocketAddress(SocketAddress sa) throws IOException {
    InetSocketAddress isa = (InetSocketAddress)sa;
    writeString(isa.getHostName() + ":" + isa.getPort());
  }
  
  public void writeKey(Key key) throws IOException {
    writeString(key.getURI().toString());
  }
  
  public void writeVectorClock(VectorClock<? extends KUID> clock) throws IOException {
    int size = 0;
    if (clock != null) {
      size = clock.size();
    }
    
    writeShort(size);
    
    if (0 < size) {
      writeLong(clock.getCreationTime());
      for (Map.Entry<? extends KUID, ? extends Vector> entry 
          : clock.entrySet()) {
        KUID contactId = entry.getKey();
        Vector vector = entry.getValue();
        
        writeKUID(contactId);
        writeVector(vector);
      }
    }
  }
  
  private void writeVector(Vector vector) throws IOException {
    writeLong(vector.getTimeStamp());
    writeInt(vector.getValue());
  }
  
  public void writeKUID(KUID kuid) throws IOException {
    writeBytes(kuid.getBytes());
  }
  
  public void writeMessageId(MessageId messageId) throws IOException {
    writeBytes(messageId.getBytes());
  }
  
  public void writeSender(Contact contact) throws IOException {
    writeKUID(contact.getId());
    writeInt(contact.getInstanceId());
    writeBoolean(contact.isHidden());
    writeSocketAddress(contact.getRemoteAddress());
  }
  
  public void writeContact(Contact contact) throws IOException {
    writeKUID(contact.getId());
    writeSocketAddress(contact.getRemoteAddress());
  }
  
  public void writeContacts(Contact[] contacts) throws IOException {
    writeArray(contacts);
  }
  
  public void writeValue(Value value) throws IOException {
    writeLong(value.getContentLength());
    value.writeTo(this);
  }
  
  public void writeMessage(Message message) throws IOException {
    
    writeByte(Constants.VERSION);
    
    OpCode opcode = OpCode.valueOf(message);
    writeEnum(opcode);
    writeMessageId(message.getMessageId());
    
    // Write the source and destination
    writeSender(message.getContact());
    writeSocketAddress(message.getAddress());
    
    switch (opcode) {
      case PING_REQUEST:
        writePingRequest((PingRequest)message);
        break;
      case PING_RESPONSE:
        writePingResponse((PingResponse)message);
        break;
      case FIND_NODE_REQUEST:
        writeNodeRequest((NodeRequest)message);
        break;
      case FIND_NODE_RESPONSE:
        writeNodeResponse((NodeResponse)message);
        break;
      case FIND_VALUE_REQUEST:
        writeValueRequest((ValueRequest)message);
        break;
      case FIND_VALUE_RESPONSE:
        writeValueResponse((ValueResponse)message);
        break;
      case STORE_REQUEST:
        writeStoreRequest((StoreRequest)message);
        break;
      case STORE_RESPONSE:
        writeStoreResponse((StoreResponse)message);
        break;
      default:
        throw new IllegalArgumentException("opcode=" + opcode);
    }
  }
  
  private void writePingRequest(PingRequest message) throws IOException {
  }
  
  private void writePingResponse(PingResponse message) throws IOException {
  }
  
  private void writeNodeRequest(NodeRequest message) throws IOException {
    writeKUID(message.getId());
  }
  
  private void writeNodeResponse(NodeResponse message) throws IOException {
    Contact[] contacts = message.getContacts();
    
    writeInt(contacts.length);
    for (Contact contact : contacts) {
      writeContact(contact);
    }
  }
  
  private void writeValueRequest(ValueRequest message) throws IOException {
    writeKey(message.getKey());
  }
  
  private void writeValueResponse(ValueResponse message) throws IOException {
    writeValue(message.getValue());
  }
  
  private void writeStoreRequest(StoreRequest message) throws IOException {
    writeKey(message.getKey());
    writeValue(message.getValue());
  }
  
  private void writeStoreResponse(StoreResponse message) throws IOException {
    writeValue(message.getValue());
  }
}