/*     */ package org.zuxaw.plugin.commands;
/*     */ 
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.NameMatching;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.zuxaw.plugin.components.PlayerLevelData;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ class SetPointsCommand
/*     */   extends AbstractAsyncCommand
/*     */ {
/*     */   private final LevelingService levelingService;
/*     */   private final StatsService statsService;
/*     */   private final LevelingConfig config;
/*     */   private final RequiredArg<String> playerArg;
/*     */   private final RequiredArg<Integer> pointsArg;
/*     */   
/*     */   public SetPointsCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/* 278 */     super("setpoints", "Set available stat points for a player (admin only).");
/* 279 */     setPermissionGroups(new String[] { "OP" });
/*     */     
/* 281 */     this.levelingService = levelingService;
/* 282 */     this.statsService = statsService;
/* 283 */     this.config = config;
/*     */     
/* 285 */     this.playerArg = withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
/* 286 */     this.pointsArg = withRequiredArg("points", "Number of points to set", (ArgumentType)ArgTypes.INTEGER);
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/* 292 */     String playerName = (String)this.playerArg.get(ctx);
/* 293 */     Integer points = (Integer)this.pointsArg.get(ctx);
/*     */ 
/*     */     
/* 296 */     PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
/* 297 */     if (targetPlayer == null) {
/* 298 */       ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Player <color:yellow>%s</color> not found or not online.</color>", new Object[] { playerName })));
/*     */ 
/*     */ 
/*     */       
/* 302 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */ 
/*     */     
/* 306 */     if (points.intValue() < 0) {
/* 307 */       ctx.sendMessage(TinyMsg.parse("<color:red>Points must be 0 or greater.</color>"));
/* 308 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */ 
/*     */     
/* 312 */     return setPlayerPoints(ctx, targetPlayer, points.intValue());
/*     */   }
/*     */ 
/*     */   
/*     */   private CompletableFuture<Void> setPlayerPoints(@Nonnull CommandContext ctx, @Nonnull PlayerRef targetPlayer, int points) {
/* 317 */     Ref<EntityStore> entityRef = targetPlayer.getReference();
/*     */     
/* 319 */     if (entityRef != null && entityRef.isValid()) {
/* 320 */       UUID worldUuid = targetPlayer.getWorldUuid();
/* 321 */       if (worldUuid != null) {
/* 322 */         World world = Universe.get().getWorld(worldUuid);
/* 323 */         if (world != null && world.isAlive()) {
/* 324 */           return CompletableFuture.runAsync(() -> world.execute(()));
/*     */         }
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 360 */     Holder<EntityStore> holder = targetPlayer.getHolder();
/* 361 */     if (holder == null) {
/* 362 */       ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Player <color:yellow>%s</color> is not in a world and has no holder data.</color>", new Object[] { targetPlayer
/*     */                 
/* 364 */                 .getUsername() })));
/*     */       
/* 366 */       return CompletableFuture.completedFuture(null);
/*     */     } 
/*     */     
/* 369 */     PlayerLevelData data = (PlayerLevelData)holder.ensureAndGetComponent(this.levelingService.getPlayerLevelDataType());
/* 370 */     if (data == null) {
/* 371 */       data = new PlayerLevelData();
/*     */     }
/*     */     
/* 374 */     data.setAvailableStatPoints(points);
/* 375 */     holder.putComponent(this.levelingService.getPlayerLevelDataType(), (Component)data);
/*     */     
/* 377 */     ctx.sendMessage(TinyMsg.parse(String.format("<color:green>Set <color:yellow>%s</color>'s available stat points to <color:gold><b>%d</b></color>.</color>", new Object[] { targetPlayer
/*     */               
/* 379 */               .getUsername(), Integer.valueOf(points) })));
/*     */ 
/*     */     
/* 382 */     return CompletableFuture.completedFuture(null);
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\commands\RPGLevelingCommand$SetPointsCommand.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */