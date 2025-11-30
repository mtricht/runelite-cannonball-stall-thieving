package eu.tricht;

import com.google.inject.Provides;
import eu.tricht.config.StallMode;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameObject;
import net.runelite.api.GameState;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.Set;

@Slf4j
@PluginDescriptor(
        name = "Cannonball Stall Thieving",
        tags = {"cannonball", "port roberts", "thieving", "stall"}
)
public class CannonballStallThievingPlugin extends Plugin {
    @Inject
    private Client                         client;
    @Inject
    private OverlayManager                 overlayManager;
    @Inject
    private CannonballStallThievingConfig  config;
    @Inject
    private CannonballStallThievingOverlay overlay;
    @Getter
    private GameObject                     oreStall;
    @Getter
    private GameObject                     cannonballStall;
    @Getter
    private GameObject                     silkStall;
    @Getter
    private boolean                        cannonballCurrentlySafe = false;
    @Getter
    private boolean                        otherCurrentlySafe      = false;

    private static final int          PORT_ROBERTS_REGION_ID     = 7475;
    private static final int          CANNONBALL_STALL_OBJECT_ID = 58108;
    private static final int          ORE_STALL_OBJECT_ID        = 58107;
    private static final int          SILK_STALL_OBJECT_ID       = 58101;
    private static final Set<Integer> GUARD_IDS                  = Set.of(14881, 14882, 14883);

    @Override
    protected void startUp() throws Exception {
        log.debug("Cannonball Stall Thieving started!");
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception {
        log.debug("Cannonball Stall Thieving stopped!");
        overlayManager.remove(overlay);
        oreStall = null;
        cannonballStall = null;
        silkStall = null;
        cannonballCurrentlySafe = false;
        otherCurrentlySafe = false;
    }

    @Provides
    CannonballStallThievingConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(CannonballStallThievingConfig.class);
    }

    public boolean playerInPortRoberts() {
        return client.getGameState() != GameState.LOGGED_IN || client.getLocalPlayer().getWorldLocation().getRegionID() == PORT_ROBERTS_REGION_ID;
    }

    @Subscribe
    public void onGameObjectSpawned(GameObjectSpawned event) {
        if (!playerInPortRoberts()) {
            return;
        }
        var gameObject = event.getGameObject();
        if (gameObject.getId() == ORE_STALL_OBJECT_ID) {
            oreStall = gameObject;
        } else if (gameObject.getId() == SILK_STALL_OBJECT_ID) {
            silkStall = gameObject;
        } else if (gameObject.getId() == CANNONBALL_STALL_OBJECT_ID) {
            cannonballStall = gameObject;
        }
    }

    @Subscribe
    public void onGameObjectDespawned(GameObjectDespawned event) {
        if (!playerInPortRoberts()) {
            return;
        }
        var gameObject = event.getGameObject();
        if (gameObject.getId() == ORE_STALL_OBJECT_ID) {
            oreStall = null;
        } else if (gameObject.getId() == SILK_STALL_OBJECT_ID) {
            silkStall = null;
        } else if (gameObject.getId() == CANNONBALL_STALL_OBJECT_ID) {
            cannonballStall = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        if (!playerInPortRoberts() || oreStall == null || cannonballStall == null || silkStall == null) {
            return;
        }
        cannonballCurrentlySafe = distanceClosestGuard(cannonballStall.getWorldLocation()) > 1;
        otherCurrentlySafe = distanceClosestGuard(silkStall.getWorldLocation()) > 1;
    }

    private int distanceClosestGuard(WorldPoint worldPoint) {
        return client.getTopLevelWorldView().npcs().stream()
                .filter(npc -> GUARD_IDS.contains(npc.getId()))
                .map(npc -> npc.getWorldLocation().distanceTo2D(worldPoint))
                .min(Integer::compare)
                .orElse(0);
    }
}
