package de.dasshark.waterban.listeners;

import de.dasshark.waterban.Main;
import de.dasshark.waterban.mysql.MySQL;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public class ConnectListener implements Listener {

    @EventHandler
    public void onConnect(LoginEvent e) {
        if (MySQL.get("Banned", "UUID", e.getConnection().getUniqueId().toString()) == "1") {
            if (MySQL.get("Permanent", "UUID", e.getConnection().getUniqueId().toString()) == "1") {
                StringBuilder fullReasonBuilder = new StringBuilder();
                fullReasonBuilder.append(Main.getInstance().getCfg().getString("Messages.PermanentBan.Line 1")).append("\n").append(Main.getInstance().getCfg().getString("Messages.PermanentBan.Line 2")).append("\n").append(Main.getInstance().getCfg().getString("Messages.PermanentBan.Line 3")).append("\n").append(Main.getInstance().getCfg().getString("Messages.PermanentBan.Line 4"));

                String reason = String.valueOf(MySQL.get("BanReason", "UUID", e.getConnection().getUniqueId().toString()));

                String fullReason = fullReasonBuilder.toString();
                fullReason.replace("&", "§");
                fullReason.replace("[REASON]", reason);
                e.getConnection().disconnect(new TextComponent(fullReason));

            } else {
                StringBuilder fullReasonBuilder = new StringBuilder();
                fullReasonBuilder.append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 1")).append("\n").append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 2")).append("\n").append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 3")).append("\n").append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 4")).append("\n").append(Main.getInstance().getCfg().getString("Messages.TempBan.Line 5"));

                String reason = String.valueOf(MySQL.get("BanReason", "UUID", e.getConnection().getUniqueId().toString()));
                Timestamp until = (Timestamp) MySQL.get("Until", "UUID", e.getConnection().getUniqueId().toString());

                String fullReason = fullReasonBuilder.toString();
                fullReason.replace("&", "§");
                fullReason.replace("[REASON]", reason);
                int days = 0, hours = 0, minutes = 0, seconds = 0;
                calcToTimestamp(until, days, hours, minutes, seconds);
                StringBuilder remainingBuilder = new StringBuilder();
                remainingBuilder.append(days).append(" Days, ").append(hours).append(" Hours, ").append(minutes).append(" Minutes, ").append(seconds).append(" Seconds");
                fullReason.replace("[REMAINING]", remainingBuilder.toString());

                e.getConnection().disconnect(new TextComponent(fullReason));
            }
            e.getConnection().disconnect();
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
