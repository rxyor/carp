package com.github.rxyor.common.netty.httpclient.core;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpResponse;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-07-10 Wed 15:40:00
 * @since 1.0.0
 */
public class HttpClientInboundHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if(msg instanceof HttpResponse){

        }
    }
}
