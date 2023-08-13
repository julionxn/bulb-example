package net.pulga22.myplugin;

import net.pulga22.bulb.core.GameManager;
import net.pulga22.bulb.core.config.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class MyPlugin extends JavaPlugin {

    GameManager<MyMinigame, MyPlugin> myGameManager;

    @Override
    public void onEnable() {
        ConfigManager<MyPlugin> myConfig = ConfigManager.register(this);
        this.myGameManager = GameManager.register(
                this,
                myConfig,
                "tntrun",
                MyMinigame::new
        );
        myGameManager.registerListeners(
                OnJoinListener::new
        );
    }

    @Override
    public void onDisable() {
        this.myGameManager.endGames();
    }

}
