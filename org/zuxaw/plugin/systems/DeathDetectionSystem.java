/*     */ package org.zuxaw.plugin.systems;
/*     */ 
/*     */ import com.hypixel.hytale.component.ArchetypeChunk;
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.SystemGroup;
/*     */ import com.hypixel.hytale.component.query.Query;
/*     */ import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.Config;
/*     */ import com.hypixel.hytale.server.npc.entities.NPCEntity;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ import org.zuxaw.plugin.services.LevelingService;
/*     */ import org.zuxaw.plugin.utils.NotificationHelper;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DeathDetectionSystem
/*     */   extends EntityTickingSystem<EntityStore>
/*     */ {
/*     */   private final Map<UUID, UUID> lastAttackers;
/*     */   private final Map<UUID, String> entityNames;
/*     */   private final LevelingService levelingService;
/*     */   private final Config<LevelingConfig> config;
/*  45 */   private final Map<UUID, Boolean> processedDeaths = new ConcurrentHashMap<>();
/*  46 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */   
/*     */   public DeathDetectionSystem(Map<UUID, UUID> lastAttackers, Map<UUID, String> entityNames, LevelingService levelingService, Config<LevelingConfig> config) {
/*  50 */     this.lastAttackers = lastAttackers;
/*  51 */     this.entityNames = entityNames;
/*  52 */     this.levelingService = levelingService;
/*  53 */     this.config = config;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void tick(float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
/*  59 */     Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
/*     */     
/*  61 */     if (entityRef == null || !entityRef.isValid()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  66 */     DeathComponent deathComponent = (DeathComponent)store.getComponent(entityRef, DeathComponent.getComponentType());
/*  67 */     if (deathComponent == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  72 */     UUIDComponent uuidComponent = (UUIDComponent)store.getComponent(entityRef, UUIDComponent.getComponentType());
/*  73 */     if (uuidComponent == null) {
/*     */       return;
/*     */     }
/*  76 */     UUID entityUuid = uuidComponent.getUuid();
/*     */ 
/*     */     
/*  79 */     if (this.processedDeaths.containsKey(entityUuid)) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*  84 */     this.processedDeaths.put(entityUuid, Boolean.valueOf(true));
/*     */ 
/*     */     
/*  87 */     String entityName = this.entityNames.remove(entityUuid);
/*     */     
/*  89 */     if (entityName == null) {
/*     */       
/*  91 */       NPCEntity npcEntity = (NPCEntity)store.getComponent(entityRef, NPCEntity.getComponentType());
/*  92 */       if (npcEntity != null) {
/*  93 */         entityName = npcEntity.getNPCTypeId();
/*     */       } else {
/*  95 */         entityName = "Unknown Entity";
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 100 */     String displayName = entityName.replace("_", " ");
/*     */ 
/*     */     
/* 103 */     UUID attackerUuid = this.lastAttackers.remove(entityUuid);
/*     */     
/* 105 */     if (attackerUuid != null) {
/*     */       
/* 107 */       PlayerRef playerRef = Universe.get().getPlayer(attackerUuid);
/*     */       
/* 109 */       if (playerRef != null) {
/* 110 */         String playerName = playerRef.getUsername();
/*     */ 
/*     */         
/* 113 */         double maxHealth = 0.0D;
/* 114 */         String maxHealthInfo = "N/A";
/* 115 */         ComponentType<EntityStore, EntityStatMap> statMapType = EntityStatsModule.get().getEntityStatMapComponentType();
/* 116 */         EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, statMapType);
/* 117 */         if (statMap != null) {
/* 118 */           int healthIndex = DefaultEntityStatTypes.getHealth();
/* 119 */           EntityStatValue healthStat = statMap.get(healthIndex);
/* 120 */           if (healthStat != null) {
/* 121 */             maxHealth = healthStat.getMax();
/* 122 */             maxHealthInfo = String.valueOf(maxHealth);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 127 */         if (maxHealth > 0.0D) {
/* 128 */           double xp = this.levelingService.calculateXPFromMaxHealth(maxHealth, (LevelingConfig)this.config.get());
/* 129 */           ((HytaleLogger.Api)LOGGER.atInfo()).log(">>> CALCULATED XP: " + playerName + " should gain " + String.format("%.1f", new Object[] { Double.valueOf(xp) }) + " XP from " + entityName + " (maxHealth: " + maxHealth + ")");
/* 130 */           if (xp > 0.0D) {
/* 131 */             this.levelingService.addExperience(playerRef, xp, (LevelingConfig)this.config.get(), commandBuffer);
/* 132 */             ((HytaleLogger.Api)LOGGER.atInfo()).log(">>> XP AWARDED: " + playerName + " gained " + String.format("%.1f", new Object[] { Double.valueOf(xp) }) + " XP from killing " + entityName);
/*     */           } else {
/* 134 */             ((HytaleLogger.Api)LOGGER.atWarning()).log(">>> XP is 0 or negative, not awarding XP");
/*     */           } 
/*     */         } else {
/* 137 */           ((HytaleLogger.Api)LOGGER.atWarning()).log(">>> Max health is 0 or not found for entity: " + entityName + ", cannot award XP");
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 142 */         String message = String.format("<color:yellow>You killed: </color><color:gold>%s</color>", new Object[] { displayName });
/* 143 */         NotificationHelper.sendNotification(playerRef, message);
/*     */ 
/*     */         
/* 146 */         ((HytaleLogger.Api)LOGGER.atInfo()).log(">>> PLAYER KILL: " + playerName + " killed " + entityName + " (UUID: " + String.valueOf(entityUuid) + ", Max Health: " + maxHealthInfo + ")");
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public SystemGroup<EntityStore> getGroup() {
/* 154 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Query<EntityStore> getQuery() {
/* 161 */     return (Query<EntityStore>)DeathComponent.getComponentType();
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\systems\DeathDetectionSystem.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */