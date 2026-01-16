/*     */ package org.zuxaw.plugin.utils;
/*     */ 
/*     */ import com.hypixel.hytale.protocol.packets.interface_.NotificationStyle;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import com.hypixel.hytale.server.core.universe.Universe;
/*     */ import com.hypixel.hytale.server.core.universe.world.World;
/*     */ import com.hypixel.hytale.server.core.util.EventTitleUtil;
/*     */ import com.hypixel.hytale.server.core.util.NotificationUtil;
/*     */ import java.util.UUID;
/*     */ import javax.annotation.Nonnull;
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
/*     */ public class NotificationHelper
/*     */ {
/*     */   public static void sendNotification(@Nonnull PlayerRef playerRef, @Nonnull String message, @Nonnull NotificationStyle style) {
/*  29 */     NotificationUtil.sendNotification(playerRef
/*  30 */         .getPacketHandler(), 
/*  31 */         TinyMsg.parse(message), style);
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
/*     */   public static void sendNotification(@Nonnull PlayerRef playerRef, @Nonnull String message) {
/*  43 */     sendNotification(playerRef, message, NotificationStyle.Default);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sendSuccessNotification(@Nonnull PlayerRef playerRef, @Nonnull String message) {
/*  53 */     sendNotification(playerRef, message, NotificationStyle.Success);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sendWarningNotification(@Nonnull PlayerRef playerRef, @Nonnull String message) {
/*  63 */     sendNotification(playerRef, message, NotificationStyle.Warning);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void sendDangerNotification(@Nonnull PlayerRef playerRef, @Nonnull String message) {
/*  73 */     sendNotification(playerRef, message, NotificationStyle.Danger);
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
/*     */   public static void showEventTitle(@Nonnull PlayerRef playerRef, @Nonnull String primaryTitle, @Nonnull String secondaryTitle, boolean isMajor) {
/*  87 */     UUID worldUuid = playerRef.getWorldUuid();
/*  88 */     if (worldUuid != null) {
/*  89 */       World world = Universe.get().getWorld(worldUuid);
/*  90 */       if (world != null && world.isAlive()) {
/*  91 */         world.execute(() -> EventTitleUtil.showEventTitleToPlayer(playerRef, Message.raw(primaryTitle), Message.raw(secondaryTitle), isMajor));
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 106 */       EventTitleUtil.showEventTitleToPlayer(playerRef, 
/*     */           
/* 108 */           Message.raw(primaryTitle), 
/* 109 */           Message.raw(secondaryTitle), isMajor);
/*     */     
/*     */     }
/* 112 */     catch (Exception e) {
/*     */       
/* 114 */       sendNotification(playerRef, primaryTitle + " - " + primaryTitle, NotificationStyle.Success);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void showLevelUpTitle(@Nonnull PlayerRef playerRef, int newLevel) {
/* 125 */     showEventTitle(playerRef, "LEVEL UP!", "You are now level " + newLevel + "!", true);
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
/*     */   public static void showMaxLevelTitle(@Nonnull PlayerRef playerRef, int maxLevel) {
/* 140 */     showEventTitle(playerRef, "CONGRATULATIONS!", "You have reached the maximum level " + maxLevel + "!", true);
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugi\\utils\NotificationHelper.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */