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

package org.ardverk.dht.io;

import java.io.IOException;

import org.ardverk.dht.message.RequestMessage;
import org.ardverk.dht.message.ResponseMessage;


/**
 * Classes that process {@link RequestMessage}s may implement this interface.
 */
public interface RequestHandler {
  
  /**
   * Called for every {@link RequestMessage} we're receiving.
   */
  public ResponseMessage handleRequest(RequestMessage request) throws IOException;
}