package io.tebex.plugin.command.sub;

import io.tebex.plugin.TebexPlugin;
import io.tebex.plugin.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class CheckoutCommand extends SubCommand {
    public CheckoutCommand(TebexPlugin platform) {
        super(platform, "checkout", "tebex.checkout");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        TebexPlugin platform = getPlatform();

        if (!platform.isSetup()) {
            sender.sendMessage("§b[Tebex] §7This server is not connected to a webstore. Use /tebex secret to set your store key.");
            return;
        }

        if (args.length == 0) {
            sender.sendMessage("§b[Tebex] §7Invalid command usage. Use /tebex " + this.getName() + " " + getUsage());
            return;
        }
        
        // FOX - Handle non-number package ID
        int packageId;
        try {
            packageId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§b[Tebex] §7Package ID must be a number.");
            return;
        }
        
        // FOX - Make the operation non-blocking
        platform.getSDK().createCheckoutUrl(packageId, sender.getName()).thenAccept(checkoutUrl -> {
            Bukkit.getScheduler().runTask(platform, () -> {
                sender.sendMessage("§b[Tebex] §7Checkout started! Click here to complete payment: " + checkoutUrl.getUrl());
            });
        }).exceptionally(e -> {
            Bukkit.getScheduler().runTask(platform, () -> {
                sender.sendMessage("§b[Tebex] §7Failed to get checkout link for package, check package ID: " + e.getMessage());
            });
            return null;
        });
    }

    @Override
    public String getDescription() {
        return "Creates payment link for a package";
    }

    @Override
    public String getUsage() {
        return "<packageId>";
    }
}
