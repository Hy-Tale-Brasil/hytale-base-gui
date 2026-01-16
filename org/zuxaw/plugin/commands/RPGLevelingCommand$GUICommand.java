/*     */ package org.zuxaw.plugin.commands;
/*     */ 
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandContext;
/*     */ import com.hypixel.hytale.server.core.command.system.CommandSender;
/*     */ import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ import org.zuxaw.plugin.gui.StatsGUIPage;
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
/*     */ class GUICommand
/*     */   extends AbstractAsyncCommand
/*     */ {
/*     */   private final LevelingService levelingService;
/*     */   private final StatsService statsService;
/*     */   private final LevelingConfig config;
/*     */   
/*     */   public GUICommand(LevelingService levelingService, StatsService statsService, LevelingConfig config) {
/*  77 */     super("gui", "Open the stats management GUI.");
/*  78 */     this.levelingService = levelingService;
/*  79 */     this.statsService = statsService;
/*  80 */     this.config = config;
/*     */   }
/*     */ 
/*     */   
/*     */   protected boolean canGeneratePermission() {
/*  85 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   protected CompletableFuture<Void> executeAsync(@Nonnull CommandContext ctx) {
/*  91 */     CommandSender sender = ctx.sender();
/*  92 */     if (sender instanceof Player) { Player player = (Player)sender;
/*  93 */       player.getWorldMapTracker().tick(0.0F);
/*  94 */       Ref<EntityStore> ref = player.getReference();
/*  95 */       if (ref != null && ref.isValid()) {
/*  96 */         Store<EntityStore> store = ref.getStore();
/*  97 */         World world = ((EntityStore)store.getExternalData()).getWorld();
/*  98 */         return CompletableFuture.runAsync(() -> {
/*     */               try {
/*     */                 PlayerRef playerRef = (PlayerRef)store.getComponent(ref, PlayerRef.getComponentType());
/*     */                 
/*     */                 if (playerRef != null) {
/*     */                   player.getPageManager().openCustomPage(ref, store, (CustomUIPage)new StatsGUIPage(playerRef, this.levelingService, this.statsService, this.config));
/*     */                 } else {
/*     */                   ((HytaleLogger.Api)LOGGER.atWarning()).log("PlayerRef is null when trying to open stats GUI");
/*     */                 } 
/* 107 */               } catch (Exception e) {
/*     */                 ((HytaleLogger.Api)LOGGER.atSevere()).log("Error opening stats GUI: " + e.getMessage());
/*     */                 e.printStackTrace();
/*     */               } 
/*     */             }(Executor)world);
/*     */       } 
/* 113 */       ctx.sendMessage(TinyMsg.parse("<color:red>Unable to access your entity data. Please try again in a moment.</color>"));
/* 114 */       return CompletableFuture.completedFuture(null); }
/*     */ 
/*     */     
/* 117 */     ctx.sendMessage(TinyMsg.parse("<color:red>This command can only be used by players.</color>"));
/* 118 */     return CompletableFuture.completedFuture(null);
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\commands\RPGLevelingCommand$GUICommand.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */