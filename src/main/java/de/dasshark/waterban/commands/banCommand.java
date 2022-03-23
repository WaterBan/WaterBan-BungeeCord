package de.dasshark.waterban.commands;

import de.dasshark.waterban.Main;
import de.dasshark.waterban.mysql.MySQL;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class banCommand extends Command {
    public banCommand() {
        super("ban");
    }


    @Override
    public void execute(CommandSender sender, String[] args) {
        for (ProxiedPlayer player : Main.getInstance().getProxy().getPlayers()) {
            String reason;
            Timestamp until = new Timestamp(System.currentTimeMillis());
            Boolean permanent = false;
            if (player.getDisplayName() == args[0]) {
                if (args.length == 2) {
                    switch (args[1]) {
                            case "1":
                                reason = Main.getInstance().getCfg().getString("BanReasons.1");
                                setBanDauer(1, until, permanent);
                                break;
                            case "2":
                                reason = Main.getInstance().getCfg().getString("BanReasons.2");
                                setBanDauer(2, until, permanent);
                                break;
                            case "3":
                                reason = Main.getInstance().getCfg().getString("BanReasons.3");
                                setBanDauer(3, until, permanent);
                                break;
                            case "4":
                                reason = Main.getInstance().getCfg().getString("BanReasons.4");
                                setBanDauer(4, until, permanent);
                                break;
                            case "5":
                                reason = Main.getInstance().getCfg().getString("BanReasons.5");
                                setBanDauer(5, until, permanent);
                                break;
                            case "6":
                                reason = Main.getInstance().getCfg().getString("BanReasons.6");
                                setBanDauer(6, until, permanent);
                                break;
                            case "7":
                                reason = Main.getInstance().getCfg().getString("BanReasons.7");
                                setBanDauer(7, until, permanent);
                                break;
                            case "8":
                                reason = Main.getInstance().getCfg().getString("BanReasons.8");
                                setBanDauer(8, until, permanent);
                                break;
                            case "9":
                                reason = Main.getInstance().getCfg().getString("BanReasons.9");
                                setBanDauer(9, until, permanent);
                                break;
                            case "0":
                                reason = Main.getInstance().getCfg().getString("BanReasons.0");
                                setBanDauer(0, until, permanent);
                                break;
                            default:
                                sender.sendMessage(new TextComponent(Main.getInstance().getCfg().getString("Messages.PermanentBan.Wrong reason specified")));
                                return;
                        } //Setze Reason und Ban-Dauer
                } else {
                    sender.sendMessage(new TextComponent(Main.getInstance().getCfg().getString("Messages.PermanentBan.No reason specified")));
                    return;
                }
                if (!permanent) {
                    StringBuilder fullReasonBuilder = new StringBuilder();
                    fullReasonBuilder.append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 1")).append("\n").append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 2")).append("\n").append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 3")).append("\n").append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 4")).append("\n").append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 5"));

                    String fullReason = fullReasonBuilder.toString();
                    fullReason.replace("&", "§");
                    fullReason.replace("[REASON]", reason);
                    int days = 0, hours = 0, minutes = 0, seconds = 0;
                    calcToTimestamp(until, days, hours, minutes, seconds);
                    StringBuilder remainingBuilder = new StringBuilder();
                    remainingBuilder.append(days).append(" Days, ").append(hours).append(" Hours, ").append(minutes).append(" Minutes, ").append(seconds).append(" Seconds");
                    fullReason.replace("[REMAINING]", remainingBuilder.toString());

                    player.disconnect(new TextComponent(fullReason));
                    if (!MySQL.existsPlayer(player))
                        MySQL.update("INSERT INTO waterBan(NAME, UUID, Banned, BanReason, Muted, Until, MuteHistory, BanHistory, KickHistory) VALUES ('" + player.getDisplayName() + "','" + player.getUniqueId().toString() + "','0','null','0','1000-01-01 00:00:00.000000','0','0','0')");
                    int bans = Integer.parseInt((String) MySQL.get("BanHistory", "UUID", player.getUniqueId().toString()));
                    MySQL.set("BanHistory", "" + (bans + 1), "UUID", player.getUniqueId().toString());
                    MySQL.set("Banned", "1", "UUID", player.getUniqueId().toString());
                    MySQL.set("BanReason", reason, "UUID", player.getUniqueId().toString());
                    MySQL.set("Until", until.toString(), "UUID", player.getUniqueId().toString());
                } else {
                    StringBuilder fullReasonBuilder = new StringBuilder();
                    fullReasonBuilder.append(Main.getInstance().getCfg().getString("Messages.PermanentBan.Line 1")).append("\n").append(Main.getInstance().getCfg().getString("Messages.PermanentBan.Line 2")).append("\n").append(Main.getInstance().getCfg().getString("Messages.PermanentBan.Line 3")).append("\n").append(Main.getInstance().getCfg().getString("Messages.PermanentBan.Line 4"));

                    String fullReason = fullReasonBuilder.toString();
                    fullReason.replace("&", "§");
                    fullReason.replace("[REASON]", reason);

                    player.disconnect(new TextComponent(fullReason));
                    if (!MySQL.existsPlayer(player))
                        MySQL.update("INSERT INTO waterBan(NAME, UUID, Banned, BanReason, Muted, Until, MuteHistory, BanHistory, KickHistory) VALUES ('" + player.getDisplayName() + "','" + player.getUniqueId().toString() + "','0','null','0','1000-01-01 00:00:00.000000','0','0','0')");
                    int bans = Integer.parseInt((String) MySQL.get("BanHistory", "UUID", player.getUniqueId().toString()));
                    MySQL.set("BanHistory", "" + (bans + 1), "UUID", player.getUniqueId().toString());
                    MySQL.set("Banned", "1", "UUID", player.getUniqueId().toString());
                    MySQL.set("BanReason", reason, "UUID", player.getUniqueId().toString());
                    MySQL.set("Permanent", "1", "UUID", player.getUniqueId().toString());
                }
                return;
            }
        }
    }

    public void setBanDauer(int Banid, Timestamp until, boolean permanent) {
        if (Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).equals("PERMANENT")) {
            permanent = true;
        } else if (Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).contains("w")) {
            Integer hours = Integer.valueOf(Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).replace("w", ""));
            hours = hours*168;
            until.setHours(until.getHours() + hours);
        } else if (Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).contains("d")) {
            Integer hours = Integer.valueOf(Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).replace("d", ""));
            hours = hours*24;
            until.setHours(until.getHours() + hours);
        } else if (Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).contains("h")) {
            Integer hours = Integer.valueOf(Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).replace("h", ""));
            until.setHours(until.getHours() + hours);
        } else if (Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).contains("min")) {
            Integer minutes = Integer.valueOf(Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).replace("min", ""));
            until.setMinutes(until.getMinutes() + minutes);
        } else if (Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).contains("sec")) {
            Integer seconds = Integer.valueOf(Main.getInstance().getCfg().getString("BanReasons.Time for Ban." + Banid).replace("sec", ""));
            until.setSeconds(until.getSeconds() + seconds);
        }
    }

    public void calcToTimestamp(Timestamp timestamp, int days_output, int hours_output, int minutes_output, int seconds_output) {
        Date date = new Date();
        Timestamp currentTime = new Timestamp(date.getTime());

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime.getTime());

        cal.add(Calendar.SECOND, 98765);

        long milliseconds = timestamp.getTime() - currentTime.getTime();
        int seconds = (int) (milliseconds / 1000);

        days_output = seconds / 86400;
        hours_output = (seconds % 86400) / 3600;
        minutes_output = ((seconds % 86400) % (seconds % 3600)) / 60;
        seconds_output = ((seconds % 86400) % (seconds % 3600)) % 60;
    }
}
