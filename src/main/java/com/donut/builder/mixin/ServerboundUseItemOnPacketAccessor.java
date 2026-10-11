package com.donut.builder.mixin;

import net.minecraft.network.packet.c2s.play.ServerboundUseItemOnPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundUseItemOnPacket.class)
public interface ServerboundUseItemOnPacketAccessor {
    @Accessor("sequence") void setSequence(int sequence);
    @Accessor("sequence") int getSequence();
}
