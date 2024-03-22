package io.tebex.plugin.command.sub;

import io.tebex.plugin.TebexPlugin;
import io.tebex.plugin.command.SubCommand;
import org.bukkit.command.CommandSender;

public class LookupCommand extends SubCommand {
    public LookupCommand(TebexPlugin platform) {
        super(platform, "lookup", "tebex.lookup");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TebexPlugin platform = getPlatform();

        if (!platform.isSetup()) {
            sender.sendMessage("§b[Tebex] §7This server is not connected to a webstore. Use /tebex secret to set your store key.");
            return;
        }

        if (args.length != 1) {
            sender.sendMessage("§b[Tebex] §7Invalid command usage. Use /tebex " + this.getName() + " " + getUsage());
            return;
        }

        String username = args[0];

        // FOX - Make the operation non-blocking
        platform.getSDK().getPlayerLookupInfo(username).thenAccept(lookupInfo -> {
            if (lookupInfo != null) {
                sender.sendMessage("§b[Tebex] §7Username: " + lookupInfo.getLookupPlayer().getUsername());
                sender.sendMessage("§b[Tebex] §7Id: " + lookupInfo.getLookupPlayer().getId());
                sender.sendMessage("§b[Tebex] §7Chargeback Rate: " + lookupInfo.chargebackRate);
                sender.sendMessage("§b[Tebex] §7Bans Total: " + lookupInfo.banCount);
                sender.sendMessage("§b[Tebex] §7Payments: " + lookupInfo.payments.size());
            } else {
                sender.sendMessage("§b[Tebex] §7No information found for that player.");
            }
        }).exceptionally(e -> {
            sender.sendMessage("§b[Tebex] §7Failed to complete player lookup. " + e.getMessage());
            return null;
        });
    }

    @Override
    public String getDescription() {
        return "Gets user transaction info from your webstore.";
    }

    @Override
    public String getUsage() {
        return "<username>";
    }
}
