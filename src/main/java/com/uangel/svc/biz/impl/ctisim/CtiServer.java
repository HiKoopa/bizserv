package com.uangel.svc.biz.impl.ctisim;

import io.netty.channel.Channel;
import io.netty.channel.ChannelOutboundInvoker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class CtiServer {
    Channel serverChannel;


    public void Bind(Channel serverChannel) {
        this.serverChannel = serverChannel;


    }

    public void close() {
        serverChannel.close();

        lock.lock();
        try {
            connections.forEach(ChannelOutboundInvoker::close);
            connections = new ArrayList<>();
        } finally {
            lock.unlock();
        }
    }

    ReentrantLock lock = new ReentrantLock();

    List<Channel> connections = new ArrayList<>();

    public void channelConnected(Channel channel) {
        lock.lock();
        try {
            connections.add(channel);
        } finally {
            lock.unlock();
        }
    }

    public void channelDisconnected(Channel channel) {
        lock.lock();
        try {
            connections.remove(channel);
        } finally {
            lock.unlock();
        }
    }
}
