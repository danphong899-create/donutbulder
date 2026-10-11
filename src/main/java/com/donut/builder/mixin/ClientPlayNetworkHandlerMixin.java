package com.donut.builder.mixin;

import com.donut.builder.module.builder.SequenceTracker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ServerboundUseItemOnPacket;
import net.minecraft.network.packet.s2c.play.BlockUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "sendPacket", at = @At("HEAD"))
    private void onSend(Packet<?> packet, CallbackInfo ci) {
        if (packet instanceof ServerboundUseItemOnPacket p) {
            ((ServerboundUseItemOnPacketAccessor) p).setSequence(SequenceTracker.next());
        }
    }

    @Inject(method = "onBlockUpdate", at = @At("TAIL"))
    private void onBlockUpdate(BlockUpdateS2CPacket packet, CallbackInfo ci) {
        SequenceTracker.ack(SequenceTracker.current());
    }
}
