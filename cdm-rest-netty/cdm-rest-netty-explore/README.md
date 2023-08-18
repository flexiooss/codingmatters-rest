
# Docs

[Netty's Home](https://netty.io/)

[User guide 4.x](https://netty.io/wiki/user-guide-for-4.x.html)

[Netty Documentation](https://netty.io/wiki/) 

# Notes    

## ChannelInitializer

Configures the pipeline. Processor will be instantiated here.

## Decoder

Decorder can read incoming bytes and transform them into an object. That's the reason of the Handler's method 
signature :
```java
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
```

msg is an object added to the out parameter of the Decoder's decode method :
```java
public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out)
```

Similarly, server side, a `MessageToByteEncoder` can be used to encode an object written to the context (i.e., 
`ctx.write(obj)` or `ctx.writeAndFlush(obj)`).



# To Lookup List

- boss / worker groups dimensionning
- HttpObjectAggregator param ? good for file uploads ??
