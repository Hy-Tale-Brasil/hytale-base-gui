/*     */ package org.zuxaw.plugin.commands;
/*     */ 
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.permissions.PermissionsModule;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ import org.zuxaw.plugin.services.LevelingService;
/*     */ import org.zuxaw.plugin.services.StatsService;
/*     */ import org.zuxaw.plugin.utils.TinyMsg;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class InfoCommand
/*     */   extends AbstractAsyncCommand
/*     */ {
/*     */   private final LevelingService levelingService;
/*     */   private final StatsService statsService;
/*     */   private final LevelingConfig config;
/*     */   
/*     */   public InfoCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/* 133 */     super("info", "Show all stats information and available commands.");
/* 134 */     this.levelingService = levelingService;
/* 135 */     this.statsService = statsService;
/* 136 */     this.config = config;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean canGeneratePermission() {
/* 141 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/* 147 */     if (!ctx.isPlayer()) {
/* 148 */       ctx.sendMessage(TinyMsg.parse("<color:red>This command can only be used by players.</color>"));
/* 149 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/* 152 */     UUID playerUuid = ctx.sender().getUuid();
/* 153 */     PlayerRef playerRef = Universe.get().getPlayer(playerUuid);
/* 154 */     if (playerRef == null) {
/* 155 */       ctx.sendMessage(TinyMsg.parse("<color:red>Unable to find your player data.</color>"));
/* 156 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/* 159 */     return showInfo(ctx, playerRef);
/*     */   }
/*     */   
/*     */   private CompletableFuture<Void> showInfo(@Nonnull CommandContext ctx, @Nonnull PlayerRef playerRef) {
/* 163 */     sendInfo(ctx);
/* 164 */     return CompletableFuture.completedFuture(null);
/*     */   }
/*     */ 
/*     */   
/*     */   private void sendInfo(@Nonnull CommandContext ctx) {
/* 169 */     ctx.sendMessage(TinyMsg.parse("<color:gold><b>=== RPG Leveling Info ===</b></color>"));
/*     */ 
/*     */     
/* 172 */     ctx.sendMessage(TinyMsg.parse("<color:yellow><b>Available Stats:</b></color>"));
/* 173 */     String statList = String.join(", ", (CharSequence[])StatsService.VALID_STATS);
/* 174 */     ctx.sendMessage(TinyMsg.parse(String.format("<color:aqua>%s</color> <color:gray>(+%.1f max per point)</color>", new Object[] { statList, 
/*     */               
/* 176 */               Double.valueOf(this.config.getStatValuePerPoint()) })));
/*     */ 
/*     */ 
/*     */     
/* 180 */     ctx.sendMessage(TinyMsg.parse("<color:yellow><b>Available Commands:</b></color>"));
/* 181 */     ctx.sendMessage(TinyMsg.parse("  <color:green>/lvl gui</color> - Open stats management GUI"));
/* 182 */     ctx.sendMessage(TinyMsg.parse("  <color:green>/lvl info</color> - Show this information"));
/*     */ 
/*     */     
/* 185 */     UUID senderUuid = ctx.sender().getUuid();
/*     */ 
/*     */     
/* 188 */     boolean isAdmin = (ctx.sender().hasPermission("hytale.command.admin") || PermissionsModule.get().getGroupsForUser(senderUuid).contains("OP"));
/*     */     
/* 190 */     if (isAdmin) {
/* 191 */       ctx.sendMessage(TinyMsg.parse("<color:yellow><b>Admin Commands:</b></color>"));
/* 192 */       ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl setlevel <player> <level></color> - Set player level"));
/* 193 */       ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl setpoints <player> <points></color> - Set available stat points"));
/* 194 */       ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl addxp <player> <xp></color> - Add experience points to player"));
/* 195 */       ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl resetstats <player></color> - Reset allocated stats"));
/*     */     } else {
/* 197 */       ctx.sendMessage(TinyMsg.parse("<color:gray>Admin commands available (requires OP permission)</color>"));
/*     */     } 
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\commands\RPGLevelingCommand$InfoCommand.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */