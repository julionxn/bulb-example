package net.pulga22.myplugin;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
import net.pulga22.bulb.core.GameInstance;
import net.pulga22.bulb.core.GameManager;
import net.pulga22.bulb.core.config.ConfigManager;
import net.pulga22.bulb.core.score.GameScoreboard;
import net.pulga22.bulb.core.score.components.counter.CounterComponent;
import net.pulga22.bulb.core.score.components.state.GameStateComponent;
import net.pulga22.bulb.core.states.GameState;
import net.pulga22.bulb.core.worlds.WorldOption;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MyMinigame extends GameInstance<MyPlugin> {

    public CounterComponent alivePlayers;
    public GameStateComponent stateComponent;
    private final PlayerTracker runnable;

    public <K extends GameInstance<MyPlugin>> MyMinigame(MyPlugin plugin, GameManager<K, MyPlugin> manager, String gameName, WorldOption worldOption, boolean prepareOnCreation) {
        super(plugin, manager, gameName, worldOption, prepareOnCreation);
        this.runnable = new PlayerTracker(this);
    }

    @Override
    public int getMaxPlayers() {
        return 16;
    }

    @Override
    public int getTimeBeforeStart() {
        return 25;
    }

    @Override
    public int getTimeOfGame() {
        return -1;
    }

    @Override
    public int getTimeBeforeEnd() {
        return 25;
    }

    @Override
    public GameScoreboard initScoreboard() {
        GameScoreboard gameScoreboard = GameScoreboard.builder().setTitle(
                Component.text("Playing: TntRun", TextColor.color(0xff0000)))
                .setLastRow("-My.Server-").build();
        this.alivePlayers = new CounterComponent(ChatColor.BOLD + "Players");
        this.stateComponent = new GameStateComponent("State: ", (gameState) -> {
            String base = ChatColor.GOLD + "";
            return switch (gameState) {
                case PREPARED -> base + "Preparing...";
                case PLAYING -> base + "Playing...";
                case FINISHING -> base + "Finishing...";
                case FINISHED -> base + "Finished...";
                default -> base + "NONE";
            };
        });
        gameScoreboard.initComponent(2, this.stateComponent);
        gameScoreboard.initComponent(1, this.alivePlayers);
        return gameScoreboard;
    }

    @Override
    protected void onJoinedAsPlayer(Player player) {
        super.onJoinedAsPlayer(player);
        this.alivePlayers.increment();
    }

    @Override
    protected void onJoinedCommon(Player player) {
        super.onJoinedCommon(player);
        player.getInventory().clear();
    }

    @Override
    protected void onGameStarted(List<Player> players, List<Player> spectators) {
        this.runnable.runTaskTimer(this.plugin, 0, 4);
    }

    @Override
    protected void onDeath(World world, Player player) {
        super.onDeath(world, player);
        Location location = this.getWorldInstance().getLocationOfPoi("spawnpoint");
        player.teleport(location);
        this.alivePlayers.decrement();
    }

    @Override
    protected void onPrepareToEnd(List<Player> players, List<Player> spectators) {
        this.runnable.cancel();
        Player winner = players.get(0);
        winner.sendMessage("You won!");
        winner.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
        spectators.forEach(spectator -> {
            spectator.sendMessage(winner.displayName() + " has won!");
        });
    }

    @Override
    protected void onGameStateChange(GameState previousState, GameState newState) {
        this.stateComponent.setState(newState);
    }

}
