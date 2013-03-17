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

package org.ardverk.dht.io.transport;

import org.ardverk.concurrent.ExecutorUtils;
import org.ardverk.dht.KUID;
import org.ardverk.dht.codec.MessageCodec;
import org.ardverk.dht.codec.MessageCodec.Decoder;
import org.ardverk.dht.codec.MessageCodec.Encoder;
import org.ardverk.dht.codec.bencode.BencodeMessageCodec;
import org.ardverk.dht.message.Message;
import org.ardverk.dht.message.RequestMessage;
import org.ardverk.dht.message.ResponseMessage;
import org.ardverk.dht.utils.Idle;
import org.ardverk.io.IoUtils;
import org.ardverk.io.ProgressInputStream;
import org.ardverk.net.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class SocketTransport extends AbstractTransport implements Closeable {

  private static final Logger LOG 
    = LoggerFactory.getLogger(SocketTransport.class);
  
  private static final ExecutorService EXECUTOR 
    = ExecutorUtils.newCachedThreadPool("SocketTransportThread");
  
  private static final int DEFAULT_TIMEOUT = 10000;
  
  private final MessageCodec codec;
  
  private final SocketAddress bindaddr;
  
  private volatile ServerSocket socket = null;
  
  private Future<?> future = null;
  
  private boolean open = true;
  
  public SocketTransport(MessageCodec codec, int port) {
    this(codec, new InetSocketAddress(port));
  }
  
  public SocketTransport(MessageCodec codec, 
      String bindaddr, int port) {
    this(codec, new InetSocketAddress(bindaddr, port));
  }
  
  public SocketTransport(MessageCodec codec, 
      InetAddress bindaddr, int port) {
    this(codec, new InetSocketAddress(bindaddr, port));
  }
  
  public SocketTransport(SocketAddress bindaddr) {
    this(new BencodeMessageCodec(), bindaddr);
  }
  
  public SocketTransport(MessageCodec codec, 
      SocketAddress bindaddr) {
    this.codec = codec;
    this.bindaddr = bindaddr;
  }
  
  @Override
  public SocketAddress getSocketAddress() {
    return bindaddr;
  }
  
  @Override
  public synchronized void bind(TransportCallback callback) throws IOException {
    if (!open) {
      throw new IOException();
    }
    
    super.bind(callback);
  
    socket = new ServerSocket();
    socket.setReuseAddress(true);
    //socket.setReceiveBufferSize(64*1024);
    //socket.bind(bindaddr, 512);
    socket.bind(bindaddr);
    
    Runnable task = new Runnable() {
      @Override
      public void run() {
        doServe();
      }
    };
    
    future = EXECUTOR.submit(task);
  }
  
  @Override
  public synchronized void unbind() {
    super.unbind();
    
    IoUtils.close(socket);
    
    if (future != null) {
      future.cancel(true);
    }
  }
  
  @Override
  public synchronized void close() {
    open = false;
    unbind();
  }
  
  private void doServe() {
    ServerSocket socket = null;
    Socket client = null;
    
    while ((socket = this.socket) != null 
        && !socket.isClosed()) {
      
      boolean processing = false;
      try {
        client = socket.accept();
        
        configure(client);
        processing = receive(client);
        
      } catch (IOException err) {
        uncaughtException(socket, err);
      } finally {
        if (!processing) {
          IoUtils.close(client);
        }
      }
    }
  }
  
  private boolean receive(final Socket client) {
    Runnable task = new Runnable() {
      @Override
      public void run() {
        Decoder decoder = null;
        Encoder encoder = null;
        
        try {
          decoder = createDecoder(client);
          
          RequestMessage request = (RequestMessage)decoder.read();
          ResponseMessage response = handleRequest(request);
          
          if (response != null) {
            encoder = createEncoder(client);
          
            encoder.write(response);
            encoder.flush();
          }
          
        } catch (IOException err) {
          uncaughtException(client, err);
        } finally {
          close(client, encoder, decoder);
        }
      }
    };
    
    EXECUTOR.execute(task);
    return true;
  }
  
  @Override
  public void send(KUID contactId, final Message request, 
      final long timeout, final TimeUnit unit) throws IOException {
    
    ServerSocket socket = this.socket;
    if (socket == null || socket.isClosed()) {
      throw new IOException();
    }
    
    Runnable task = new Runnable() {
      @Override
      public void run() {
        Socket client = null;
        Encoder encoder = null;
        Decoder decoder = null;
        
        try {
          client = new Socket();
          configure(client);
          
          int timeoutInMillis = (int)unit.toMillis(timeout);
          if (timeoutInMillis < 0) {
            timeoutInMillis = DEFAULT_TIMEOUT;
          }
          
          SocketAddress addr = request.getAddress();
          SocketAddress endpoint 
            = NetworkUtils.getResolved(addr);
          
          client.connect(endpoint, timeoutInMillis);
          
          encoder = createEncoder(client);
          encoder.write(request);
          encoder.flush();

          decoder = createDecoder(client, null);
          
          ResponseMessage response = (ResponseMessage)decoder.read();
          handleResponse(response);
          
        } catch (Exception err) {
          uncaughtException(client, err);
          handleException(request, err);
          
        } finally {
          close(client, encoder, decoder);
        }
      }
    };
    
    EXECUTOR.execute(task);
  }
  
  private Encoder createEncoder(Socket client) throws IOException {
    return codec.createEncoder(new BufferedOutputStream(
          client.getOutputStream()));
  }
  
  private Decoder createDecoder(Socket client) throws IOException {
    return createDecoder(client, null);
  }
  
  private Decoder createDecoder(Socket client, final Idle idle) throws IOException {
    SocketAddress addr = client.getRemoteSocketAddress();
    
    InputStream in = new BufferedInputStream(
        new SocketInputStream(client));
    
    if (idle != null) {
      in = new ProgressInputStream(in, idle);
    }
    
    return codec.createDecoder(addr, in);
  }

  private static void configure(Socket client) throws SocketException {
    client.setSoLinger(true, 0);
  }
  
  private static void uncaughtException(ServerSocket socket, Throwable t) {
    uncaughtException(socket.isClosed(), t);
  }
  
  private static void uncaughtException(Socket socket, Throwable t) {
    uncaughtException(socket.isClosed(), t);
  }
  
  private static void uncaughtException(boolean closed, Throwable t) {
    if (closed) {
      LOG.info("Exception", t);
    } else {
      LOG.error("Exception", t);
    }
  }
  
  private static void close(Socket client, Closeable... closeable) {
    IoUtils.closeAll(closeable);
    IoUtils.close(client);
  }
  
  /*private static InputStream getInputStream(final Socket client) throws IOException {
    final Object lock = new Object();
    synchronized (lock) {
      IdleCallback callback = new IdleAdapter() {
        @Override
        public void idle(InputStream in, long time, TimeUnit unit) {
          SocketTransport.close(client, in);
        }

        @Override
        public void eof(InputStream in) {
          SocketTransport.close(client, in);
        }

        @Override
        public void closed(InputStream in) {
          SocketTransport.close(client, in);
        }
      };
      
      return new IdleInputStream(new BufferedInputStream(
          client.getInputStream()), 
          callback, 5L, 5L, TimeUnit.SECONDS);
    }
  }*/
  
  private static class SocketInputStream extends FilterInputStream {
    
    private boolean open = true;
    
    public SocketInputStream(Socket socket) throws IOException {
      super(socket.getInputStream());
    }

    @Override
    public int read() throws IOException {
      if (!open) {
        throw new IOException();
      }
      return super.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
      if (!open) {
        throw new IOException();
      }
      return super.read(b, off, len);
    }

    @Override
    public void close() throws IOException {
      open = false;
    }
  }
}