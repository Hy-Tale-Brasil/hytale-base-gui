/*     */ package org.zuxaw.plugin;
/*     */ 
/*     */ import com.hypixel.hytale.component.ComponentType;
/*     */ import com.hypixel.hytale.component.Holder;
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.component.system.ISystem;
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.command.system.AbstractCommand;
/*     */ import com.hypixel.hytale.server.core.entity.UUIDComponent;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
/*     */ import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
/*     */ import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPlugin;
/*     */ import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import com.hypixel.hytale.server.core.util.Config;
/*     */ import java.io.File;
/*     */ import java.io.FileReader;
/*     */ import java.io.FileWriter;
/*     */ import java.io.IOException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.UUID;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.zuxaw.plugin.commands.RPGLevelingCommand;
/*     */ import org.zuxaw.plugin.components.PlayerLevelData;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ import org.zuxaw.plugin.hud.HudManagerService;
/*     */ import org.zuxaw.plugin.hud.LevelProgressHud;
/*     */ import org.zuxaw.plugin.services.LevelingService;
/*     */ import org.zuxaw.plugin.services.StatsService;
/*     */ import org.zuxaw.plugin.systems.DamageTrackingSystem;
/*     */ import org.zuxaw.plugin.systems.DeathDetectionSystem;
/*     */ import org.zuxaw.plugin.systems.LevelProgressHudSystem;
/*     */ import org.zuxaw.plugin.systems.StatsApplicationSystem;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RPGLevelingPlugin
/*     */   extends JavaPlugin
/*     */ {
/*     */   private static RPGLevelingPlugin instance;
/*  55 */   private final Config<LevelingConfig> config = withConfig("RPGLevelingConfig", LevelingConfig.CODEC);
/*     */ 
/*     */   
/*  58 */   private final Map<UUID, UUID> lastAttackers = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*  61 */   private final Map<UUID, String> entityNames = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*  64 */   private final Map<UUID, LevelProgressHud> levelHuds = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*     */   private ComponentType<EntityStore, PlayerLevelData> playerLevelDataType;
/*     */ 
/*     */   
/*     */   private LevelingService levelingService;
/*     */ 
/*     */   
/*     */   private StatsService statsService;
/*     */ 
/*     */   
/*     */   private HudManagerService hudManagerService;
/*     */   
/*  78 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */   
/*     */   public RPGLevelingPlugin(@Nonnull JavaPluginInit init) {
/*  81 */     super(init);
/*  82 */     instance = this;
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/*  87 */       LevelingConfig levelingConfig = (LevelingConfig)this.config.get();
/*  88 */     } catch (Exception e) {
/*  89 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Failed to access config in constructor: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void setup() {
/*  96 */     LevelingConfig levelingConfig = (LevelingConfig)this.config.get();
/*     */ 
/*     */     
/*  99 */     String workingDir = System.getProperty("user.dir");
/* 100 */     if (workingDir != null && !workingDir.isEmpty()) {
/* 101 */       Path configPath = Paths.get(workingDir, new String[] { "mods", "RPGLeveling", "RPGLevelingConfig.json" });
/* 102 */       File configFile = configPath.toFile();
/* 103 */       if (configFile.exists()) {
/*     */         try {
/* 105 */           Map<String, Object> fileConfig = readConfigFile(configFile);
/* 106 */           Object fileRateExp = fileConfig.get("RateExp");
/* 107 */           Object fileBaseXP = fileConfig.get("BaseXP");
/*     */ 
/*     */ 
/*     */           
/* 111 */           boolean configUpdated = false;
/*     */           
/* 113 */           if (fileRateExp != null) {
/* 114 */             double fileRateExpValue = (fileRateExp instanceof Number) ? ((Number)fileRateExp).doubleValue() : 0.0D;
/* 115 */             if (Math.abs(levelingConfig.getRateExp() - fileRateExpValue) > 0.01D) {
/* 116 */               ((HytaleLogger.Api)LOGGER.atSevere()).log("ERROR: RateExp mismatch! File has " + fileRateExpValue + " but loaded config has " + levelingConfig.getRateExp() + ". Applying file value as workaround.");
/* 117 */               levelingConfig.setRateExp(fileRateExpValue);
/* 118 */               configUpdated = true;
/*     */             } 
/*     */           } 
/* 121 */           if (fileBaseXP != null) {
/* 122 */             double fileBaseXPValue = (fileBaseXP instanceof Number) ? ((Number)fileBaseXP).doubleValue() : 0.0D;
/* 123 */             if (Math.abs(levelingConfig.getBaseXP() - fileBaseXPValue) > 0.01D) {
/* 124 */               ((HytaleLogger.Api)LOGGER.atSevere()).log("ERROR: BaseXP mismatch! File has " + fileBaseXPValue + " but loaded config has " + levelingConfig.getBaseXP() + ". Applying file value as workaround.");
/* 125 */               levelingConfig.setBaseXP(fileBaseXPValue);
/* 126 */               configUpdated = true;
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/* 131 */           Object fileMaxLevel = fileConfig.get("MaxLevel");
/* 132 */           if (fileMaxLevel != null && fileMaxLevel instanceof Number) {
/* 133 */             int fileMaxLevelValue = ((Number)fileMaxLevel).intValue();
/* 134 */             if (levelingConfig.getMaxLevel() != fileMaxLevelValue) {
/* 135 */               levelingConfig.setMaxLevel(fileMaxLevelValue);
/* 136 */               configUpdated = true;
/*     */             } 
/*     */           } 
/*     */           
/* 140 */           Object fileLevelBaseXP = fileConfig.get("LevelBaseXP");
/* 141 */           if (fileLevelBaseXP != null && fileLevelBaseXP instanceof Number) {
/* 142 */             double fileLevelBaseXPValue = ((Number)fileLevelBaseXP).doubleValue();
/* 143 */             if (Math.abs(levelingConfig.getLevelBaseXP() - fileLevelBaseXPValue) > 0.01D) {
/* 144 */               levelingConfig.setLevelBaseXP(fileLevelBaseXPValue);
/* 145 */               configUpdated = true;
/*     */             } 
/*     */           } 
/*     */           
/* 149 */           Object fileLevelOffset = fileConfig.get("LevelOffset");
/* 150 */           if (fileLevelOffset != null && fileLevelOffset instanceof Number) {
/* 151 */             double fileLevelOffsetValue = ((Number)fileLevelOffset).doubleValue();
/* 152 */             if (Math.abs(levelingConfig.getLevelOffset() - fileLevelOffsetValue) > 0.01D) {
/* 153 */               levelingConfig.setLevelOffset(fileLevelOffsetValue);
/* 154 */               configUpdated = true;
/*     */             } 
/*     */           } 
/*     */           
/* 158 */           Object fileStatPointsPerLevel = fileConfig.get("StatPointsPerLevel");
/* 159 */           if (fileStatPointsPerLevel != null && fileStatPointsPerLevel instanceof Number) {
/* 160 */             int fileStatPointsPerLevelValue = ((Number)fileStatPointsPerLevel).intValue();
/* 161 */             if (levelingConfig.getStatPointsPerLevel() != fileStatPointsPerLevelValue) {
/* 162 */               levelingConfig.setStatPointsPerLevel(fileStatPointsPerLevelValue);
/* 163 */               configUpdated = true;
/*     */             } 
/*     */           } 
/*     */           
/* 167 */           Object fileStatValuePerPoint = fileConfig.get("StatValuePerPoint");
/* 168 */           if (fileStatValuePerPoint != null && fileStatValuePerPoint instanceof Number) {
/* 169 */             double fileStatValuePerPointValue = ((Number)fileStatValuePerPoint).doubleValue();
/* 170 */             if (Math.abs(levelingConfig.getStatValuePerPoint() - fileStatValuePerPointValue) > 0.01D) {
/* 171 */               levelingConfig.setStatValuePerPoint(fileStatValuePerPointValue);
/* 172 */               configUpdated = true;
/*     */             } 
/*     */           } 
/*     */           
/* 176 */           Object fileEnableHUD = fileConfig.get("EnableHUD");
/* 177 */           if (fileEnableHUD != null && fileEnableHUD instanceof Boolean) {
/* 178 */             boolean fileEnableHUDValue = ((Boolean)fileEnableHUD).booleanValue();
/* 179 */             if (levelingConfig.isEnableHUD() != fileEnableHUDValue) {
/* 180 */               levelingConfig.setEnableHUD(fileEnableHUDValue);
/* 181 */               configUpdated = true;
/*     */             }
/*     */           
/*     */           }
/*     */         
/* 186 */         } catch (IOException e) {
/* 187 */           ((HytaleLogger.Api)LOGGER.atWarning()).log("Failed to read config file for verification: " + e.getMessage());
/*     */         } 
/*     */       } else {
/* 190 */         ((HytaleLogger.Api)LOGGER.atWarning()).log("Config file not found at: " + configFile.getAbsolutePath() + " - using default values");
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 195 */     createConfigFileIfNeeded(levelingConfig);
/*     */ 
/*     */     
/* 198 */     this.playerLevelDataType = getEntityStoreRegistry().registerComponent(PlayerLevelData.class, "PlayerLevelData", PlayerLevelData.CODEC);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 205 */     this.levelingService = new LevelingService(this.playerLevelDataType);
/* 206 */     this.statsService = new StatsService(this.playerLevelDataType);
/* 207 */     this.levelingService.setStatsService(this.statsService);
/* 208 */     this.hudManagerService = new HudManagerService();
/*     */ 
/*     */     
/* 211 */     getCommandRegistry().registerCommand((AbstractCommand)new RPGLevelingCommand(this.levelingService, this.statsService, levelingConfig));
/*     */ 
/*     */ 
/*     */     
/* 215 */     getEntityStoreRegistry().registerSystem((ISystem)new DamageTrackingSystem(this.lastAttackers, this.entityNames));
/*     */     
/* 217 */     getEntityStoreRegistry().registerSystem((ISystem)new DeathDetectionSystem(this.lastAttackers, this.entityNames, this.levelingService, this.config));
/*     */     
/* 219 */     getEntityStoreRegistry().registerSystem((ISystem)new StatsApplicationSystem(this.statsService, this.config));
/*     */ 
/*     */     
/* 222 */     if (levelingConfig.isEnableHUD()) {
/* 223 */       getEntityStoreRegistry().registerSystem((ISystem)new LevelProgressHudSystem());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 229 */     if (levelingConfig.isEnableHUD()) {
/* 230 */       getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, event -> {
/*     */             Holder<EntityStore> holder = event.getHolder();
/*     */ 
/*     */             
/*     */             if (holder == null) {
/*     */               ((HytaleLogger.Api)LOGGER.atWarning()).log("AddPlayerToWorldEvent: Holder is null");
/*     */ 
/*     */               
/*     */               return;
/*     */             } 
/*     */ 
/*     */             
/*     */             World world = event.getWorld();
/*     */ 
/*     */             
/*     */             if (world == null || !world.isAlive()) {
/*     */               ((HytaleLogger.Api)LOGGER.atWarning()).log("AddPlayerToWorldEvent: World is not available");
/*     */ 
/*     */               
/*     */               return;
/*     */             } 
/*     */ 
/*     */             
/*     */             UUIDComponent uuidComponent = (UUIDComponent)holder.getComponent(UUIDComponent.getComponentType());
/*     */ 
/*     */             
/*     */             if (uuidComponent == null) {
/*     */               ((HytaleLogger.Api)LOGGER.atWarning()).log("AddPlayerToWorldEvent: UUIDComponent not found");
/*     */               
/*     */               return;
/*     */             } 
/*     */             
/*     */             UUID playerUuid = uuidComponent.getUuid();
/*     */             
/*     */             PlayerRef playerRef = Universe.get().getPlayer(playerUuid);
/*     */             
/*     */             if (playerRef == null) {
/*     */               ((HytaleLogger.Api)LOGGER.atWarning()).log("AddPlayerToWorldEvent: PlayerRef not found for UUID " + String.valueOf(playerUuid));
/*     */               
/*     */               return;
/*     */             } 
/*     */             
/*     */             CompletableFuture.runAsync(());
/*     */           });
/*     */     }
/*     */     
/* 276 */     getEventRegistry().register(PlayerDisconnectEvent.class, event -> {
/*     */           PlayerRef playerRef = event.getPlayerRef();
/*     */           this.levelHuds.remove(playerRef.getUuid());
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void start() {
/* 286 */     LevelingConfig levelingConfig = (LevelingConfig)this.config.get();
/* 287 */     ((HytaleLogger.Api)LOGGER.atInfo()).log("Config accessed in start() - MaxLevel: " + levelingConfig.getMaxLevel());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static RPGLevelingPlugin get() {
/* 296 */     return instance;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ComponentType<EntityStore, PlayerLevelData> getPlayerLevelDataType() {
/* 305 */     return this.playerLevelDataType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LevelingService getLevelingService() {
/* 314 */     return this.levelingService;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Config<LevelingConfig> getConfig() {
/* 323 */     return this.config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StatsService getStatsService() {
/* 332 */     return this.statsService;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HudManagerService getHudManagerService() {
/* 341 */     return this.hudManagerService;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateLevelProgressHud(@Nonnull PlayerRef playerRef) {
/* 351 */     LevelProgressHud hud = this.levelHuds.get(playerRef.getUuid());
/* 352 */     if (hud == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 357 */     Ref<EntityStore> entityRef = playerRef.getReference();
/* 358 */     if (entityRef != null && entityRef.isValid()) {
/* 359 */       UUID worldUuid = playerRef.getWorldUuid();
/* 360 */       if (worldUuid != null) {
/*     */         
/* 362 */         World world = Universe.get().getWorld(worldUuid);
/* 363 */         if (world != null && world.isAlive()) {
/*     */           
/* 365 */           world.execute(() -> {
/*     */                 try {
/*     */                   hud.update();
/* 368 */                 } catch (Exception e) {
/*     */                   ((HytaleLogger.Api)LOGGER.atSevere()).log("Error updating HUD: " + e.getMessage());
/*     */                 } 
/*     */               });
/*     */           
/*     */           return;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*     */     try {
/* 379 */       hud.update();
/* 380 */     } catch (Exception e) {
/* 381 */       ((HytaleLogger.Api)LOGGER.atSevere()).log("Error updating HUD (not on world thread): " + e.getMessage());
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
/*     */   private void createConfigFileIfNeeded(@Nonnull LevelingConfig defaultConfig) {
/*     */     try {
/* 396 */       String workingDir = System.getProperty("user.dir");
/* 397 */       if (workingDir == null || workingDir.isEmpty()) {
/* 398 */         ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot determine working directory, cannot create config file");
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 403 */       Path pluginsDir = Paths.get(workingDir, new String[] { "mods", "RPGLeveling" });
/* 404 */       File pluginDataDir = pluginsDir.toFile();
/*     */ 
/*     */       
/* 407 */       if (!pluginDataDir.exists()) {
/* 408 */         pluginDataDir.mkdirs();
/*     */       }
/*     */ 
/*     */       
/* 412 */       File configFile = new File(pluginDataDir, "RPGLevelingConfig.json");
/*     */       
/* 414 */       if (!configFile.exists())
/*     */       
/* 416 */       { String jsonContent = formatConfigJson(defaultConfig);
/*     */ 
/*     */         
/* 419 */         FileWriter writer = new FileWriter(configFile); 
/* 420 */         try { writer.write(jsonContent);
/* 421 */           writer.close(); } catch (Throwable throwable) { try { writer.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */            throw throwable; }
/*     */          }
/* 424 */       else { mergeConfigFile(configFile, defaultConfig); }
/*     */     
/* 426 */     } catch (IOException e) {
/* 427 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Failed to create/update config file: " + e.getMessage());
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
/*     */   private void mergeConfigFile(@Nonnull File configFile, @Nonnull LevelingConfig defaultConfig) {
/*     */     try {
/* 440 */       Map<String, Object> existingConfig = readConfigFile(configFile);
/*     */ 
/*     */       
/* 443 */       Map<String, Object> defaultValues = new HashMap<>();
/* 444 */       defaultValues.put("MaxLevel", Integer.valueOf(defaultConfig.getMaxLevel()));
/* 445 */       defaultValues.put("RateExp", Double.valueOf(defaultConfig.getRateExp()));
/* 446 */       defaultValues.put("BaseXP", Double.valueOf(defaultConfig.getBaseXP()));
/* 447 */       defaultValues.put("LevelBaseXP", Double.valueOf(defaultConfig.getLevelBaseXP()));
/* 448 */       defaultValues.put("LevelOffset", Double.valueOf(defaultConfig.getLevelOffset()));
/* 449 */       defaultValues.put("StatPointsPerLevel", Integer.valueOf(defaultConfig.getStatPointsPerLevel()));
/* 450 */       defaultValues.put("StatValuePerPoint", Double.valueOf(defaultConfig.getStatValuePerPoint()));
/* 451 */       defaultValues.put("EnableHUD", Boolean.valueOf(defaultConfig.isEnableHUD()));
/*     */ 
/*     */       
/* 454 */       boolean hasChanges = false;
/* 455 */       for (Map.Entry<String, Object> entry : defaultValues.entrySet()) {
/* 456 */         String key = entry.getKey();
/* 457 */         if (!existingConfig.containsKey(key)) {
/* 458 */           existingConfig.put(key, entry.getValue());
/* 459 */           hasChanges = true;
/*     */         } 
/*     */       } 
/*     */ 
/*     */       
/* 464 */       if (hasChanges)
/* 465 */       { String mergedJson = formatConfigJsonFromMap(existingConfig);
/* 466 */         FileWriter writer = new FileWriter(configFile); 
/* 467 */         try { writer.write(mergedJson);
/* 468 */           ((HytaleLogger.Api)LOGGER.atInfo()).log("Updated config file with new options: " + configFile.getAbsolutePath());
/* 469 */           writer.close(); } catch (Throwable throwable) { try { writer.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }  throw throwable; }
/*     */          }
/* 471 */       else { ((HytaleLogger.Api)LOGGER.atInfo()).log("Config file is up to date, no changes needed"); }
/*     */     
/* 473 */     } catch (IOException e) {
/* 474 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Failed to merge config file: " + e.getMessage());
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Map<String, Object> readConfigFile(@Nonnull File configFile) throws IOException {
/* 485 */     Map<String, Object> config = new HashMap<>();
/*     */     
/* 487 */     FileReader reader = new FileReader(configFile); 
/* 488 */     try { StringBuilder content = new StringBuilder();
/*     */       int ch;
/* 490 */       while ((ch = reader.read()) != -1) {
/* 491 */         content.append((char)ch);
/*     */       }
/*     */       
/* 494 */       String json = content.toString().trim();
/*     */ 
/*     */       
/* 497 */       if (json.startsWith("{") && json.endsWith("}")) {
/* 498 */         json = json.substring(1, json.length() - 1).trim();
/*     */       }
/*     */       
/* 501 */       if (!json.isEmpty()) {
/*     */         
/* 503 */         int depth = 0;
/* 504 */         boolean inString = false;
/* 505 */         boolean escapeNext = false;
/* 506 */         StringBuilder currentPair = new StringBuilder();
/*     */         
/* 508 */         for (int i = 0; i < json.length(); i++) {
/* 509 */           char c = json.charAt(i);
/*     */           
/* 511 */           if (escapeNext) {
/* 512 */             currentPair.append(c);
/* 513 */             escapeNext = false;
/*     */ 
/*     */           
/*     */           }
/* 517 */           else if (c == '\\') {
/* 518 */             escapeNext = true;
/* 519 */             currentPair.append(c);
/*     */ 
/*     */           
/*     */           }
/* 523 */           else if (c == '"') {
/* 524 */             inString = !inString;
/* 525 */             currentPair.append(c);
/*     */ 
/*     */           
/*     */           }
/* 529 */           else if (c == '{' || c == '[') {
/* 530 */             depth++;
/* 531 */             currentPair.append(c);
/*     */ 
/*     */           
/*     */           }
/* 535 */           else if (c == '}' || c == ']') {
/* 536 */             depth--;
/* 537 */             currentPair.append(c);
/*     */ 
/*     */           
/*     */           }
/* 541 */           else if (c == ',' && depth == 0 && !inString) {
/*     */             
/* 543 */             parseKeyValuePair(currentPair.toString(), config);
/* 544 */             currentPair.setLength(0);
/*     */           }
/*     */           else {
/*     */             
/* 548 */             currentPair.append(c);
/*     */           } 
/*     */         } 
/*     */         
/* 552 */         if (currentPair.length() > 0) {
/* 553 */           parseKeyValuePair(currentPair.toString(), config);
/*     */         }
/*     */       } 
/* 556 */       reader.close(); } catch (Throwable throwable) { try { reader.close(); } catch (Throwable throwable1) { throwable.addSuppressed(throwable1); }
/*     */        throw throwable; }
/* 558 */      return config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parseKeyValuePair(@Nonnull String pair, @Nonnull Map<String, Object> config) {
/*     */     Object value;
/* 568 */     pair = pair.trim();
/* 569 */     if (pair.isEmpty()) {
/*     */       return;
/*     */     }
/*     */     
/* 573 */     int colonIndex = pair.indexOf(':');
/* 574 */     if (colonIndex == -1) {
/*     */       return;
/*     */     }
/*     */     
/* 578 */     String key = pair.substring(0, colonIndex).trim();
/* 579 */     String valueStr = pair.substring(colonIndex + 1).trim();
/*     */ 
/*     */     
/* 582 */     if (key.startsWith("\"") && key.endsWith("\"")) {
/* 583 */       key = key.substring(1, key.length() - 1);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 588 */     if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
/*     */       
/* 590 */       value = valueStr.substring(1, valueStr.length() - 1);
/* 591 */     } else if (valueStr.contains(".")) {
/*     */       
/*     */       try {
/* 594 */         value = Double.valueOf(Double.parseDouble(valueStr));
/* 595 */       } catch (NumberFormatException e) {
/* 596 */         value = valueStr;
/*     */       } 
/*     */     } else {
/*     */       
/*     */       try {
/* 601 */         value = Integer.valueOf(Integer.parseInt(valueStr));
/* 602 */       } catch (NumberFormatException e) {
/* 603 */         if (valueStr.equalsIgnoreCase("true")) {
/* 604 */           value = Boolean.valueOf(true);
/* 605 */         } else if (valueStr.equalsIgnoreCase("false")) {
/* 606 */           value = Boolean.valueOf(false);
/*     */         } else {
/* 608 */           value = valueStr;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 613 */     config.put(key, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String formatConfigJson(@Nonnull LevelingConfig config) {
/* 623 */     return String.format("{\n  \"MaxLevel\": %d,\n  \"RateExp\": %.1f,\n  \"BaseXP\": %.1f,\n  \"LevelBaseXP\": %.1f,\n  \"LevelOffset\": %.1f,\n  \"StatPointsPerLevel\": %d,\n  \"StatValuePerPoint\": %.1f,\n  \"EnableHUD\": %s\n}", new Object[] {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 634 */           Integer.valueOf(config.getMaxLevel()), 
/* 635 */           Double.valueOf(config.getRateExp()), 
/* 636 */           Double.valueOf(config.getBaseXP()), 
/* 637 */           Double.valueOf(config.getLevelBaseXP()), 
/* 638 */           Double.valueOf(config.getLevelOffset()), 
/* 639 */           Integer.valueOf(config.getStatPointsPerLevel()), 
/* 640 */           Double.valueOf(config.getStatValuePerPoint()), 
/* 641 */           Boolean.valueOf(config.isEnableHUD())
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String formatConfigJsonFromMap(@Nonnull Map<String, Object> configMap) {
/* 652 */     StringBuilder json = new StringBuilder("{\n");
/* 653 */     boolean first = true;
/*     */ 
/*     */     
/* 656 */     String[] keys = { "MaxLevel", "RateExp", "BaseXP", "LevelBaseXP", "LevelOffset", "StatPointsPerLevel", "StatValuePerPoint", "EnableHUD" };
/*     */ 
/*     */     
/* 659 */     for (String key : keys) {
/* 660 */       if (configMap.containsKey(key)) {
/* 661 */         if (!first) {
/* 662 */           json.append(",\n");
/*     */         }
/* 664 */         first = false;
/*     */         
/* 666 */         Object value = configMap.get(key);
/* 667 */         if (value instanceof Integer) {
/* 668 */           json.append("  \"").append(key).append("\": ").append(value);
/* 669 */         } else if (value instanceof Double) {
/* 670 */           json.append("  \"").append(key).append("\": ").append(String.format("%.1f", new Object[] { value }));
/* 671 */         } else if (value instanceof Boolean) {
/* 672 */           json.append("  \"").append(key).append("\": ").append(value);
/*     */         } else {
/* 674 */           json.append("  \"").append(key).append("\": \"").append(value).append("\"");
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 679 */     json.append("\n}");
/* 680 */     return json.toString();
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
/*     */   private void showLevelProgressHudWithRetry(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config, @Nonnull World world, int retryCount) {
/* 693 */     if (retryCount >= 5) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/* 698 */     Ref<EntityStore> entityRef = playerRef.getReference();
/* 699 */     if (entityRef == null || !entityRef.isValid()) {
/*     */       
/* 701 */       CompletableFuture.runAsync(() -> {
/*     */             try {
/*     */               Thread.sleep(200L);
/*     */ 
/*     */               
/*     */               world.execute(());
/* 707 */             } catch (InterruptedException e) {
/*     */               Thread.currentThread().interrupt();
/*     */             } 
/*     */           });
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*     */     try {
/* 716 */       Store<EntityStore> store = entityRef.getStore();
/* 717 */       Player playerComponent = (Player)store.getComponent(entityRef, Player.getComponentType());
/* 718 */       if (playerComponent == null) {
/* 719 */         ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot show HUD: Player component not found for " + playerRef.getUsername());
/*     */         
/*     */         return;
/*     */       } 
/*     */       
/* 724 */       LevelProgressHud levelHud = new LevelProgressHud(playerRef, this.levelingService, config);
/* 725 */       this.hudManagerService.setCustomHud(playerComponent, playerRef, "LevelProgress", (CustomUIHud)levelHud);
/*     */ 
/*     */       
/* 728 */       this.levelHuds.put(playerRef.getUuid(), levelHud);
/*     */ 
/*     */ 
/*     */       
/* 732 */       levelHud.update();
/* 733 */     } catch (Exception e) {
/* 734 */       ((HytaleLogger.Api)LOGGER.atSevere()).log("Error showing level progress HUD: " + e.getMessage());
/* 735 */       e.printStackTrace();
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
/*     */   private void showLevelProgressHud(@Nonnull PlayerRef playerRef, @Nonnull LevelingConfig config) {
/* 747 */     Ref<EntityStore> entityRef = playerRef.getReference();
/* 748 */     if (entityRef == null || !entityRef.isValid()) {
/* 749 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot show HUD: Entity reference is invalid for player " + playerRef.getUsername());
/*     */       
/*     */       return;
/*     */     } 
/* 753 */     Store<EntityStore> store = entityRef.getStore();
/* 754 */     World world = ((EntityStore)store.getExternalData()).getWorld();
/* 755 */     if (world == null || !world.isAlive()) {
/* 756 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot show HUD: World is not available for player " + playerRef.getUsername());
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 761 */     world.execute(() -> {
/*     */           try {
/*     */             Player playerComponent = (Player)store.getComponent(entityRef, Player.getComponentType());
/*     */ 
/*     */             
/*     */             if (playerComponent == null) {
/*     */               ((HytaleLogger.Api)LOGGER.atWarning()).log("Cannot show HUD: Player component not found for " + playerRef.getUsername());
/*     */               
/*     */               return;
/*     */             } 
/*     */             
/*     */             LevelProgressHud levelHud = new LevelProgressHud(playerRef, this.levelingService, config);
/*     */             
/*     */             this.hudManagerService.setCustomHud(playerComponent, playerRef, "LevelProgress", (CustomUIHud)levelHud);
/*     */             
/*     */             this.levelHuds.put(playerRef.getUuid(), levelHud);
/* 777 */           } catch (Exception e) {
/*     */             ((HytaleLogger.Api)LOGGER.atSevere()).log("Error showing level progress HUD: " + e.getMessage());
/*     */             e.printStackTrace();
/*     */           } 
/*     */         });
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\RPGLevelingPlugin.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */