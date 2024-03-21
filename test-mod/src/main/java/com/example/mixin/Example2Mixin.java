package com.example.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class Example2Mixin {
	@Inject(at = @At("HEAD"), method = "init")
	private void init(CallbackInfo ci) {
		// This code is injected into the start of MinecraftServer.loadWorld()V
		System.out.println("Injected into method_2106");
	}
}