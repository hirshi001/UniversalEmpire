package com.hirshi001.game.shared.settings;

import com.hirshi001.networking.packetdecoderencoder.PacketEncoderDecoder;
import com.hirshi001.networking.packetdecoderencoder.SimplePacketEncoderDecoder;

public class Network {

    public static final PacketEncoderDecoder PACKET_ENCODER_DECODER = new SimplePacketEncoderDecoder(Integer.MAX_VALUE);
    public static final int PORT = 8080;


}
