package netty.demo.codec;


import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * @see 封装的netty编码类，继承ByteToMessageDecoder，这里对传输的对象进行反序列化，处理半包问题。  或者也基于其他策略进行处理
 */
public class RpcDecoder extends ByteToMessageDecoder{

	private Class<?> genericClass;

    public RpcDecoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public final void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            return;
        }
        in.markReaderIndex();
        int dataLength = in.readInt();
        if (dataLength < 0) {
            ctx.close();
        }
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            return ;
        }
        byte[] data = new byte[dataLength];
        try {
			in.readBytes(data);
		} catch (Exception e) {
			e.printStackTrace();
		}

        Object obj = SerializationUtil.deserialize(data, genericClass);
        out.add(obj);
    }
}
