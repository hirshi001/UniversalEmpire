package com.hirshi001.game.shared.util;

import com.hirshi001.buffer.buffers.ByteBuffer;
import com.hirshi001.networking.packet.DataPacket;
import com.hirshi001.networking.packet.Packet;
import com.hirshi001.networking.packetdecoderencoder.PacketEncoderDecoder;
import com.hirshi001.networking.packethandlercontext.PacketHandlerContext;
import com.hirshi001.networking.packethandlercontext.PacketType;
import com.hirshi001.networking.packetregistry.PacketRegistry;
import com.hirshi001.networking.packetregistrycontainer.PacketRegistryContainer;

import java.util.concurrent.atomic.AtomicInteger;

public class ByteCounterPacketEncoderDecoder implements PacketEncoderDecoder {

    private PacketEncoderDecoder encoderDecoder;

    public AtomicInteger decodedBytes = new AtomicInteger(0), encodedBytes = new AtomicInteger(0);
    public AtomicInteger maxPacketSize = new AtomicInteger(0);

    public ByteCounterPacketEncoderDecoder(PacketEncoderDecoder encoderDecoder){
        this.encoderDecoder = encoderDecoder;
    }


    @Override
    public PacketHandlerContext<?> decode(PacketRegistryContainer container, ByteBuffer in, PacketHandlerContext<?> context) {
        int startIndex = in.readerIndex();
        PacketHandlerContext<?> ctx = encoderDecoder.decode(container, in, context);
        if(ctx==null) return null;
        if(ctx.packetType==PacketType.TCP) decodedBytes.addAndGet(in.readerIndex() - startIndex);
        return ctx;
    }

    @Override
    public void encode(PacketHandlerContext ctx, DataPacket dataPacket, PacketRegistryContainer container, ByteBuffer out) {
        if(ctx.packetType==PacketType.UDP && Math.random()<1D) return; // emulate sending a bad packet
        int startIndex = out.writerIndex();
        encoderDecoder.encode(ctx, dataPacket, container, out);
        encodedBytes.addAndGet(out.writerIndex() - startIndex);
        maxPacketSize.getAndUpdate(x -> Math.max(x, out.writerIndex() - startIndex));
    }
}
