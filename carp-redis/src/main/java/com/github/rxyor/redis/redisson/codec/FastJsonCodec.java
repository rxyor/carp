package com.github.rxyor.redis.redisson.codec;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.rxyor.common.util.string.CharSequenceUtil;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import org.redisson.client.codec.BaseCodec;
import org.redisson.client.protocol.Decoder;
import org.redisson.client.protocol.Encoder;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019-05-23 Thu 20:22:00
 * @since 1.0.0
 */
public class FastJsonCodec extends BaseCodec {

    public static final FastJsonCodec INSTANCE = new FastJsonCodec();

    static {
        //设置Fastjson Json自动转换为Java对象
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    private final Encoder encoder = in -> {
        String json = JSON.toJSONString(in, SerializerFeature.WriteClassName);
        CharBuffer charBuffer = CharBuffer.wrap(CharSequenceUtil.string2CharArray(json));
        return ByteBufUtil.encodeString(ByteBufAllocator.DEFAULT, charBuffer, Charset.forName("utf-8"));
    };

    private final Decoder<Object> decoder = (buf, state) -> {
        byte[] dst = new byte[buf.capacity()];
        buf.readBytes(dst);
        String json = CharSequenceUtil.bytes2String(dst);
        return JSON.parse(json);
    };

    @Override
    public Encoder getValueEncoder() {
        return encoder;
    }

    @Override
    public Decoder<Object> getValueDecoder() {
        return decoder;
    }
}
