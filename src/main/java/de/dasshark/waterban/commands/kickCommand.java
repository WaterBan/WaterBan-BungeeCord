package de.dasshark.waterban.commands;

import de.dasshark.waterban.Main;
import de.dasshark.waterban.mysql.MySQL;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class kickCommand extends Command {
    public kickCommand() {
        super("kick");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        if (Main.getInstance().getProxy().getPlayers().size() == 0) return;
        for (ProxiedPlayer player : Main.getInstance().getProxy().getPlayers()) {
            if (player.getDisplayName() == args[0]) {
                String reason = Main.getInstance().getCfg().getString("Messages.Kick.Default");
                if (args.length > 1) {
                    if (args[0].equalsIgnoreCase(player.getDisplayName())) {
                        args[0] = "";
                        StringBuilder reasonBuilder = new StringBuilder();
                        for (String arg : args) {
                            reasonBuilder.append(arg).append(" ");
                        }
                        reason = reasonBuilder.toString();
                    }
                }
                StringBuilder fullReasonBuilder = new StringBuilder();
                fullReasonBuilder.append(Main.getInstance().getCfg().getString("Messages.Kick.Line 1")).append("\n").append(Main.getInstance().getCfg().getString("Messages.Kick.Line 2")).append("\n").append(Main.getInstance().getCfg().getString("Messages.Kick.Line 3")).append("\n").append(Main.getInstance().getCfg().getString("Messages.Kick.Line 4"));

                String fullReason = fullReasonBuilder.toString();
                fullReason.replace("&", "§");
                fullReason.replace("[REASON]", reason);

                player.disconnect(new TextComponent(fullReason));
                if (!MySQL.existsPlayer(player))
                    MySQL.update("INSERT INTO waterBan(NAME, UUID, Banned, BanReason, Muted, Until, MuteHistory, BanHistory, KickHistory) VALUES ('" + player.getDisplayName() + "','" + player.getUniqueId().toString() + "','0','null','0','1000-01-01 00:00:00.000000','0','0','0')");
                int kicks = Integer.parseInt((String)MySQL.get("KickHistory", "UUID", player.getUniqueId().toString()));
                MySQL.set("KickHistory", "" + (kicks + 1), "UUID", player.getUniqueId().toString());
                return;
            }
        }
        sender.sendMessage(new TextComponent(Main.getInstance().getCfg().getString("Messages.Kick.Player Not Online").replace("&", "§")));

    }
}
