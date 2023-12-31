package io.redspace.ironsspellbooks.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.redspace.ironsspellbooks.IronsSpellbooks;
import io.redspace.ironsspellbooks.player.DamageTracker;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class DamageTrackerCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> command = dispatcher.register(Commands.literal("damageTracker")
                .requires((p) -> p.hasPermission(2))
                .then(Commands.literal("clear")
                        .executes((context) -> clear()))
                .then(Commands.literal("dump")
                        .executes((context) -> dump(context.getSource())))
        );
    }

    private static int clear() {
        DamageTracker.INSTANCE.clear();
        return 1;
    }

    private static int dump(CommandSourceStack source) {
        try {
            var file = new File("damageTracker.txt");
            var writer = new BufferedWriter(new FileWriter(file));
            writer.write(DamageTracker.INSTANCE.toString());
            writer.close();

            Component component = Component.literal(file.getName()).withStyle(ChatFormatting.UNDERLINE).withStyle((style) -> {
                return style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file.getAbsolutePath()));
            });

            source.sendSuccess(() -> Component.translatable("commands.irons_spellbooks.generate_mod_list.success", component), true);

        } catch (Exception e) {
            IronsSpellbooks.LOGGER.info(e.getMessage());
        }

        return 1;
    }
}
