package ga.jarza.hundbot.main.commands.moderation;

import ga.jarza.hundbot.main.MainBot;
import ga.jarza.hundbot.main.Util;
import ga.jarza.hundbot.main.commands.ChatCommands;
import ga.jarza.hundbot.main.db.DataManager;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.MessageHistory;
import sx.blah.discord.util.RequestBuffer;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ModerationCL {
  
  public static void init() {
    ChatCommands.commandMap.put("warnp", (event, args) -> {
      Punishments p;
      if (args.isEmpty() || args.size() != 2)
        return;

      switch (args.get(1)) {
        case "kick":
          p = Punishments.kick;
          break;
        case "ban":
          p = Punishments.ban;
          break;
        default:
          Util.sendMessage(event, "Error: 'kick' or 'ban' only.");
          return;
      }
      Moderation.warnp(Integer.parseInt(args.get(0)), p, event);
    });

    ChatCommands.commandMap.put("warn", (event, args) -> {
      if (DataManager.getWarnrole(event.getGuild().getLongID()).size() == 1 && DataManager.getWarnrole(event.getGuild().getLongID()).get(0) > -1) {
        boolean cont = false;
        for (Long a : DataManager.getWarnrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: warning role not set. Please run " + Util.prefix + "warnr roleName**");
        return;
      }
      String text = "";

      if (event.getMessage().getMentions().isEmpty()) {
        Util.sendMessage(event, "**>Error: you must tag the user you are warning!** *(hint: if you have the ID use this <@idGoesHere>)*");
        return;
      }

      args.remove(0);
      if (!args.isEmpty()) {
        args.remove(0);
        for (String b : args) {
          text += b + " ";
        }
        text.trim();
      } else {
        text = "-no reason-";
      }
      //       System.out.println("id: " + id + " res: " + text);
      Moderation.warn(event.getMessage().getMentions().get(0).getLongID(), text, event);
    });

    ChatCommands.commandMap.put("warnc", (event, args) -> {
      // if (Database.loadSettings(event).warnrole > -1) {
      // System.out.println("warnrole: " + Database.loadSettings(event).warnrole);
      // if (!event.getAuthor().getRolesForGuild(event.getGuild())
      // .contains(event.getGuild().getRoleByID(Database.loadSettings(event).warnrole))) {
      // // for (IRole a : event.getAuthor().getRolesForGuild(event.getGuild())) {
      // // System.out.println(a.getName() + " < warnrole name loop");
      // // }
      // Util.sendMessage(event.getChannel(), "**>Error: inssuficient permission**");
      // return;
      // }
      // } else {
      // Util.sendMessage(event.getChannel(), "**>Error: warning role not set**");
      // return;
      // }

      if (args.isEmpty() || args.size() > 2)
        return;

      //      Long id;
      //      try{
      //        id = event.getGuild().getUserByID(Util.userToID(args.get(0))).getLongID();
      //      }catch(NumberFormatException e){
      //        Util.sendMessage(event.getChannel(), "**>Error: you must tag the user you are warning!**");
      //        return;
      //      }

      if (event.getMessage().getMentions().isEmpty()) {
        Util.sendMessage(event, "**>Error: you must tag the user you are warning!** *(hint: if you have the ID use this <@idGoesHere>)*");
        return;
      }

      if (args.size() == 2) {
        Moderation.clearWarn(Integer.parseInt(args.get(1)) - 1, event.getMessage().getMentions().get(0).getLongID(), event);
      } else {
        Moderation.clearWarn(-1, event.getMessage().getMentions().get(0).getLongID(), event);
      }


    });

    ChatCommands.commandMap.put("warnl", (event, args) -> {
      String mode = "a";
      Long id = 0L;
      boolean cont = false;
      for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
        if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
          cont = true;
        }
      }
      if (!cont) {
        Util.sendMessage(event, "**>Error: inssuficient permission**");
        return;
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
      }
      if (args.size() == 1) {
        if (event.getMessage().getMentions().isEmpty()) {
          Util.sendMessage(event, "**>Error: you must tag the user you are warning!** *(hint: if you have the ID use this <@idGoesHere>)*");
          return;
        }
        id = event.getMessage().getMentions().get(0).getLongID();
        mode = "b";
      }

      Moderation.listWarns(id, mode, event);

    });

    ChatCommands.commandMap.put("modr", (event, args) -> {
      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
      }

      if (args.isEmpty()) {
        String stuff = "Moderator roles:\n";
        for (Long p : DataManager.getModrole(event.getGuild().getLongID())) {
          stuff += event.getGuild().getRoleByID(p).getName() + "\n";
        }
        Util.sendMessage(event, stuff);
      } else if (args.size() == 1) {
        String role = args.get(0);
        IRole rol = event.getGuild().getRoles().get(0);
        boolean f = false;
        Long temp = 0L;
        try {
          temp = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
        }
        for (IRole r : event.getGuild().getRoles()) {
          if (r.getName().equals(role) || r.getLongID() == temp) {
            rol = r;
            f = true;
          }
        }

        if (rol == event.getGuild().getRoles().get(0) && !f) {
          Util.sendMessage(event, "**>Error: bad role u dipshite**");
          return;
        }
        Moderation.setModRole(rol, event);
      }
    });

    ChatCommands.commandMap.put("warnr", (event, args) -> {
      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
      }
      if (args.isEmpty()) {
        String stuff = "Warning roles:\n";
        for (Long p : DataManager.getWarnrole(event.getGuild().getLongID())) {
          stuff += event.getGuild().getRoleByID(p).getName() + "\n";
        }
        Util.sendMessage(event, stuff);
      } else if (args.size() == 1) {
        String role = args.get(0);
        IRole rol = event.getGuild().getRoles().get(0);
        boolean f = false;
        Long temp = 0L;
        try {
          temp = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
        }
        for (IRole r : event.getGuild().getRoles()) {
          if (r.getName().equals(role) || r.getLongID() == temp) {
            rol = r;
            f = true;
          }
        }

        if (rol == event.getGuild().getRoles().get(0) && !f) {
          Util.sendMessage(event, "**>Error: invalid role**");
          return;
        }
        Moderation.setWarnRole(rol, event);
      }
    });

    ChatCommands.commandMap.put("backuppins", (event, args) -> {
      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
      }
      if (args.size() > 0) {
        if (event.getMessage().getChannelMentions().isEmpty()) {
          Util.sendMessage(event, "*Error: Invalid channel*");
        } else {
          DataManager.setPinbu(event.getGuild().getLongID(), event.getMessage().getChannelMentions().get(0).getLongID());
          Util.sendMessage(event, "*Success!*");
        }
      } else {
        if (DataManager.getPinbu(event.getGuild().getLongID()).equals(-1L)) {
          IChannel ps;
          if (event.getGuild().getChannelsByName("pins").size() == 0) {
            ps = event.getGuild().createChannel("pins");
            ps.overrideRolePermissions(event.getGuild().getEveryoneRole(), null, EnumSet.of(Permissions.SEND_MESSAGES));
            ps.overrideUserPermissions(MainBot.cli.getOurUser(), EnumSet.of(Permissions.SEND_MESSAGES), null);
            DataManager.setPinbu(event.getGuild().getLongID(), ps.getLongID());
          } else {
            ps = event.getGuild().getChannelsByName("pins").get(0);
            ps.overrideRolePermissions(event.getGuild().getEveryoneRole(), null, EnumSet.of(Permissions.SEND_MESSAGES));
            ps.overrideUserPermissions(MainBot.cli.getOurUser(), EnumSet.of(Permissions.SEND_MESSAGES), null);
            DataManager.setPinbu(event.getGuild().getLongID(), ps.getLongID());
          }
          Util.sendMessage(event, "*Success! Pins will be stored in " + ps.getName() + "*");
        } else {
          DataManager.setPinbu(event.getGuild().getLongID(), -1L);
          Util.sendMessage(event, "*Success! Pins will no longer be backed up.*");
        }
      }
    });

    ChatCommands.commandMap.put("roleids", (event, args) -> {
      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
      }
      String s = "";
      for (IRole r : event.getGuild().getRoles()) {
        if (!r.isEveryoneRole())
          s += "@" + r.getName() + " - " + r.getLongID() + "\n";
      }
      String finalS = s;
      Util.sendMessage(event, finalS);
    });

    ChatCommands.commandMap.put("ruler", (event, args) -> {
      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
      }

      EmbedBuilder builder = new EmbedBuilder();
      builder.withFooterText("Permissions for this guild");
      if (args.isEmpty()) {
        int id = 0;
        for (Permission p : DataManager.getPerms(event.getGuild().getLongID())) {
          String stuff = p.command + " is " + (p.value ? "allowed " : "denied ") + (p.channel == 0L ? " everywhere" : " in " + event.getGuild().getChannelByID(p.channel)) + " for " + (p.role == 0L ?
            "everyone" :
            "@" + event.getGuild().getRoleByID(p.role).getName());
          builder.appendField("#" + id, stuff, false);
          id++;
        }

        Util.sendMessage(event, builder.build());
      } else if (args.size() == 1) {
        List<Permission> ls = new ArrayList<>();
        DataManager.getPerms(event.getGuild().getLongID()).forEach(ls::add);
        ls.remove(ls.get(Integer.parseInt(args.get(0))));
        DataManager.setPermissions(ls, event.getGuild().getLongID());
      } else if (args.size() == 2) {
        List<Permission> ls = new ArrayList<>();
        DataManager.getPerms(event.getGuild().getLongID()).forEach(ls::add);

        int a, b;
        try {
          a = Integer.parseInt(args.get(0));
          b = Integer.parseInt(args.get(1));
        } catch (NumberFormatException e) {
          e.printStackTrace();
          return;
        }
        Permission temp = ls.get(a);
        Permission tempb = ls.get(b);

        if (a > b) {
          ls.remove(ls.get(a));
          ls.remove(ls.get(b));
        } else if (a < b) {
          ls.remove(ls.get(b));
          ls.remove(ls.get(a));
        }

        if (a > b) {
          ls.add(b, temp);
          ls.add(a, tempb);
        } else if (a < b) {
          ls.add(a, tempb);
          ls.add(b, temp);
        }

        DataManager.setPermissions(ls, event.getGuild().getLongID());
      }
    });

    ChatCommands.commandMap.put("kick", (MessageReceivedEvent event, List<String> args) -> {
      if (args.isEmpty())
        return;

      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
        return;
      }

      if (args.size() > 1) {
        args.remove(0);
        String a = "";
        for (String b : args) {
          a += b + " ";
        }
        event.getGuild().kickUser(event.getMessage().getMentions().get(0), a);
      }

      Util.sendMessage(event, "**>User kicked.**");
    });

    ChatCommands.commandMap.put("ban", (MessageReceivedEvent event, List<String> args) -> {
      if (args.isEmpty())
        return;

      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
        return;
      }

      if (args.size() > 1) {
        args.remove(0);
        String a = "";
        for (String b : args) {
          a += b + " ";
        }
        event.getGuild().banUser(event.getMessage().getMentions().get(0), a);
      }

      Util.sendMessage(event, "**>User banned.**");
    });

    ChatCommands.commandMap.put("quote", (MessageReceivedEvent event, List<String> args) -> {
      if (args.isEmpty())
        return;
      Long msgid;
      IMessage msg;
      try {
        msgid = Long.parseLong(args.get(0));
        msg = event.getGuild().getMessageByID(msgid);
      } catch (Throwable e) {
        return;
      }
      EmbedBuilder e = new EmbedBuilder();

      e.withAuthorIcon(msg.getAuthor().getAvatarURL());
      e.withAuthorName(msg.getAuthor().getName() + "#" + msg.getAuthor().getDiscriminator());
      e.withDescription(msg.getFormattedContent());
      for (IMessage.Attachment a : msg.getAttachments()) {
        e.withUrl(a.getUrl());
        e.withImage(a.getUrl());
      }
      e.withFooterText(
        Util.formatter.format(msg.getTimestamp()) + " in #" + msg.getChannel().getName());
      e.withColor(112, 128, 144);
      Util.sendMessage(event, e.build());
    });

    ChatCommands.commandMap.put("botchannel", (event, args) -> {
      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
        return;
      }

      if(event.getMessage().getChannelMentions().size() == 0) {
        if(args.isEmpty()){
          Util.sendMessage(event, event.getGuild().getChannelByID(DataManager.getBotComChan(event.getGuild().getLongID())) + " is the current bot command channel.");
        }else {
          if (args.get(0).equalsIgnoreCase("remove")) {
            DataManager.setBotComChan(event.getGuild().getLongID(), -1L);
            Util.sendMessage(event.getChannel(), "Success!");
          } else if (args.get(0).equalsIgnoreCase("dms")) {
            if (DataManager.getBotComChan(event.getGuild().getLongID()) == -1L) {
              DataManager.setBotComChan(event.getGuild().getLongID(), -2L);
              Util.sendMessage(event.getChannel(), "People will no longer get DMs about me not having talking permissions in the channel they use me in.");
            } else if (DataManager.getBotComChan(event.getGuild().getLongID()) == -2L) {
              DataManager.setBotComChan(event.getGuild().getLongID(), -1L);
              Util.sendMessage(event.getChannel(), "People will now get DMs about me not having talking permissions in the channel they use me in.");
            } else {
              Util.sendMessage(event.getChannel(), event.getGuild().getChannelByID(DataManager.getBotComChan(event.getGuild().getLongID())) + " has been selected as the bot command channel, so I will respond to people in there, not in the DMs.");
            }
          }
        }
        return;
      }

      if(!event.getGuild().getChannels().contains(event.getMessage().getChannelMentions().get(0)))
        return;


      IChannel s = event.getMessage().getChannelMentions().get(0);

      if(s.getLongID() == DataManager.getBotComChan(event.getGuild().getLongID())){
        Util.sendMessage(event.getChannel(), "That channel is allready the selected bot commands channel!");
      }else{
        DataManager.setBotComChan(event.getGuild().getLongID(), s.getLongID());
        Util.sendMessage(event.getChannel(), "Success!");
      }
    });

    ChatCommands.commandMap.put("botclear",  (MessageReceivedEvent event, List<String> args) -> {
      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
        return;
      }

      int mc = 0;
      int num = 100;

      if(!args.isEmpty()){
        try{
          num = Integer.parseInt(args.get(0));
        }catch (Throwable e){
          e.printStackTrace();
        }
      }


      try{
        final int numm = num+1;
        mc = RequestBuffer.request(() -> {
          MessageHistory m = event.getChannel().getMessageHistory(numm);
          try {
            m.stream().filter(n -> {
              System.out.println(n.getAuthor().isBot());
              return true;
            });
          }catch (Throwable e){
            e.printStackTrace();
          }
          return m.bulkDelete().size();
        }).get();
      }catch (Throwable e){
        Util.sendMessage(event, "failed to delete messages");
        e.printStackTrace();
      }finally{
        EmbedBuilder e = new EmbedBuilder();
        mc--;
        e.withColor(event.getAuthor().getColorForGuild(event.getGuild()));

        e.withTitle(String.format("Successfully deleted %d messages!", mc));
        Util.sendMessage(event, e.build());
      }
    });

    ChatCommands.commandMap.put("clear",  (MessageReceivedEvent event, List<String> args) -> {
      if (!DataManager.getModrole(event.getGuild().getLongID()).isEmpty()) {
        boolean cont = false;
        for (Long a : DataManager.getModrole(event.getGuild().getLongID())) {
          if (event.getAuthor().getRolesForGuild(event.getGuild()).contains(event.getGuild().getRoleByID(a))) {
            cont = true;
          }
        }
        if (!cont) {
          Util.sendMessage(event, "**>Error: inssuficient permission**");
          return;
        }
      } else {
        Util.sendMessage(event, "**>Error: bot editing permission has not been set up. Please run " + Util.prefix + "modr roleName**");
        return;
      }

      if(args.isEmpty())
        return;
      int num = 99;

      try{
        num = Integer.parseInt(args.get(0));
        if(num > 100 || num < 1){
          EmbedBuilder e = new EmbedBuilder();

          e.withColor(255, 0, 0);
          e.withTitle("Specify an ammount of up to 100 messages " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + ".");

          Util.sendMessage(event, e.build());
        }
      }catch(Throwable e){
        e.printStackTrace();
      }

      IUser a = null;
      if(!event.getMessage().getMentions().isEmpty()){
        a = event.getMessage().getMentions().get(0);
      }
      int mc = 0;
      try{
        final int numm = num;
        if(a == null) {
          mc = RequestBuffer.request(() -> { return event.getChannel().getMessageHistory(numm+1).bulkDelete().size();}).get();
        } else{
          final Long uid = a.getLongID();
          mc = RequestBuffer.request(() -> { return event.getChannel().getMessageHistoryFrom(uid, 100).bulkDelete().size();}).get();
        }
      }catch (Throwable e){
        Util.sendMessage(event, String.format("`failed to delete %d messages`", num-1));
        e.printStackTrace();
      }finally{
        EmbedBuilder e = new EmbedBuilder();
        mc--;
        e.withColor(event.getAuthor().getColorForGuild(event.getGuild()));
        if(a == null)
          e.withTitle(String.format("Successfully deleted %d messages!", mc));
        else
          e.withTitle(String.format("Successfully deleted %d messages ", mc).concat("from " + event.getAuthor().getName() + "#" + event.getAuthor().getDiscriminator() + "!"));

        Util.sendMessage(event, e.build());
      }
    });
  }
  
}
