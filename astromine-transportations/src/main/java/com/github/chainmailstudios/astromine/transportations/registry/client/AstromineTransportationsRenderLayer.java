package com.github.chainmailstudios.astromine.transportations.registry.client;

import com.github.chainmailstudios.astromine.registry.client.AstromineRenderLayers;
import com.github.chainmailstudios.astromine.transportations.registry.AstromineTransportationsBlocks;
import net.minecraft.client.render.RenderLayer;

public class AstromineTransportationsRenderLayer extends AstromineRenderLayers {
	public static void initialize() {
		register(AstromineTransportationsBlocks.ALTERNATOR, RenderLayer.getCutout());
		register(AstromineTransportationsBlocks.SPLITTER, RenderLayer.getCutout());
		register(AstromineTransportationsBlocks.INCINERATOR, RenderLayer.getCutout());
	}
}