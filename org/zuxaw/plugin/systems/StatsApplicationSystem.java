/*    */ package org.zuxaw.plugin.systems;
/*    */ 
/*    */ import com.hypixel.hytale.component.ArchetypeChunk;
/*    */ import com.hypixel.hytale.component.CommandBuffer;
/*    */ import com.hypixel.hytale.component.Ref;
/*    */ import com.hypixel.hytale.component.Store;
/*    */ import com.hypixel.hytale.component.query.Query;
/*    */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*    */ import com.hypixel.hytale.logger.HytaleLogger;
/*    */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*    */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*    */ import com.hypixel.hytale.server.core.util.Config;
/*    */ import java.util.Map;
/*    */ import java.util.UUID;
/*    */ import java.util.concurrent.ConcurrentHashMap;
/*    */ import javax.annotation.Nonnull;
/*    */ import org.zuxaw.plugin.components.PlayerLevelData;
/*    */ import org.zuxaw.plugin.config.LevelingConfig;
/*    */ import org.zuxaw.plugin.services.StatsService;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatsApplicationSystem
/*    */   extends EntityTickingSystem<EntityStore>
/*    */ {
/*    */   @Nonnull
/*    */   public Query<EntityStore> getQuery() {
/* 33 */     return (Query<EntityStore>)Query.any();
/*    */   }
/*    */   
/* 36 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*    */   
/*    */   private final StatsService statsService;
/*    */   private final Config<LevelingConfig> config;
/* 40 */   private final Map<UUID, Boolean> statsApplied = new ConcurrentHashMap<>();
/*    */   
/*    */   public StatsApplicationSystem(StatsService statsService, Config<LevelingConfig> config) {
/* 43 */     this.statsService = statsService;
/* 44 */     this.config = config;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/* 50 */     Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
/*    */ 
/*    */     
/* 53 */     UUIDComponent uuidComponent = (UUIDComponent)store.getComponent(entityRef, UUIDComponent.getComponentType());
/* 54 */     if (uuidComponent == null) {
/*    */       return;
/*    */     }
/* 57 */     UUID entityUuid = uuidComponent.getUuid();
/*    */ 
/*    */     
/* 60 */     if (this.statsApplied.containsKey(entityUuid)) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 65 */     PlayerLevelData levelData = (PlayerLevelData)store.getComponent(entityRef, this.statsService.getPlayerLevelDataType());
/* 66 */     if (levelData == null) {
/* 67 */       this.statsApplied.put(entityUuid, Boolean.valueOf(true));
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 72 */     if (levelData.getAllocatedStats().isEmpty()) {
/* 73 */       this.statsApplied.put(entityUuid, Boolean.valueOf(true));
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 78 */     this.statsService.applyStatModifiers(entityRef, store, levelData, (LevelingConfig)this.config.get());
/*    */ 
/*    */     
/* 81 */     this.statsApplied.put(entityUuid, Boolean.valueOf(true));
/*    */   }
/*    */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\systems\StatsApplicationSystem.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */