package eu.tricht;

import eu.tricht.config.StallMode;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.outline.ModelOutlineRenderer;

import javax.inject.Inject;
import java.awt.*;

@Slf4j
public class CannonballStallThievingOverlay extends Overlay {

    private final CannonballStallThievingPlugin plugin;
    private final CannonballStallThievingConfig config;
    private final ModelOutlineRenderer          modelOutlineRenderer;

    @Inject
    public CannonballStallThievingOverlay(CannonballStallThievingPlugin plugin, CannonballStallThievingConfig config, ModelOutlineRenderer modelOutlineRenderer) {
        this.plugin = plugin;
        this.config = config;
        this.modelOutlineRenderer = modelOutlineRenderer;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!plugin.playerInPortRoberts() || plugin.getCannonballStall() == null || plugin.getSilkStall() == null || plugin.getOreStall() == null) {
            return null;
        }
        var cannonballStall = plugin.isCannonballCurrentlySafe() ? Color.GREEN : Color.RED;
        var otherStall = plugin.isOtherCurrentlySafe() ? Color.GREEN : Color.RED;
        modelOutlineRenderer.drawOutline(plugin.getCannonballStall(), config.borderWidth(), cannonballStall, config.outlineFeather());
        if (config.stallHighlight() == StallMode.ORE) {
            modelOutlineRenderer.drawOutline(plugin.getOreStall(), config.borderWidth(), otherStall, config.outlineFeather());
        } else if (config.stallHighlight() == StallMode.SILK) {
            modelOutlineRenderer.drawOutline(plugin.getSilkStall(), config.borderWidth(), otherStall, config.outlineFeather());
        }
        return null;
    }
}
