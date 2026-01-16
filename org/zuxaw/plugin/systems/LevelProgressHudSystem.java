/*     */ package org.zuxaw.plugin.systems;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.server.core.entity.EntityUtils;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.zuxaw.plugin.RPGLevelingPlugin;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ import org.zuxaw.plugin.hud.HudManagerService;
/*     */ import org.zuxaw.plugin.hud.LevelProgressHud;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LevelProgressHudSystem
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   @Nonnull
/*     */   private final Query<EntityStore> query;
/*  31 */   private final Map<PlayerRef, LevelProgressHud> huds = new HashMap<>();
/*     */   private static final String HUD_IDENTIFIER = "LevelProgress";
/*     */   
/*     */   public LevelProgressHudSystem() {
/*  35 */     this.query = (Query<EntityStore>)Query.and(new Query[] { (Query)Player.getComponentType() });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  41 */     Holder<EntityStore> holder = EntityUtils.toHolder(index, archetypeChunk);
/*  42 */     Player player = (Player)holder.getComponent(Player.getComponentType());
/*  43 */     PlayerRef playerRef = (PlayerRef)holder.getComponent(PlayerRef.getComponentType());
/*     */     
/*  45 */     if (player == null || playerRef == null) {
/*     */       return;
/*     */     }
/*     */     
/*  49 */     RPGLevelingPlugin plugin = RPGLevelingPlugin.get();
/*  50 */     if (plugin == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  55 */     if (!((LevelingConfig)plugin.getConfig().get()).isEnableHUD()) {
/*     */       
/*  57 */       LevelProgressHud existingHud = this.huds.remove(playerRef);
/*  58 */       if (existingHud != null) {
/*  59 */         HudManagerService hudManagerService1 = plugin.getHudManagerService();
/*  60 */         if (hudManagerService1 != null) {
/*  61 */           hudManagerService1.hideCustomHud(player, playerRef, "LevelProgress");
/*     */         }
/*     */       } 
/*     */       
/*     */       return;
/*     */     } 
/*  67 */     HudManagerService hudManagerService = plugin.getHudManagerService();
/*  68 */     if (hudManagerService == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  73 */     LevelProgressHud hud = this.huds.get(playerRef);
/*  74 */     if (hud == null) {
/*     */       
/*  76 */       hud = new LevelProgressHud(playerRef, plugin.getLevelingService(), (LevelingConfig)plugin.getConfig().get());
/*  77 */       this.huds.put(playerRef, hud);
/*     */       
/*  79 */       hudManagerService.setCustomHud(player, playerRef, "LevelProgress", (CustomUIHud)hud);
/*     */     } else {
/*     */       
/*  82 */       hud.update();
/*     */ 
/*     */       
/*  85 */       hudManagerService.setCustomHud(player, playerRef, "LevelProgress", (CustomUIHud)hud);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/*  92 */     return this.query;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeHud(@Nonnull PlayerRef playerRef) {
/* 101 */     this.huds.remove(playerRef);
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\systems\LevelProgressHudSystem.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */