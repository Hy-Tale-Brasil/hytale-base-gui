/*     */ package org.zuxaw.plugin.commands;
/*     */ 
/*     */ import com.hypixel.hytale.server.core.NameMatching;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
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
/*     */ class SetLevelCommand
/*     */   extends AbstractAsyncCommand
/*     */ {
/*     */   private final LevelingService levelingService;
/*     */   private final StatsService statsService;
/*     */   private final LevelingConfig config;
/*     */   private final RequiredArg<String> playerArg;
/*     */   private final RequiredArg<Integer> levelArg;
/*     */   
/*     */   public SetLevelCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/* 214 */     super("setlevel", "Set a player's level (admin only).");
/* 215 */     setPermissionGroups(new String[] { "OP" });
/*     */     
/* 217 */     this.levelingService = levelingService;
/* 218 */     this.statsService = statsService;
/* 219 */     this.config = config;
/*     */     
/* 221 */     this.playerArg = withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
/* 222 */     this.levelArg = withRequiredArg("level", "Level to set (1-" + config.getMaxLevel() + ")", (ArgumentType)ArgTypes.INTEGER);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/* 228 */     String playerName = (String)this.playerArg.get(ctx);
/* 229 */     Integer level = (Integer)this.levelArg.get(ctx);
/*     */ 
/*     */     
/* 232 */     PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
/* 233 */     if (targetPlayer == null) {
/* 234 */       ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Player <color:yellow>%s</color> not found or not online.</color>", new Object[] { playerName })));
/*     */ 
/*     */ 
/*     */       
/* 238 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */ 
/*     */     
/* 242 */     if (level.intValue() < 1 || level.intValue() > this.config.getMaxLevel()) {
/* 243 */       ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Level must be between 1 and %d.</color>", new Object[] {
/*     */                 
/* 245 */                 Integer.valueOf(this.config.getMaxLevel())
/*     */               })));
/* 247 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */ 
/*     */     
/* 251 */     boolean success = this.levelingService.setPlayerLevel(targetPlayer, level.intValue(), this.config);
/*     */     
/* 253 */     if (success) {
/* 254 */       ctx.sendMessage(TinyMsg.parse(String.format("<color:green>Set <color:yellow>%s</color> to level <color:gold><b>%d</b></color>.</color>", new Object[] { targetPlayer
/*     */                 
/* 256 */                 .getUsername(), level })));
/*     */     } else {
/*     */       
/* 259 */       ctx.sendMessage(TinyMsg.parse("<color:red>Failed to set player level. Please try again.</color>"));
/*     */     } 
/*     */     
/* 262 */     return CompletableFuture.completedFuture(null);
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\commands\RPGLevelingCommand$SetLevelCommand.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */