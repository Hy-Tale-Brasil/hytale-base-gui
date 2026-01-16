/*     */ package org.zuxaw.plugin.commands;
/*     */ 
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.NameMatching;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandSender;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
/*     */ import com.hypixel.hytale.server.core.command.system.arguments.types.ArgumentType;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
/*     */ import com.hypixel.hytale.server.core.permissions.PermissionsModule;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.zuxaw.plugin.components.PlayerLevelData;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ import org.zuxaw.plugin.gui.StatsGUIPage;
/*     */ import org.zuxaw.plugin.services.LevelingService;
/*     */ import org.zuxaw.plugin.services.StatsService;
/*     */ import org.zuxaw.plugin.utils.TinyMsg;
/*     */ 
/*     */ public class RPGLevelingCommand
/*     */   extends AbstractCommandCollection {
/*  37 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   private final LevelingService levelingService;
/*     */   private final StatsService statsService;
/*     */   private final LevelingConfig config;
/*     */   
/*     */   public RPGLevelingCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/*  44 */     super("lvl", "RPG Leveling plugin commands.");
/*     */ 
/*     */     
/*  47 */     this.levelingService = levelingService;
/*  48 */     this.statsService = statsService;
/*  49 */     this.config = config;
/*     */ 
/*     */     
/*  52 */     addSubCommand((AbstractCommand)new GUICommand(levelingService, statsService, config));
/*  53 */     addSubCommand((AbstractCommand)new InfoCommand(levelingService, statsService, config));
/*     */ 
/*     */     
/*  56 */     addSubCommand((AbstractCommand)new SetLevelCommand(levelingService, statsService, config));
/*  57 */     addSubCommand((AbstractCommand)new SetPointsCommand(levelingService, statsService, config));
/*  58 */     addSubCommand((AbstractCommand)new ResetStatsCommand(levelingService, statsService, config));
/*  59 */     addSubCommand((AbstractCommand)new AddXPCommand(levelingService, statsService, config));
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean canGeneratePermission() {
/*  64 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   private static class GUICommand
/*     */     extends AbstractAsyncCommand
/*     */   {
/*     */     private final LevelingService levelingService;
/*     */     
/*     */     private final StatsService statsService;
/*     */     private final LevelingConfig config;
/*     */     
/*     */     public GUICommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/*  77 */       super("gui", "Open the stats management GUI.");
/*  78 */       this.levelingService = levelingService;
/*  79 */       this.statsService = statsService;
/*  80 */       this.config = config;
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean canGeneratePermission() {
/*  85 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/*  91 */       CommandSender sender = ctx.sender();
/*  92 */       if (sender instanceof Player) { Player player = (Player)sender;
/*  93 */         player.getWorldMapTracker().tick(0.0F);
/*  94 */         Ref<EntityStore> ref = player.getReference();
/*  95 */         if (ref != null && ref.isValid()) {
/*  96 */           Store<EntityStore> store = ref.getStore();
/*  97 */           World world = ((EntityStore)store.getExternalData()).getWorld();
/*  98 */           return CompletableFuture.runAsync(() -> {
/*     */                 try {
/*     */                   PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*     */                   
/*     */                   if (playerRef != null) {
/*     */                     player.getPageManager().openCustomPage(ref, store, (CustomUIPage)new StatsGUIPage(playerRef, this.levelingService, this.statsService, this.config));
/*     */                   } else {
/*     */                     ((HytaleLogger.Api)LOGGER.atWarning()).log("PlayerRef is null when trying to open stats GUI");
/*     */                   } 
/* 107 */                 } catch (Exception e) {
/*     */                   ((HytaleLogger.Api)LOGGER.atSevere()).log("Error opening stats GUI: " + e.getMessage());
/*     */                   e.printStackTrace();
/*     */                 } 
/*     */               }(Executor)world);
/*     */         } 
/* 113 */         ctx.sendMessage(TinyMsg.parse("<color:red>Unable to access your entity data. Please try again in a moment.</color>"));
/* 114 */         return CompletableFuture.completedFuture(null); }
/*     */ 
/*     */       
/* 117 */       ctx.sendMessage(TinyMsg.parse("<color:red>This command can only be used by players.</color>"));
/* 118 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class InfoCommand
/*     */     extends AbstractAsyncCommand
/*     */   {
/*     */     private final LevelingService levelingService;
/*     */     
/*     */     private final StatsService statsService;
/*     */     
/*     */     private final LevelingConfig config;
/*     */     
/*     */     public InfoCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/* 133 */       super("info", "Show all stats information and available commands.");
/* 134 */       this.levelingService = levelingService;
/* 135 */       this.statsService = statsService;
/* 136 */       this.config = config;
/*     */     }
/*     */ 
/*     */     
/*     */     protected boolean canGeneratePermission() {
/* 141 */       return false;
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/* 147 */       if (!ctx.isPlayer()) {
/* 148 */         ctx.sendMessage(TinyMsg.parse("<color:red>This command can only be used by players.</color>"));
/* 149 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */       
/* 152 */       UUID playerUuid = ctx.sender().getUuid();
/* 153 */       PlayerRef playerRef = Universe.get().getPlayer(playerUuid);
/* 154 */       if (playerRef == null) {
/* 155 */         ctx.sendMessage(TinyMsg.parse("<color:red>Unable to find your player data.</color>"));
/* 156 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */       
/* 159 */       return showInfo(ctx, playerRef);
/*     */     }
/*     */     
/*     */     private CompletableFuture<Void> showInfo(@Nonnull CommandContext ctx, @Nonnull PlayerRef playerRef) {
/* 163 */       sendInfo(ctx);
/* 164 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */ 
/*     */     
/*     */     private void sendInfo(@Nonnull CommandContext ctx) {
/* 169 */       ctx.sendMessage(TinyMsg.parse("<color:gold><b>=== RPG Leveling Info ===</b></color>"));
/*     */ 
/*     */       
/* 172 */       ctx.sendMessage(TinyMsg.parse("<color:yellow><b>Available Stats:</b></color>"));
/* 173 */       String statList = String.join(", ", (CharSequence[])StatsService.VALID_STATS);
/* 174 */       ctx.sendMessage(TinyMsg.parse(String.format("<color:aqua>%s</color> <color:gray>(+%.1f max per point)</color>", new Object[] { statList, 
/*     */                 
/* 176 */                 Double.valueOf(this.config.getStatValuePerPoint()) })));
/*     */ 
/*     */ 
/*     */       
/* 180 */       ctx.sendMessage(TinyMsg.parse("<color:yellow><b>Available Commands:</b></color>"));
/* 181 */       ctx.sendMessage(TinyMsg.parse("  <color:green>/lvl gui</color> - Open stats management GUI"));
/* 182 */       ctx.sendMessage(TinyMsg.parse("  <color:green>/lvl info</color> - Show this information"));
/*     */ 
/*     */       
/* 185 */       UUID senderUuid = ctx.sender().getUuid();
/*     */ 
/*     */       
/* 188 */       boolean isAdmin = (ctx.sender().hasPermission("hytale.command.admin") || PermissionsModule.get().getGroupsForUser(senderUuid).contains("OP"));
/*     */       
/* 190 */       if (isAdmin) {
/* 191 */         ctx.sendMessage(TinyMsg.parse("<color:yellow><b>Admin Commands:</b></color>"));
/* 192 */         ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl setlevel <player> <level></color> - Set player level"));
/* 193 */         ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl setpoints <player> <points></color> - Set available stat points"));
/* 194 */         ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl addxp <player> <xp></color> - Add experience points to player"));
/* 195 */         ctx.sendMessage(TinyMsg.parse("  <color:red>/lvl resetstats <player></color> - Reset allocated stats"));
/*     */       } else {
/* 197 */         ctx.sendMessage(TinyMsg.parse("<color:gray>Admin commands available (requires OP permission)</color>"));
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class SetLevelCommand
/*     */     extends AbstractAsyncCommand
/*     */   {
/*     */     private final LevelingService levelingService;
/*     */     
/*     */     private final StatsService statsService;
/*     */     private final LevelingConfig config;
/*     */     private final RequiredArg<String> playerArg;
/*     */     private final RequiredArg<Integer> levelArg;
/*     */     
/*     */     public SetLevelCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/* 214 */       super("setlevel", "Set a player's level (admin only).");
/* 215 */       setPermissionGroups(new String[] { "OP" });
/*     */       
/* 217 */       this.levelingService = levelingService;
/* 218 */       this.statsService = statsService;
/* 219 */       this.config = config;
/*     */       
/* 221 */       this.playerArg = withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
/* 222 */       this.levelArg = withRequiredArg("level", "Level to set (1-" + config.getMaxLevel() + ")", (ArgumentType)ArgTypes.INTEGER);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/* 228 */       String playerName = (String)this.playerArg.get(ctx);
/* 229 */       Integer level = (Integer)this.levelArg.get(ctx);
/*     */ 
/*     */       
/* 232 */       PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
/* 233 */       if (targetPlayer == null) {
/* 234 */         ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Player <color:yellow>%s</color> not found or not online.</color>", new Object[] { playerName })));
/*     */ 
/*     */ 
/*     */         
/* 238 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */ 
/*     */       
/* 242 */       if (level.intValue() < 1 || level.intValue() > this.config.getMaxLevel()) {
/* 243 */         ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Level must be between 1 and %d.</color>", new Object[] {
/*     */                   
/* 245 */                   Integer.valueOf(this.config.getMaxLevel())
/*     */                 })));
/* 247 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */ 
/*     */       
/* 251 */       boolean success = this.levelingService.setPlayerLevel(targetPlayer, level.intValue(), this.config);
/*     */       
/* 253 */       if (success) {
/* 254 */         ctx.sendMessage(TinyMsg.parse(String.format("<color:green>Set <color:yellow>%s</color> to level <color:gold><b>%d</b></color>.</color>", new Object[] { targetPlayer
/*     */                   
/* 256 */                   .getUsername(), level })));
/*     */       } else {
/*     */         
/* 259 */         ctx.sendMessage(TinyMsg.parse("<color:red>Failed to set player level. Please try again.</color>"));
/*     */       } 
/*     */       
/* 262 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class SetPointsCommand
/*     */     extends AbstractAsyncCommand
/*     */   {
/*     */     private final LevelingService levelingService;
/*     */     
/*     */     private final StatsService statsService;
/*     */     private final LevelingConfig config;
/*     */     private final RequiredArg<String> playerArg;
/*     */     private final RequiredArg<Integer> pointsArg;
/*     */     
/*     */     public SetPointsCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/* 278 */       super("setpoints", "Set available stat points for a player (admin only).");
/* 279 */       setPermissionGroups(new String[] { "OP" });
/*     */       
/* 281 */       this.levelingService = levelingService;
/* 282 */       this.statsService = statsService;
/* 283 */       this.config = config;
/*     */       
/* 285 */       this.playerArg = withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
/* 286 */       this.pointsArg = withRequiredArg("points", "Number of points to set", (ArgumentType)ArgTypes.INTEGER);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/* 292 */       String playerName = (String)this.playerArg.get(ctx);
/* 293 */       Integer points = (Integer)this.pointsArg.get(ctx);
/*     */ 
/*     */       
/* 296 */       PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
/* 297 */       if (targetPlayer == null) {
/* 298 */         ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Player <color:yellow>%s</color> not found or not online.</color>", new Object[] { playerName })));
/*     */ 
/*     */ 
/*     */         
/* 302 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */ 
/*     */       
/* 306 */       if (points.intValue() < 0) {
/* 307 */         ctx.sendMessage(TinyMsg.parse("<color:red>Points must be 0 or greater.</color>"));
/* 308 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */ 
/*     */       
/* 312 */       return setPlayerPoints(ctx, targetPlayer, points.intValue());
/*     */     }
/*     */ 
/*     */     
/*     */     private CompletableFuture<Void> setPlayerPoints(@Nonnull CommandContext ctx, @Nonnull PlayerRef targetPlayer, int points) {
/* 317 */       Ref<EntityStore> entityRef = targetPlayer.getReference();
/*     */       
/* 319 */       if (entityRef != null && entityRef.isValid()) {
/* 320 */         UUID worldUuid = targetPlayer.getWorldUuid();
/* 321 */         if (worldUuid != null) {
/* 322 */           World world = Universe.get().getWorld(worldUuid);
/* 323 */           if (world != null && world.isAlive()) {
/* 324 */             return CompletableFuture.runAsync(() -> world.execute(()));
/*     */           }
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 360 */       Holder<EntityStore> holder = targetPlayer.getHolder();
/* 361 */       if (holder == null) {
/* 362 */         ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Player <color:yellow>%s</color> is not in a world and has no holder data.</color>", new Object[] { targetPlayer
/*     */                   
/* 364 */                   .getUsername() })));
/*     */         
/* 366 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */       
/* 369 */       PlayerLevelData data = (PlayerLevelData)holder.ensureAndGetComponent(this.levelingService.getPlayerLevelDataType());
/* 370 */       if (data == null) {
/* 371 */         data = new PlayerLevelData();
/*     */       }
/*     */       
/* 374 */       data.setAvailableStatPoints(points);
/* 375 */       holder.putComponent(this.levelingService.getPlayerLevelDataType(), (Component)data);
/*     */       
/* 377 */       ctx.sendMessage(TinyMsg.parse(String.format("<color:green>Set <color:yellow>%s</color>'s available stat points to <color:gold><b>%d</b></color>.</color>", new Object[] { targetPlayer
/*     */                 
/* 379 */                 .getUsername(), Integer.valueOf(points) })));
/*     */ 
/*     */       
/* 382 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ResetStatsCommand
/*     */     extends AbstractAsyncCommand
/*     */   {
/*     */     private final LevelingService levelingService;
/*     */     
/*     */     private final StatsService statsService;
/*     */     private final LevelingConfig config;
/*     */     private final RequiredArg<String> playerArg;
/*     */     
/*     */     public ResetStatsCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/* 397 */       super("resetstats", "Reset a player's allocated stats (admin only).");
/* 398 */       setPermissionGroups(new String[] { "OP" });
/*     */       
/* 400 */       this.levelingService = levelingService;
/* 401 */       this.statsService = statsService;
/* 402 */       this.config = config;
/*     */       
/* 404 */       this.playerArg = withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/* 410 */       String playerName = (String)this.playerArg.get(ctx);
/*     */ 
/*     */       
/* 413 */       PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
/* 414 */       if (targetPlayer == null) {
/* 415 */         ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Player <color:yellow>%s</color> not found or not online.</color>", new Object[] { playerName })));
/*     */ 
/*     */ 
/*     */         
/* 419 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */ 
/*     */       
/* 423 */       boolean success = this.statsService.resetAllocatedStats(targetPlayer, this.config);
/*     */       
/* 425 */       if (success) {
/* 426 */         ctx.sendMessage(TinyMsg.parse(String.format("<color:green>Reset stats for <color:yellow>%s</color>. Allocated points have been returned to available points.</color>", new Object[] { targetPlayer
/*     */                   
/* 428 */                   .getUsername() })));
/*     */       } else {
/*     */         
/* 431 */         ctx.sendMessage(TinyMsg.parse("<color:red>Failed to reset player stats. Please try again.</color>"));
/*     */       } 
/*     */       
/* 434 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class AddXPCommand
/*     */     extends AbstractAsyncCommand
/*     */   {
/*     */     private final LevelingService levelingService;
/*     */     
/*     */     private final StatsService statsService;
/*     */     private final LevelingConfig config;
/*     */     private final RequiredArg<String> playerArg;
/*     */     private final RequiredArg<Double> xpArg;
/*     */     
/*     */     public AddXPCommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/* 450 */       super("addxp", "Add experience points to a player (admin only).");
/* 451 */       setPermissionGroups(new String[] { "OP" });
/*     */       
/* 453 */       this.levelingService = levelingService;
/* 454 */       this.statsService = statsService;
/* 455 */       this.config = config;
/*     */       
/* 457 */       this.playerArg = withRequiredArg("player", "Player name", (ArgumentType)ArgTypes.STRING);
/* 458 */       this.xpArg = withRequiredArg("xp", "Amount of XP to add", (ArgumentType)ArgTypes.DOUBLE);
/*     */     }
/*     */ 
/*     */     
/*     */     @Nonnull
/*     */     protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/* 464 */       String playerName = (String)this.playerArg.get(ctx);
/* 465 */       Double xp = (Double)this.xpArg.get(ctx);
/*     */ 
/*     */       
/* 468 */       PlayerRef targetPlayer = Universe.get().getPlayerByUsername(playerName, NameMatching.EXACT_IGNORE_CASE);
/* 469 */       if (targetPlayer == null) {
/* 470 */         ctx.sendMessage(TinyMsg.parse(String.format("<color:red>Player <color:yellow>%s</color> not found or not online.</color>", new Object[] { playerName })));
/*     */ 
/*     */ 
/*     */         
/* 474 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */ 
/*     */       
/* 478 */       if (xp.doubleValue() <= 0.0D) {
/* 479 */         ctx.sendMessage(TinyMsg.parse("<color:red>XP amount must be greater than 0.</color>"));
/* 480 */         return CompletableFuture.completedFuture(null);
/*     */       } 
/*     */ 
/*     */       
/* 484 */       this.levelingService.addExperience(targetPlayer, xp.doubleValue(), this.config, null);
/*     */       
/* 486 */       ctx.sendMessage(TinyMsg.parse(String.format("<color:green>Added <color:gold><b>%.1f</b></color> XP to <color:yellow>%s</color>.</color>", new Object[] { xp, targetPlayer
/*     */                 
/* 488 */                 .getUsername() })));
/*     */ 
/*     */       
/* 491 */       return CompletableFuture.completedFuture(null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\commands\RPGLevelingCommand.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */