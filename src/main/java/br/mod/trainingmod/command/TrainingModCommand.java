package br.mod.trainingmod.command;

import br.mod.trainingmod.config.TrainingDifficulty;
import br.mod.trainingmod.config.TrainingKnockbackMode;
import br.mod.trainingmod.entity.EntityTrainingNpc;
import br.mod.trainingmod.session.TrainingSessionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import java.util.Arrays;
import java.util.List;

public class TrainingModCommand extends CommandBase {
    private static final List<String> ALIASES = Arrays.asList("tm");

    @Override
    public String getCommandName() {
        return "trainingmod";
    }

    @Override
    public List<String> getCommandAliases() {
        return ALIASES;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/trainingmod spawn <hard|easy|normal|extreme> | /trainingmod knockback <default|hacking|long-normal>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer player = (EntityPlayer) sender;
        TrainingSessionManager manager = TrainingSessionManager.getInstance();

        if (args.length == 0) {
            sendMessage(player, "Uso: " + getCommandUsage(sender));
            return;
        }

        String action = args[0].toLowerCase();
        if ("spawn".equals(action)) {
            handleSpawn(player, manager, args);
            return;
        }

        if ("knockback".equals(action)) {
            handleKnockback(player, manager, args);
            return;
        }

        sendMessage(player, "Comando desconhecido.");
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, net.minecraft.util.BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "spawn", "knockback");
        }
        if (args.length == 2 && "spawn".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, "easy", "normal", "hard", "extreme");
        }
        if (args.length == 2 && "knockback".equalsIgnoreCase(args[0])) {
            return getListOfStringsMatchingLastWord(args, "default", "hacking", "long-normal");
        }
        return null;
    }

    private void handleSpawn(EntityPlayer player, TrainingSessionManager manager, String[] args) {
        if (args.length < 2) {
            sendMessage(player, "Use: /trainingmod spawn <hard|easy|normal|extreme>");
            return;
        }

        TrainingDifficulty difficulty = TrainingDifficulty.fromString(args[1]);
        if (difficulty == null) {
            sendMessage(player, "Dificuldade inválida. Use: hard, easy, normal, extreme.");
            return;
        }

        EntityTrainingNpc npc = manager.spawnNpc(player, difficulty);
        if (npc == null) {
            sendMessage(player, "Não foi possível spawnar o NPC. Certifique-se de não estar no pacífico.");
            return;
        }

        sendMessage(
                player,
                "NPC spawnado. Dificuldade: " + difficulty.getKey()
                        + " | Reach: " + difficulty.getHitReach()
                        + " | Knockback: " + manager.getKnockbackMode().getKey()
        );
    }

    private void handleKnockback(EntityPlayer player, TrainingSessionManager manager, String[] args) {
        if (args.length < 2) {
            sendMessage(player, "Use: /trainingmod knockback <default|hacking|long-normal>");
            return;
        }

        TrainingKnockbackMode mode = TrainingKnockbackMode.fromString(args[1]);
        if (mode == null) {
            sendMessage(player, "Knockback inválido. Use: default, hacking, long-normal.");
            return;
        }

        manager.setKnockbackMode(mode);
        sendMessage(player, "Knockback do NPC definido para: " + mode.getKey());
    }

    private void sendMessage(EntityPlayer player, String message) {
        player.addChatMessage(new ChatComponentText("[TrainingMod] " + message));
    }
}

