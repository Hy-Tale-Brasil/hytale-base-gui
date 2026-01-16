/*     */ package org.zuxaw.plugin.components;
/*     */ 
/*     */ import com.hypixel.hytale.codec.Codec;
/*     */ import com.hypixel.hytale.codec.KeyedCodec;
/*     */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*     */ import com.hypixel.hytale.component.Component;
/*     */ import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PlayerLevelData
/*     */   implements Component<EntityStore>
/*     */ {
/*     */   public static final BuilderCodec<PlayerLevelData> CODEC;
/*     */   
/*     */   static {
/*  74 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(PlayerLevelData.class, PlayerLevelData::new).append(new KeyedCodec("Level", (Codec)Codec.INTEGER), (data, value) -> data.level = value.intValue(), data -> Integer.valueOf(data.level)).add()).append(new KeyedCodec("Experience", (Codec)Codec.DOUBLE), (data, value) -> data.experience = value.doubleValue(), data -> Double.valueOf(data.experience)).add()).append(new KeyedCodec("AvailableStatPoints", (Codec)Codec.INTEGER), (data, value) -> data.availableStatPoints = value.intValue(), data -> Integer.valueOf(data.availableStatPoints)).add()).append(new KeyedCodec("AllocatedStats", (Codec)Codec.STRING), (data, value) -> { data.allocatedStats = new HashMap<>(); if (value != null && !value.isEmpty()) { String[] entries = value.split(","); for (String entry : entries) { String[] parts = entry.split(":", 2); if (parts.length == 2) try { data.allocatedStats.put(parts[0], Integer.valueOf(Integer.parseInt(parts[1]))); } catch (NumberFormatException numberFormatException) {}  }  }  }data -> { if (data.allocatedStats == null || data.allocatedStats.isEmpty()) return (Function)"";  StringBuilder sb = new StringBuilder(); boolean first = true; for (Map.Entry<String, Integer> entry : data.allocatedStats.entrySet()) { if (!first) sb.append(",");  sb.append(entry.getKey()).append(":").append(entry.getValue()); first = false; }  return (Function)sb.toString(); }).add()).build();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  79 */   private int level = 1;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   private double experience = 0.0D;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  89 */   private int availableStatPoints = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  94 */   private Map<String, Integer> allocatedStats = new HashMap<>();
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
/*     */   public int getLevel() {
/* 108 */     return this.level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLevel(int level) {
/* 117 */     this.level = level;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public double getExperience() {
/* 126 */     return this.experience;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setExperience(double experience) {
/* 135 */     this.experience = experience;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addExperience(double amount) {
/* 144 */     this.experience += amount;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAvailableStatPoints() {
/* 153 */     return this.availableStatPoints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setAvailableStatPoints(int availableStatPoints) {
/* 162 */     this.availableStatPoints = availableStatPoints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Nonnull
/*     */   public Map<String, Integer> getAllocatedStats() {
/* 172 */     if (this.allocatedStats == null) {
/* 173 */       this.allocatedStats = new HashMap<>();
/*     */     }
/* 175 */     return this.allocatedStats;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getAllocatedPoints(@Nonnull String statName) {
/* 185 */     if (this.allocatedStats == null) {
/* 186 */       return 0;
/*     */     }
/* 188 */     return ((Integer)this.allocatedStats.getOrDefault(statName, Integer.valueOf(0))).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void allocatePoints(@Nonnull String statName, int points) {
/* 198 */     if (this.allocatedStats == null) {
/* 199 */       this.allocatedStats = new HashMap<>();
/*     */     }
/* 201 */     this.allocatedStats.put(statName, Integer.valueOf(points));
/*     */   }
/*     */ 
/*     */   
/*     */   @Nullable
/*     */   public Component<EntityStore> clone() {
/* 207 */     PlayerLevelData copy = new PlayerLevelData();
/* 208 */     copy.level = this.level;
/* 209 */     copy.experience = this.experience;
/* 210 */     copy.availableStatPoints = this.availableStatPoints;
/* 211 */     if (this.allocatedStats != null) {
/* 212 */       copy.allocatedStats = new HashMap<>(this.allocatedStats);
/*     */     }
/* 214 */     return copy;
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\components\PlayerLevelData.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */