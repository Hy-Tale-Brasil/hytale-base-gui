/*     */ package org.zuxaw.plugin.hud;
/*     */ 
/*     */ import com.hypixel.hytale.component.Ref;
/*     */ import com.hypixel.hytale.component.Store;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
/*     */ import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import javax.annotation.Nonnull;
/*     */ import org.zuxaw.plugin.components.PlayerLevelData;
/*     */ import org.zuxaw.plugin.config.LevelingConfig;
/*     */ import org.zuxaw.plugin.services.LevelingService;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LevelProgressHud
/*     */   extends CustomUIHud
/*     */ {
/*     */   private final LevelingService levelingService;
/*     */   private final LevelingConfig config;
/*  23 */   private int currentLevel = 1;
/*  24 */   private double currentXP = 0.0D;
/*  25 */   private double xpNeeded = 0.0D;
/*  26 */   private float progressValue = 0.0F;
/*     */ 
/*     */   
/*  29 */   private int lastDisplayedLevel = -1;
/*  30 */   private float lastDisplayedProgress = -1.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isBuilt;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LevelProgressHud(@Nonnull PlayerRef playerRef, @Nonnull LevelingService levelingService, @Nonnull LevelingConfig config) {
/*  42 */     super(playerRef);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  47 */     this.isBuilt = false;
/*     */     this.levelingService = levelingService;
/*     */     this.config = config;
/*     */   }
/*     */   protected void build(@Nonnull UICommandBuilder builder) {
/*  52 */     builder.append("HUD/LevelProgress.ui");
/*     */     
/*  54 */     updateData(builder);
/*     */     
/*  56 */     this.lastDisplayedLevel = this.currentLevel;
/*  57 */     this.lastDisplayedProgress = this.progressValue;
/*  58 */     this.isBuilt = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void updateData(@Nonnull UICommandBuilder builder) {
/*  68 */     PlayerLevelData data = null;
/*     */ 
/*     */     
/*  71 */     Ref<EntityStore> entityRef = getPlayerRef().getReference();
/*  72 */     if (entityRef != null && entityRef.isValid()) {
/*  73 */       Store<EntityStore> store = entityRef.getStore();
/*  74 */       data = (PlayerLevelData)store.getComponent(entityRef, this.levelingService.getPlayerLevelDataType());
/*     */     } 
/*     */ 
/*     */     
/*  78 */     if (data == null) {
/*  79 */       data = this.levelingService.getPlayerData(getPlayerRef());
/*     */     }
/*     */     
/*  82 */     if (data == null) {
/*     */       
/*  84 */       this.currentLevel = 1;
/*  85 */       this.currentXP = 0.0D;
/*  86 */       this.xpNeeded = this.levelingService.getXPNeededForNextLevel(1, this.config);
/*  87 */       this.progressValue = 0.0F;
/*     */     } else {
/*  89 */       this.currentLevel = data.getLevel();
/*  90 */       this.currentXP = data.getExperience();
/*     */     } 
/*     */     
/*  93 */     int maxLevel = this.config.getMaxLevel();
/*     */ 
/*     */     
/*  96 */     builder.set("#LevelLabel.Text", String.format("Level %d", new Object[] { Integer.valueOf(this.currentLevel) }));
/*     */ 
/*     */     
/*  99 */     if (this.currentLevel >= maxLevel) {
/*     */       
/* 101 */       this.progressValue = 1.0F;
/*     */     } else {
/*     */       
/* 104 */       this.xpNeeded = this.levelingService.getXPNeededForNextLevel(this.currentLevel, this.config);
/* 105 */       if (this.xpNeeded > 0.0D) {
/* 106 */         this.progressValue = (float)Math.min(1.0D, Math.max(0.0D, this.currentXP / this.xpNeeded));
/*     */       } else {
/*     */         
/* 109 */         this.progressValue = 0.0F;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 115 */     int progressBarWidth = Math.max(0, Math.min(396, (int)(this.progressValue * 396.0F)));
/* 116 */     builder.remove("#ProgressBarFill");
/* 117 */     builder.appendInline("#ProgressBarContainer", 
/* 118 */         String.format("Group #ProgressBarFill { Anchor: (Left: 0, Width: %d, Height: 12); Background: #4a90e2(1.0); }", new Object[] { Integer.valueOf(progressBarWidth) }));
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
/*     */   public void update() {
/*     */     try {
/*     */       float newProgressValue;
/* 136 */       if (!this.isBuilt) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 141 */       PlayerLevelData data = null;
/* 142 */       Ref<EntityStore> entityRef = getPlayerRef().getReference();
/* 143 */       if (entityRef != null && entityRef.isValid()) {
/* 144 */         Store<EntityStore> store = entityRef.getStore();
/* 145 */         data = (PlayerLevelData)store.getComponent(entityRef, this.levelingService.getPlayerLevelDataType());
/*     */       } 
/*     */       
/* 148 */       if (data == null) {
/* 149 */         data = this.levelingService.getPlayerData(getPlayerRef());
/*     */       }
/*     */       
/* 152 */       if (data == null) {
/*     */         return;
/*     */       }
/*     */       
/* 156 */       int newLevel = data.getLevel();
/* 157 */       double newXP = data.getExperience();
/* 158 */       int maxLevel = this.config.getMaxLevel();
/*     */ 
/*     */ 
/*     */       
/* 162 */       if (newLevel >= maxLevel) {
/* 163 */         newProgressValue = 1.0F;
/*     */       } else {
/* 165 */         double newXpNeeded = this.levelingService.getXPNeededForNextLevel(newLevel, this.config);
/* 166 */         if (newXpNeeded > 0.0D) {
/* 167 */           newProgressValue = (float)Math.min(1.0D, Math.max(0.0D, newXP / newXpNeeded));
/*     */         } else {
/* 169 */           newProgressValue = 0.0F;
/*     */         } 
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 175 */       boolean levelChanged = (newLevel != this.lastDisplayedLevel);
/* 176 */       boolean progressChanged = (Math.abs(newProgressValue - this.lastDisplayedProgress) > 0.001F);
/*     */       
/* 178 */       if (!levelChanged && !progressChanged) {
/*     */         return;
/*     */       }
/*     */ 
/*     */       
/* 183 */       this.currentLevel = newLevel;
/* 184 */       this.currentXP = newXP;
/* 185 */       this.progressValue = newProgressValue;
/* 186 */       this.lastDisplayedLevel = newLevel;
/* 187 */       this.lastDisplayedProgress = newProgressValue;
/*     */ 
/*     */ 
/*     */       
/* 191 */       UICommandBuilder builder = new UICommandBuilder();
/* 192 */       updateData(builder);
/* 193 */       update(false, builder);
/* 194 */     } catch (Exception exception) {}
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\hud\LevelProgressHud.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */