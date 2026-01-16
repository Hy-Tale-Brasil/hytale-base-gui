/*     */ package org.zuxaw.plugin.config;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.ExtraInfo;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import java.util.function.Supplier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LevelingConfig
/*     */ {
/*     */   public static final BuilderCodec<LevelingConfig> CODEC;
/*     */   
/*     */   static {
/*  51 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LevelingConfig.class, LevelingConfig::new).append(new KeyedCodec("MaxLevel", (Codec)Codec.INTEGER), (config, value, extraInfo) -> config.maxLevel = value.intValue(), (config, extraInfo) -> Integer.valueOf(config.maxLevel)).add()).append(new KeyedCodec("RateExp", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> config.rateExp = value.doubleValue(), (config, extraInfo) -> Double.valueOf(config.rateExp)).add()).append(new KeyedCodec("BaseXP", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> config.baseXP = value.doubleValue(), (config, extraInfo) -> Double.valueOf(config.baseXP)).add()).append(new KeyedCodec("LevelBaseXP", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> config.levelBaseXP = value.doubleValue(), (config, extraInfo) -> Double.valueOf(config.levelBaseXP)).add()).append(new KeyedCodec("LevelOffset", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> config.levelOffset = value.doubleValue(), (config, extraInfo) -> Double.valueOf(config.levelOffset)).add()).append(new KeyedCodec("StatPointsPerLevel", (Codec)Codec.INTEGER), (config, value, extraInfo) -> config.statPointsPerLevel = value.intValue(), (config, extraInfo) -> Integer.valueOf(config.statPointsPerLevel)).add()).append(new KeyedCodec("StatValuePerPoint", (Codec)Codec.DOUBLE), (config, value, extraInfo) -> config.statValuePerPoint = value.doubleValue(), (config, extraInfo) -> Double.valueOf(config.statValuePerPoint)).add()).append(new KeyedCodec("EnableHUD", (Codec)Codec.BOOLEAN), (config, value, extraInfo) -> config.enableHUD = value.booleanValue(), (config, extraInfo) -> Boolean.valueOf(config.enableHUD)).add()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  58 */   private int maxLevel = 100;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  65 */   private double rateExp = 1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  73 */   private double baseXP = 10.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  81 */   private double levelBaseXP = 50.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   private double levelOffset = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  96 */   private int statPointsPerLevel = 5;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 103 */   private double statValuePerPoint = 1.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean enableHUD = true;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMaxLevel() {
/* 125 */     return this.maxLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getRateExp() {
/* 134 */     return this.rateExp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getBaseXP() {
/* 143 */     return this.baseXP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getLevelBaseXP() {
/* 152 */     return this.levelBaseXP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getLevelOffset() {
/* 161 */     return this.levelOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getStatPointsPerLevel() {
/* 170 */     return this.statPointsPerLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getStatValuePerPoint() {
/* 179 */     return this.statValuePerPoint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setRateExp(double rateExp) {
/* 188 */     this.rateExp = rateExp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setBaseXP(double baseXP) {
/* 197 */     this.baseXP = baseXP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setMaxLevel(int maxLevel) {
/* 206 */     this.maxLevel = maxLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevelBaseXP(double levelBaseXP) {
/* 215 */     this.levelBaseXP = levelBaseXP;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevelOffset(double levelOffset) {
/* 224 */     this.levelOffset = levelOffset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatPointsPerLevel(int statPointsPerLevel) {
/* 233 */     this.statPointsPerLevel = statPointsPerLevel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setStatValuePerPoint(double statValuePerPoint) {
/* 242 */     this.statValuePerPoint = statValuePerPoint;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isEnableHUD() {
/* 251 */     return this.enableHUD;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEnableHUD(boolean enableHUD) {
/* 260 */     this.enableHUD = enableHUD;
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\config\LevelingConfig.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */