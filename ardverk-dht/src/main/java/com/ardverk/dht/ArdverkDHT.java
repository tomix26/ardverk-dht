package com.ardverk.dht;

import com.ardverk.dht.io.transport.Transport;

public class ArdverkDHT {

    private final NodeManager nodeManager 
        = new NodeManager();
    
    private final Transport transport;
    
    public ArdverkDHT(Transport transport, KUID nodeId) {
        if (transport == null) {
            throw new NullPointerException("transport");
        }
        
        if (nodeId == null) {
            throw new NullPointerException("nodeId");
        }
        
        this.transport = transport;
        
        nodeManager.add(nodeId);
    }
    
    public Transport getTransport() {
        return transport;
    }
}
