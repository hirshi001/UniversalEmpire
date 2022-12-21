package com.hirshi001.game.shared.settings;

import com.hirshi001.game.shared.util.ByteCounterPacketEncoderDecoder;
import com.hirshi001.networking.packetdecoderencoder.PacketEncoderDecoder;
import com.hirshi001.networking.packetdecoderencoder.SimplePacketEncoderDecoder;

public class Network {

    public static final ByteCounterPacketEncoderDecoder PACKET_ENCODER_DECODER = new ByteCounterPacketEncoderDecoder(new SimplePacketEncoderDecoder(Integer.MAX_VALUE));
    public static final int PORT = 80;


}
