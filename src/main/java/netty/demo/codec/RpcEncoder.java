package netty.demo.codec;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author lin
 * @see 封装的netty编码类，这里对传输的对象进行序列化
 */

public class RpcEncoder extends MessageToByteEncoder<Object>{

	private Class<?> genericClass;

    public RpcEncoder(Class<?> genericClass) {
        this.genericClass = genericClass;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object in, ByteBuf out) throws Exception {
        if (genericClass.isInstance(in)) {
            byte[] data = SerializationUtil.serialize(in);  //对传输对象进行序列化
            out.writeInt(data.length);   //把传输数据的大小加在传输数据前（方便后面处理粘包）
            out.writeBytes(data);
        }
    }
}
