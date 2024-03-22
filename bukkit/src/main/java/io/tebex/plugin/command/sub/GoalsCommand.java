package io.tebex.plugin.command.sub;

import io.tebex.plugin.TebexPlugin;
import io.tebex.plugin.command.SubCommand;
import io.tebex.sdk.obj.CommunityGoal;
import org.bukkit.command.CommandSender;

public class GoalsCommand extends SubCommand {
    public GoalsCommand(TebexPlugin platform) {
        super(platform, "goals", "tebex.goals");
    }

    // FOX - Make the operation non-blocking & fix header spam
    @Override
    public void execute(CommandSender sender, String[] args) {
        TebexPlugin platform = getPlatform();
        
        sender.sendMessage("§b[Tebex] §7Community Goals: ");
        
        platform.getSDK().getCommunityGoals().thenAccept(goals -> {
            for (CommunityGoal goal: goals) {
                if (goal.getStatus() != CommunityGoal.Status.DISABLED) {
                    sender.sendMessage(String.format("§b[Tebex] §7- %s (%.2f/%.2f) [%s]", goal.getName(), goal.getCurrent(), goal.getTarget(), goal.getStatus()));
                }
            }
        }).exceptionally(e -> {
            sender.sendMessage("§b[Tebex] §7Unexpected response: " + e.getMessage());
            return null;
        });
    }

    @Override
    public String getDescription() {
        return "Shows active and completed community goals.";
    }
}
