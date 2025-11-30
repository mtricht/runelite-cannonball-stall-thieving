package eu.tricht;

import eu.tricht.config.StallMode;
import net.runelite.client.config.*;

@ConfigGroup("Cannonball Stall Thieving")
public interface CannonballStallThievingConfig extends Config {

    @ConfigItem(
            keyName = "stallMode",
            name = "Stalls mode",
            description = "Configures which stalls will be highlighted.",
            position = 1
    )
    default StallMode stallHighlight() {
        return StallMode.ORE;
    }

    @ConfigItem(
            keyName = "borderWidth",
            name = "Border width",
            description = "Width of the outlined border.",
            position = 2
    )
    default int borderWidth() {
        return 4;
    }

    @ConfigItem(
            keyName = "outlineFeather",
            name = "Outline feather",
            description = "Specify between 0-4 how much of the stall model outline should be faded.",
            position = 3
    )
    @Range(
            max = 4
    )
    default int outlineFeather() {
        return 4;
    }
}
