/*     */ package org.zuxaw.plugin.services;
/*     */ 
/*     */ import com.hypixel.hytale.component.CommandBuffer;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ import org.zuxaw.plugin.RPGLevelingPlugin;
/*     */ import org.zuxaw.plugin.components.PlayerLevelData;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ import org.zuxaw.plugin.utils.NotificationHelper;
/*     */ 
/*     */ public class LevelingService {
/*  26 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */   
/*     */   private final ComponentType<EntityStore, PlayerLevelData> playerLevelDataType;
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   private StatsService statsService;
/*     */ 
/*     */   
/*     */   public LevelingService(@Nonnull ComponentType<EntityStore, PlayerLevelData> playerLevelDataType) {
/*  37 */     this.playerLevelDataType = playerLevelDataType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatsService(@Nonnull StatsService statsService) {
/*  46 */     this.statsService = statsService;
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
/*     */   public double calculateXPFromMaxHealth(double maxHealth, @Nonnull LevelingConfig config) {
/*  58 */     if (maxHealth <= 0.0D) {
/*  59 */       return 0.0D;
/*     */     }
/*  61 */     double sqrtHealth = Math.sqrt(maxHealth);
/*  62 */     double baseXP = config.getBaseXP();
/*  63 */     double rateExp = config.getRateExp();
/*  64 */     double calculatedXP = sqrtHealth * baseXP * rateExp;
/*  65 */     return calculatedXP;
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
/*     */   public double getXPRequiredForLevel(int level, @Nonnull LevelingConfig config) {
/*  77 */     if (level <= 1) {
/*  78 */       return 0.0D;
/*     */     }
/*     */     
/*  81 */     return config.getLevelBaseXP() * level * level + config.getLevelOffset();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getXPNeededForNextLevel(int currentLevel, @Nonnull LevelingConfig config) {
/*  92 */     if (currentLevel >= config.getMaxLevel()) {
/*  93 */       return 0.0D;
/*     */     }
/*  95 */     double currentLevelXP = getXPRequiredForLevel(currentLevel, config);
/*  96 */     double nextLevelXP = getXPRequiredForLevel(currentLevel + 1, config);
/*  97 */     return nextLevelXP - currentLevelXP;
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
/*     */   public void addExperience(@Nonnull PlayerRef playerRef, double xp, @Nonnull LevelingConfig config, @Nullable CommandBuffer<EntityStore> commandBuffer) {
/* 110 */     if (xp <= 0.0D) {
/* 111 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot add XP: XP is " + xp + " for player " + playerRef.getUsername());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 116 */     Ref<EntityStore> entityRef = playerRef.getReference();
/* 117 */     boolean componentNeedsToBeAdded = false;
/* 118 */     PlayerLevelData data = null;
/*     */ 
/*     */     
/* 121 */     if (entityRef != null && entityRef.isValid()) {
/* 122 */       Store<EntityStore> store = entityRef.getStore();
/* 123 */       data = (PlayerLevelData)store.getComponent(entityRef, this.playerLevelDataType);
/* 124 */       if (data == null) {
/*     */         
/* 126 */         Holder<EntityStore> holder = playerRef.getHolder();
/* 127 */         if (holder != null) {
/* 128 */           data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
/*     */           
/* 130 */           if (commandBuffer != null) {
/* 131 */             PlayerLevelData clonedData = (PlayerLevelData)data.clone();
/* 132 */             commandBuffer.addComponent(entityRef, this.playerLevelDataType, (Component)clonedData);
/*     */           } 
/*     */         } else {
/*     */           
/* 136 */           data = new PlayerLevelData();
/* 137 */           componentNeedsToBeAdded = true;
/*     */         } 
/*     */       } 
/*     */     } else {
/*     */       
/* 142 */       Holder<EntityStore> holder = playerRef.getHolder();
/* 143 */       if (holder != null) {
/* 144 */         data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
/*     */       }
/*     */     } 
/*     */     
/* 148 */     if (data == null) {
/* 149 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot add XP: Unable to get or create player data for " + playerRef.getUsername());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 154 */     if (data.getLevel() >= config.getMaxLevel()) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 159 */     int currentLevel = data.getLevel();
/*     */ 
/*     */     
/* 162 */     data.addExperience(xp);
/*     */ 
/*     */     
/* 165 */     if (componentNeedsToBeAdded && entityRef != null && entityRef.isValid() && commandBuffer != null) {
/* 166 */       commandBuffer.addComponent(entityRef, this.playerLevelDataType, (Component)data);
/*     */     }
/*     */ 
/*     */     
/* 170 */     sendXPGainMessage(playerRef, data, xp, currentLevel, config);
/*     */ 
/*     */     
/* 173 */     checkLevelUp(playerRef, data, config);
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
/*     */ 
/*     */   
/*     */   private void checkLevelUp(@Nonnull PlayerRef playerRef, @Nonnull PlayerLevelData data, @Nonnull LevelingConfig config) {
/* 189 */     int levelsGained = 0;
/*     */     
/* 191 */     while (data.getLevel() < config.getMaxLevel()) {
/* 192 */       double xpNeeded = getXPNeededForNextLevel(data.getLevel(), config);
/*     */       
/* 194 */       if (data.getExperience() >= xpNeeded) {
/*     */         
/* 196 */         data.setExperience(data.getExperience() - xpNeeded);
/* 197 */         data.setLevel(data.getLevel() + 1);
/* 198 */         levelsGained++;
/*     */ 
/*     */         
/* 201 */         int statPointsPerLevel = config.getStatPointsPerLevel();
/* 202 */         data.setAvailableStatPoints(data.getAvailableStatPoints() + statPointsPerLevel);
/*     */ 
/*     */         
/* 205 */         NotificationHelper.showLevelUpTitle(playerRef, data.getLevel());
/*     */ 
/*     */         
/* 208 */         if (statPointsPerLevel > 0) {
/* 209 */           NotificationHelper.sendNotification(playerRef, 
/*     */               
/* 211 */               String.format("<color:aqua>You earned <color:gold><b>%d</b></color> stat point%s! Use <color:yellow>/lvl gui</color> to allocate them.", new Object[] {
/*     */                   
/* 213 */                   Integer.valueOf(statPointsPerLevel), 
/* 214 */                   (statPointsPerLevel > 1) ? "s" : ""
/*     */                 }), NotificationStyle.Default);
/*     */         }
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 221 */         if (data.getLevel() >= config.getMaxLevel()) {
/* 222 */           NotificationHelper.showMaxLevelTitle(playerRef, config.getMaxLevel());
/*     */           break;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void sendXPGainMessage(@Nonnull PlayerRef playerRef, @Nonnull PlayerLevelData data, double xpEarned, int levelBefore, @Nonnull LevelingConfig config) {
/* 244 */     int currentLevel = data.getLevel();
/* 245 */     double currentXP = data.getExperience();
/*     */ 
/*     */     
/* 248 */     if (currentLevel >= config.getMaxLevel()) {
/* 249 */       String maxLevelMessage = String.format("[LEVEL %d] +%.1f XP (MAX LEVEL)", new Object[] {
/*     */             
/* 251 */             Integer.valueOf(currentLevel), Double.valueOf(xpEarned)
/*     */           });
/* 253 */       NotificationHelper.sendNotification(playerRef, maxLevelMessage, NotificationStyle.Default);
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 258 */     double xpNeeded = getXPNeededForNextLevel(currentLevel, config);
/* 259 */     double xpProgress = currentXP / xpNeeded;
/* 260 */     int progressPercent = (int)(xpProgress * 100.0D);
/*     */ 
/*     */     
/* 263 */     int filled = (int)(xpProgress * 20.0D);
/* 264 */     StringBuilder progressBar = new StringBuilder();
/*     */ 
/*     */     
/* 267 */     if (filled > 0) {
/* 268 */       if (filled >= 10) {
/*     */         
/* 270 */         progressBar.append("<gradient:green:lime>");
/* 271 */         for (int i = 0; i < filled; i++) {
/* 272 */           progressBar.append("|");
/*     */         }
/* 274 */         progressBar.append("</gradient>");
/*     */       } else {
/*     */         
/* 277 */         progressBar.append("<color:green>");
/* 278 */         for (int i = 0; i < filled; i++) {
/* 279 */           progressBar.append("|");
/*     */         }
/* 281 */         progressBar.append("</color>");
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 286 */     if (filled < 20) {
/* 287 */       progressBar.append("<color:gray>");
/* 288 */       for (int i = filled; i < 20; i++) {
/* 289 */         progressBar.append("-");
/*     */       }
/* 291 */       progressBar.append("</color>");
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 296 */     String message = String.format("<color:gold><b>[LEVEL %d]</b></color> <color:green>+%.1f XP</color> <color:gray>|</color> %s <color:gray>|</color> <color:yellow>%.1f/%.1f</color> <color:gray>(</color><color:aqua>%d%%</color><color:gray>)</color>", new Object[] {
/*     */           
/* 298 */           Integer.valueOf(currentLevel), 
/* 299 */           Double.valueOf(xpEarned), progressBar
/* 300 */           .toString(), 
/* 301 */           Double.valueOf(currentXP), 
/* 302 */           Double.valueOf(xpNeeded), 
/* 303 */           Integer.valueOf(progressPercent)
/*     */         });
/*     */ 
/*     */     
/* 307 */     NotificationHelper.sendSuccessNotification(playerRef, message);
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
/*     */   
/*     */   @Nonnull
/*     */   public PlayerLevelData getPlayerData(@Nonnull PlayerRef playerRef) {
/* 323 */     Holder<EntityStore> holder = playerRef.getHolder();
/* 324 */     if (holder != null) {
/*     */       
/* 326 */       PlayerLevelData data = (PlayerLevelData)holder.getComponent(this.playerLevelDataType);
/* 327 */       if (data != null) {
/* 328 */         return data;
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 333 */       data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
/* 334 */       if (data != null) {
/* 335 */         return data;
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 341 */     ((HytaleLogger.Api)LOGGER.atWarning()).log("Holder is null for " + playerRef.getUsername() + ", returning default PlayerLevelData. Data may exist in world Store but cannot be accessed from command thread.");
/* 342 */     return new PlayerLevelData();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public ComponentType<EntityStore, PlayerLevelData> getPlayerLevelDataType() {
/* 352 */     return this.playerLevelDataType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateLevelProgressHud(@Nonnull PlayerRef playerRef) {
/*     */     try {
/* 363 */       RPGLevelingPlugin plugin = RPGLevelingPlugin.get();
/* 364 */       if (plugin != null) {
/* 365 */         plugin.updateLevelProgressHud(playerRef);
/*     */       }
/* 367 */     } catch (Exception exception) {}
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
/*     */ 
/*     */   
/*     */   public boolean setPlayerLevel(@Nonnull PlayerRef playerRef, int newLevel, @Nonnull LevelingConfig config) {
/* 383 */     if (newLevel < 1 || newLevel > config.getMaxLevel()) {
/* 384 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Invalid level " + newLevel + " for player " + playerRef.getUsername() + " (must be between 1 and " + config
/* 385 */           .getMaxLevel() + ")");
/* 386 */       return false;
/*     */     } 
/*     */     
/* 389 */     Ref<EntityStore> entityRef = playerRef.getReference();
/*     */     
/* 391 */     if (entityRef != null && entityRef.isValid()) {
/*     */       
/* 393 */       UUID worldUuid = playerRef.getWorldUuid();
/* 394 */       if (worldUuid != null) {
/* 395 */         World world = Universe.get().getWorld(worldUuid);
/* 396 */         if (world != null && world.isAlive()) {
/* 397 */           CompletableFuture<Boolean> resultFuture = new CompletableFuture<>();
/*     */           
/* 399 */           world.execute(() -> {
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
/*     */                 data.setLevel(newLevel);
/*     */                 
/*     */                 data.setExperience(0.0D);
/*     */                 
/*     */                 Map<String, Integer> allocatedStats = data.getAllocatedStats();
/*     */                 
/*     */                 int totalAllocated = 0;
/*     */                 
/*     */                 for (Integer points : allocatedStats.values()) {
/*     */                   totalAllocated += points.intValue();
/*     */                 }
/*     */                 
/*     */                 allocatedStats.clear();
/*     */                 
/*     */                 data.getAllocatedStats().clear();
/*     */                 
/*     */                 if (this.statsService != null) {
/*     */                   this.statsService.removeAllStatModifiers(entityRef, store);
/*     */                 }
/*     */                 
/*     */                 int newAvailablePoints = (newLevel - 1) * config.getStatPointsPerLevel() + totalAllocated;
/*     */                 
/*     */                 data.setAvailableStatPoints(newAvailablePoints);
/*     */                 
/*     */                 store.putComponent(entityRef, this.playerLevelDataType, (Component)data);
/*     */                 
/*     */                 Holder<EntityStore> holder = playerRef.getHolder();
/*     */                 
/*     */                 if (holder != null) {
/*     */                   holder.putComponent(this.playerLevelDataType, data.clone());
/*     */                 }
/*     */                 
/*     */                 updateLevelProgressHud(playerRef);
/*     */                 
/*     */                 resultFuture.complete(Boolean.valueOf(true));
/*     */               });
/*     */           try {
/* 451 */             return ((Boolean)resultFuture.get()).booleanValue();
/* 452 */           } catch (Exception e) {
/* 453 */             ((HytaleLogger.Api)LOGGER.atWarning()).log("Error setting player level on world thread: " + e.getMessage());
/* 454 */             return false;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 461 */     Holder<EntityStore> holder = playerRef.getHolder();
/* 462 */     if (holder == null) {
/* 463 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot set level: Holder is null for " + playerRef.getUsername());
/* 464 */       return false;
/*     */     } 
/*     */     
/* 467 */     PlayerLevelData data = (PlayerLevelData)holder.ensureAndGetComponent(this.playerLevelDataType);
/* 468 */     if (data == null) {
/* 469 */       data = new PlayerLevelData();
/*     */     }
/*     */ 
/*     */     
/* 473 */     data.setLevel(newLevel);
/* 474 */     data.setExperience(0.0D);
/*     */ 
/*     */     
/* 477 */     Map<String, Integer> allocatedStats = data.getAllocatedStats();
/* 478 */     int totalAllocated = 0;
/* 479 */     for (Integer points : allocatedStats.values()) {
/* 480 */       totalAllocated += points.intValue();
/*     */     }
/* 482 */     allocatedStats.clear();
/* 483 */     data.getAllocatedStats().clear();
/*     */ 
/*     */ 
/*     */     
/* 487 */     int newAvailablePoints = (newLevel - 1) * config.getStatPointsPerLevel() + totalAllocated;
/* 488 */     data.setAvailableStatPoints(newAvailablePoints);
/*     */ 
/*     */     
/* 491 */     holder.putComponent(this.playerLevelDataType, (Component)data);
/*     */ 
/*     */     
/* 494 */     updateLevelProgressHud(playerRef);
/*     */     
/* 496 */     return true;
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\services\LevelingService.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */