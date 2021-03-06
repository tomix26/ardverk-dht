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

package org.ardverk.dht.rsrc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.ardverk.io.IoUtils;
import org.ardverk.io.StreamUtils;

public abstract class AbstractValue implements Value {

  @Override
  public void writeTo(OutputStream out) throws IOException {
    long length = getContentLength();
    InputStream in = getContent();
    try {
      StreamUtils.copy(in, out, length);
    } finally {
      IoUtils.close(in);
    }
  }

  @Override
  public boolean isRepeatable() {
    return false;
  }

  @Override
  public boolean isStreaming() {
    return false;
  }
}
