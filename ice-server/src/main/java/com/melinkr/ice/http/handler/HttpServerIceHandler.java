package com.melinkr.ice.http.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.common.base.Throwables;
import com.melinkr.ice.IceHandler;
import com.melinkr.ice.codec.SimpleQueryStringDecoder;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by <a href="mailto:xiegengcai@gmail.com">Xie Gengcai</a> on 2016/8/19.
 */
//@Component("serverHandler")
@ChannelHandler.Sharable
public class HttpServerIceHandler extends IceHandler {


    private Map<String, Object> params;

    private final DefaultHttpDataFactory httpDataFactory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);;

    @Override
    protected String service(FullHttpRequest request) {
        params = new SimpleQueryStringDecoder(request.uri()).parameters();
        if (params.size() == 0) { // Collections.EMPTY_MAP
            params = new HashMap<>();
        }
        // 处理POST数据
        if (HttpMethod.POST == request.method()) {
            HttpPostRequestDecoder  decoder = new HttpPostRequestDecoder (httpDataFactory, request);
            decoder.getBodyHttpDatas().stream().filter(httpData -> httpData.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute).forEach(httpData -> {
                Attribute attribute = (Attribute) httpData;
                try {
                    params.put(attribute.getName(), attribute.getValue());
                } catch (IOException e) {
                    logger.warn(Throwables.getStackTraceAsString(e));
                }
            });
        }
        String httpContent = request.content().toString(CharsetUtil.UTF_8).trim();
        // POST JSON DATA
        if (StringUtils.hasText(httpContent)) {
            if (httpContent.startsWith("{") && httpContent.endsWith("}")) {
                params.putAll(JSON.parseObject(httpContent, new TypeReference<Map<String, Object>>(){}));
            }
        }
        return JSON.toJSONString(params);
    }
}
