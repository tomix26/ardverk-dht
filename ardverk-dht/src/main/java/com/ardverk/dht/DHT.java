package com.ardverk.dht;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

import com.ardverk.dht.concurrent.ArdverkFuture;
import com.ardverk.dht.entity.NodeEntity;
import com.ardverk.dht.entity.PingEntity;
import com.ardverk.dht.entity.StoreEntity;
import com.ardverk.dht.entity.ValueEntity;
import com.ardverk.dht.io.transport.Transport;
import com.ardverk.dht.routing.Contact2;
import com.ardverk.dht.routing.RouteTable;
import com.ardverk.dht.storage.Database;

/**
 * 
 */
interface DHT {

    /**
     * 
     */
    public Transport getTransport();
    
    /**
     * 
     */
    public RouteTable getRouteTable();
    
    /**
     * 
     */
    public Database getDatabase();
    
    /**
     * 
     */
    public Contact2 getContact(KUID contactId);
    
    /**
     * 
     */
    //public ArdverkFuture<PingEntity> bootstrap(Contact contact, 
    //        long timeout, TimeUnit unit);
    
    /**
     * Sends a ping to the given host.
     */
    public ArdverkFuture<PingEntity> ping(String address, int port, 
            long timeout, TimeUnit unit);
    
    /**
     * Sends a ping to the given host.
     */
    public ArdverkFuture<PingEntity> ping(InetAddress address, int port, 
            long timeout, TimeUnit unit);

    /**
     * Sends a ping to the given host.
     */
    public ArdverkFuture<PingEntity> ping(SocketAddress dst, 
            long timeout, TimeUnit unit);
    
    /**
     * Sends a ping to the given host.
     */
    public ArdverkFuture<PingEntity> ping(Contact2 contact, 
            long timeout, TimeUnit unit);
    
    /**
     * 
     */
    public ArdverkFuture<StoreEntity> put(KUID key, byte[] value, 
            long timeout, TimeUnit unit);
    
    /**
     * 
     */
    public ArdverkFuture<ValueEntity> get(KUID key, 
            long timeout, TimeUnit unit);
    
    /**
     * 
     */
    public ArdverkFuture<NodeEntity> lookup(KUID key, 
            long timeout, TimeUnit unit);
}
