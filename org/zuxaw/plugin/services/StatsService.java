/*     */ package org.zuxaw.plugin.services;
/*     */ 
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.asset.EntityStatType;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.modifier.Modifier;
/*     */ import com.hypixel.hytale.server.core.modules.entitystats.modifier.StaticModifier;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.zuxaw.plugin.components.PlayerLevelData;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ 
/*     */ 
/*     */ public class StatsService
/*     */ {
/*  30 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   private final ComponentType<EntityStore, PlayerLevelData> playerLevelDataType;
/*     */   
/*     */   private final ComponentType<EntityStore, EntityStatMap> statMapType;
/*  35 */   public static final String[] VALID_STATS = new String[] { "Health", "Stamina", "Mana", "Ammo", "Oxygen", "StaminaRegenDelay", "MagicCharges", "Immunity" };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StatsService(@Nonnull ComponentType<EntityStore, PlayerLevelData> playerLevelDataType) {
/*  45 */     this.playerLevelDataType = playerLevelDataType;
/*  46 */     this.statMapType = EntityStatsModule.get().getEntityStatMapComponentType();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStatIndex(@Nonnull String statName) {
/*  56 */     switch (statName.toLowerCase()) {
/*     */       case "health":
/*  58 */         return DefaultEntityStatTypes.getHealth();
/*     */       case "stamina":
/*  60 */         return DefaultEntityStatTypes.getStamina();
/*     */       case "mana":
/*  62 */         return DefaultEntityStatTypes.getMana();
/*     */       case "ammo":
/*  64 */         return DefaultEntityStatTypes.getAmmo();
/*     */       case "oxygen":
/*  66 */         return DefaultEntityStatTypes.getOxygen();
/*     */       
/*     */       case "staminaregendelay":
/*     */         try {
/*  70 */           int index = EntityStatType.getAssetMap().getIndex("StaminaRegenDelay");
/*  71 */           if (index >= 0) {
/*  72 */             return index;
/*     */           }
/*  74 */         } catch (Exception exception) {}
/*     */ 
/*     */ 
/*     */         
/*  78 */         return -1;
/*     */       
/*     */       case "magiccharges":
/*     */         try {
/*  82 */           int index = EntityStatType.getAssetMap().getIndex("MagicCharges");
/*  83 */           if (index >= 0) {
/*  84 */             return index;
/*     */           }
/*  86 */         } catch (Exception exception) {}
/*     */         
/*  88 */         return -1;
/*     */       
/*     */       case "immunity":
/*     */         try {
/*  92 */           int index = EntityStatType.getAssetMap().getIndex("Immunity");
/*  93 */           if (index >= 0) {
/*  94 */             return index;
/*     */           }
/*  96 */         } catch (Exception exception) {}
/*     */         
/*  98 */         return -1;
/*     */     } 
/* 100 */     return -1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isValidStat(@Nonnull String statName) {
/* 111 */     for (String validStat : VALID_STATS) {
/* 112 */       if (validStat.equalsIgnoreCase(statName)) {
/* 113 */         return true;
/*     */       }
/*     */     } 
/* 116 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean allocateStatPoints(@Nonnull PlayerRef playerRef, @Nonnull String statName, int points, @Nonnull LevelingConfig config) {
/* 130 */     if (points <= 0) {
/* 131 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot allocate non-positive points: " + points);
/* 132 */       return false;
/*     */     } 
/*     */     
/* 135 */     if (!isValidStat(statName)) {
/* 136 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Invalid stat name: " + statName);
/* 137 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 141 */     Ref<EntityStore> entityRef = playerRef.getReference();
/* 142 */     PlayerLevelData data = null;
/*     */     
/* 144 */     if (entityRef != null && entityRef.isValid()) {
/*     */       
/* 146 */       UUID worldUuid = playerRef.getWorldUuid();
/* 147 */       if (worldUuid != null) {
/* 148 */         World world = Universe.get().getWorld(worldUuid);
/* 149 */         if (world != null && world.isAlive()) {
/*     */           
/* 151 */           CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
/*     */           
/* 153 */           world.execute(() -> {
/*     */                 Store<EntityStore> store = entityRef.getStore();
/*     */ 
/*     */                 
/*     */                 PlayerLevelData storeData = (PlayerLevelData)store.getComponent(entityRef, this.playerLevelDataType);
/*     */                 
/*     */                 if (storeData == null) {
/*     */                   Holder<EntityStore> holder1 = playerRef.getHolder();
/*     */                   
/*     */                   if (holder1 != null) {
/*     */                     storeData = (PlayerLevelData)holder1.ensureAndGetComponent(this.playerLevelDataType);
/*     */                   } else {
/*     */                     storeData = new PlayerLevelData();
/*     */                   } 
/*     */                 } 
/*     */                 
/*     */                 if (storeData.getAvailableStatPoints() < points) {
/*     */                   resultFuture.complete(Boolean.valueOf(false));
/*     */                   
/*     */                   return;
/*     */                 } 
/*     */                 
/*     */                 int currentAllocation = storeData.getAllocatedPoints(statName);
/*     */                 
/*     */                 int newAllocation = currentAllocation + points;
/*     */                 
/*     */                 storeData.setAvailableStatPoints(storeData.getAvailableStatPoints() - points);
/*     */                 
/*     */                 storeData.allocatePoints(statName, newAllocation);
/*     */                 
/*     */                 store.putComponent(entityRef, this.playerLevelDataType, (Component)storeData);
/*     */                 
/*     */                 Holder<EntityStore> holder = playerRef.getHolder();
/*     */                 
/*     */                 if (holder != null) {
/*     */                   holder.putComponent(this.playerLevelDataType, storeData.clone());
/*     */                 }
/*     */                 
/*     */                 applyStatModifiers(entityRef, store, storeData, config);
/*     */                 
/*     */                 resultFuture.complete(Boolean.valueOf(true));
/*     */               });
/*     */           
/*     */           try {
/* 197 */             return ((Boolean)resultFuture.get()).booleanValue();
/* 198 */           } catch (Exception e) {
/* 199 */             ((HytaleLogger.Api)LOGGER.atWarning()).log("Error allocating stat points on world thread: " + e.getMessage());
/* 200 */             return false;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 207 */     Holder<EntityStore> holder = playerRef.getHolder();
/* 208 */     if (holder == null) {
/* 209 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot allocate stats: Holder is null for " + playerRef.getUsername());
/* 210 */       return false;
/*     */     } 
/*     */     
/* 213 */     data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
/* 214 */     if (data == null) {
/* 215 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot allocate stats: Failed to get player data for " + playerRef.getUsername());
/* 216 */       return false;
/*     */     } 
/*     */ 
/*     */     
/* 220 */     if (data.getAvailableStatPoints() < points) {
/* 221 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 225 */     int currentAllocation = data.getAllocatedPoints(statName);
/* 226 */     int newAllocation = currentAllocation + points;
/*     */ 
/*     */     
/* 229 */     data.setAvailableStatPoints(data.getAvailableStatPoints() - points);
/* 230 */     data.allocatePoints(statName, newAllocation);
/*     */     
/* 232 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void applyStatModifiers(@Nonnull Ref<EntityStore> entityRef, @Nonnull Store<EntityStore> store, @Nonnull PlayerLevelData data, @Nonnull LevelingConfig config) {
/* 246 */     EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, this.statMapType);
/* 247 */     if (statMap == null) {
/* 248 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot apply stat modifiers: EntityStatMap not found");
/*     */       
/*     */       return;
/*     */     } 
/* 252 */     Map<String, Integer> allocatedStats = data.getAllocatedStats();
/* 253 */     double statValuePerPoint = config.getStatValuePerPoint();
/*     */ 
/*     */     
/* 256 */     for (Map.Entry<String, Integer> entry : allocatedStats.entrySet()) {
/* 257 */       String statName = entry.getKey();
/* 258 */       int allocatedPoints = ((Integer)entry.getValue()).intValue();
/*     */       
/* 260 */       if (allocatedPoints <= 0) {
/*     */         continue;
/*     */       }
/*     */       
/* 264 */       int statIndex = getStatIndex(statName);
/* 265 */       if (statIndex < 0) {
/*     */         continue;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 271 */       float bonusValue = (float)(allocatedPoints * statValuePerPoint);
/*     */ 
/*     */       
/* 274 */       String modifierKey = "RPGLeveling_" + statName + "_Bonus";
/*     */ 
/*     */       
/* 277 */       statMap.removeModifier(statIndex, modifierKey);
/* 278 */       statMap.removeModifier(statIndex, "RPGLeveling_" + statName.toLowerCase() + "_Bonus");
/*     */ 
/*     */       
/* 281 */       EntityStatValue statValue = statMap.get(statIndex);
/* 282 */       float currentMax = (statValue != null) ? statValue.getMax() : 0.0F;
/* 283 */       float currentValue = (statValue != null) ? statValue.get() : 0.0F;
/*     */ 
/*     */       
/* 286 */       StaticModifier modifier = new StaticModifier(Modifier.ModifierTarget.MAX, StaticModifier.CalculationType.ADDITIVE, bonusValue);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 292 */       statMap.putModifier(statIndex, modifierKey, (Modifier)modifier);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void applyStatModifiers(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config) {
/* 305 */     Holder<EntityStore> holder = playerRef.getHolder();
/* 306 */     if (holder == null) {
/* 307 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot apply stat modifiers: Holder is null for " + playerRef.getUsername());
/*     */       
/*     */       return;
/*     */     } 
/* 311 */     PlayerLevelData data = (PlayerLevelData)holder.getComponent(this.playerLevelDataType);
/* 312 */     if (data == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 317 */     Ref<EntityStore> entityRef = playerRef.getReference();
/* 318 */     if (entityRef != null && entityRef.isValid()) {
/* 319 */       UUID worldUuid = playerRef.getWorldUuid();
/* 320 */       if (worldUuid != null) {
/* 321 */         World world = Universe.get().getWorld(worldUuid);
/* 322 */         if (world != null && world.isAlive()) {
/* 323 */           Store<EntityStore> store = entityRef.getStore();
/* 324 */           world.execute(() -> applyStatModifiers(entityRef, store, data, config));
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void recalculateStats(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config) {
/* 339 */     applyStatModifiers(playerRef, config);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ComponentType<EntityStore, PlayerLevelData> getPlayerLevelDataType() {
/* 349 */     return this.playerLevelDataType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ComponentType<EntityStore, EntityStatMap> getEntityStatMapType() {
/* 359 */     return this.statMapType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeAllStatModifiers(@Nonnull Ref<EntityStore> entityRef, @Nonnull Store<EntityStore> store) {
/* 370 */     EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, this.statMapType);
/* 371 */     if (statMap == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 376 */     for (String statName : VALID_STATS) {
/* 377 */       int statIndex = getStatIndex(statName);
/* 378 */       if (statIndex >= 0) {
/* 379 */         String modifierKey = "RPGLeveling_" + statName + "_Bonus";
/* 380 */         statMap.removeModifier(statIndex, modifierKey);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean resetAllocatedStats(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config) {
/* 394 */     Ref<EntityStore> entityRef = playerRef.getReference();
/*     */     
/* 396 */     if (entityRef != null && entityRef.isValid()) {
/*     */       
/* 398 */       UUID worldUuid = playerRef.getWorldUuid();
/* 399 */       if (worldUuid != null) {
/* 400 */         World world = Universe.get().getWorld(worldUuid);
/* 401 */         if (world != null && world.isAlive()) {
/* 402 */           CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
/*     */           
/* 404 */           world.execute(() -> {
/*     */                 Store<EntityStore> store = entityRef.getStore();
/*     */                 
/*     */                 PlayerLevelData data = (PlayerLevelData)store.getComponent(entityRef, this.playerLevelDataType);
/*     */                 
/*     */                 if (data == null) {
/*     */                   Holder<EntityStore> holder1 = playerRef.getHolder();
/*     */                   
/*     */                   if (holder1 != null) {
/*     */                     data = (PlayerLevelData)holder1.ensureAndGetComponent(this.playerLevelDataType);
/*     */                   } else {
/*     */                     data = new PlayerLevelData();
/*     */                   } 
/*     */                 } 
/*     */                 
/*     */                 Map<String, Integer> allocatedStats = data.getAllocatedStats();
/*     */                 
/*     */                 int totalAllocated = 0;
/*     */                 
/*     */                 for (Integer points : allocatedStats.values()) {
/*     */                   totalAllocated += points.intValue();
/*     */                 }
/*     */                 
/*     */                 data.setAvailableStatPoints(data.getAvailableStatPoints() + totalAllocated);
/*     */                 
/*     */                 allocatedStats.clear();
/*     */                 
/*     */                 data.getAllocatedStats().clear();
/*     */                 
/*     */                 EntityStatMap statMap = (EntityStatMap)store.getComponent(entityRef, this.statMapType);
/*     */                 
/*     */                 if (statMap != null) {
/*     */                   for (String statName : VALID_STATS) {
/*     */                     int statIndex = getStatIndex(statName);
/*     */                     
/*     */                     if (statIndex >= 0) {
/*     */                       String modifierKey = "RPGLeveling_" + statName + "_Bonus";
/*     */                       
/*     */                       statMap.removeModifier(statIndex, modifierKey);
/*     */                     } 
/*     */                   } 
/*     */                 }
/*     */                 
/*     */                 store.putComponent(entityRef, this.playerLevelDataType, (Component)data);
/*     */                 
/*     */                 Holder<EntityStore> holder = playerRef.getHolder();
/*     */                 if (holder != null) {
/*     */                   holder.putComponent(this.playerLevelDataType, data.clone());
/*     */                 }
/*     */                 resultFuture.complete(Boolean.valueOf(true));
/*     */               });
/*     */           try {
/* 456 */             return ((Boolean)resultFuture.get()).booleanValue();
/* 457 */           } catch (Exception e) {
/* 458 */             ((HytaleLogger.Api)LOGGER.atWarning()).log("Error resetting stats on world thread: " + e.getMessage());
/* 459 */             return false;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 466 */     Holder<EntityStore> holder = playerRef.getHolder();
/* 467 */     if (holder == null) {
/* 468 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot reset stats: Holder is null for " + playerRef.getUsername());
/* 469 */       return false;
/*     */     } 
/*     */     
/* 472 */     PlayerLevelData data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
/* 473 */     if (data == null) {
/* 474 */       data = new PlayerLevelData();
/*     */     }
/*     */ 
/*     */     
/* 478 */     Map<String, Integer> allocatedStats = data.getAllocatedStats();
/* 479 */     int totalAllocated = 0;
/* 480 */     for (Integer points : allocatedStats.values()) {
/* 481 */       totalAllocated += points.intValue();
/*     */     }
/*     */ 
/*     */     
/* 485 */     data.setAvailableStatPoints(data.getAvailableStatPoints() + totalAllocated);
/*     */ 
/*     */     
/* 488 */     allocatedStats.clear();
/* 489 */     data.getAllocatedStats().clear();
/*     */ 
/*     */     
/* 492 */     holder.putComponent(this.playerLevelDataType, (Component)data);
/*     */     
/* 494 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\services\StatsService.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */