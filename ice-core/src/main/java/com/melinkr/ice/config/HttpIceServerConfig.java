package com.melinkr.ice.config;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.net.InetSocketAddress;
import java.util.Set;

/**
 * Created by <a href="mailto:xiegengcai@gmail.com">Xie Gengcai</a> on 2016/8/19.
 */
@Configuration
//@ComponentScan("com.melinkr.ice")
@PropertySource("conf/iceServer.properties")
public class HttpIceServerConfig implements IceServerConfig {
    @Value("${server.port:8080}")
    private int port;
    @Value("${server.maxContentLength:67108864}")
    private int maxContentLength;
    @Value("#{'${black.ip}'.split(';')}")
    private Set<String> ipBlacklist;

    @Value("${boss.thread.bossThreadSize}")
    private int bossThreadSize;

    @Value("${boss.thread.wokerThreadSize}")
    private int wokerThreadSize;

    private NioEventLoopGroup bossGroup;
    private NioEventLoopGroup workerGroup;
    private InetSocketAddress socketAddress;

//    @PostConstruct
//    public void init(){
//        this.bossGroup = new NioEventLoopGroup(bossThreadSize);
//        this.workerGroup = new NioEventLoopGroup(wokerThreadSize);
//        this.socketAddress = new InetSocketAddress(this.port);
//    }

    @Override
    public int port() {
        return this.port;
    }

    @Override
    public int maxContentLength() {
        return this.maxContentLength;
    }

    @Override
    public Set<String> ipBlacklist() {
        return this.ipBlacklist;
    }


    @Override
    @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup bossGroup() {
        return new NioEventLoopGroup(bossThreadSize);
//        return this.bossGroup;
    }

    @Override
    @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
    public NioEventLoopGroup workerGroup() {
        return new NioEventLoopGroup(wokerThreadSize);
//        return this.workerGroup;
    }

    @Override
    @Bean(name = "socketAddress")
    public InetSocketAddress socketAddress() {
//        return this.socketAddress;
        return new InetSocketAddress(this.port);
    }
}
