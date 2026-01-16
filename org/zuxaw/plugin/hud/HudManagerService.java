/*     */ package org.zuxaw.plugin.hud;
/*     */ 
/*     */ import com.hypixel.hytale.logger.HytaleLogger;
/*     */ import com.hypixel.hytale.server.core.entity.entities.Player;
/*     */ import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
/*     */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.HashMap;
/*     */ import javax.annotation.Nonnull;
/*     */ import javax.annotation.Nullable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class HudManagerService
/*     */ {
/*  20 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*     */ 
/*     */   
/*  23 */   private static Object mhudInstance = null;
/*  24 */   private static Method mhudSetCustomHudMethod = null;
/*  25 */   private static Method mhudHideCustomHudMethod = null;
/*     */ 
/*     */   
/*     */   private static boolean mhudChecked = false;
/*     */ 
/*     */ 
/*     */   
/*     */   private static void checkMhudPlugin() {
/*  33 */     if (mhudChecked) {
/*     */       return;
/*     */     }
/*  36 */     mhudChecked = true;
/*     */ 
/*     */     
/*     */     try {
/*  40 */       Class<?> mhudClass = Class.forName("com.buuz135.mhud.MultipleHUD");
/*     */ 
/*     */       
/*  43 */       Method getInstanceMethod = mhudClass.getMethod("getInstance", new Class[0]);
/*  44 */       Object instance = getInstanceMethod.invoke(null, new Object[0]);
/*     */       
/*  46 */       if (instance != null) {
/*  47 */         mhudInstance = instance;
/*     */ 
/*     */         
/*     */         try {
/*  51 */           mhudSetCustomHudMethod = mhudClass.getMethod("setCustomHud", new Class[] { Player.class, PlayerRef.class, String.class, CustomUIHud.class });
/*     */         }
/*  53 */         catch (NoSuchMethodException e) {
/*  54 */           ((HytaleLogger.Api)LOGGER.atWarning()).log("MHUD plugin found but setCustomHud method not accessible");
/*     */         } 
/*     */ 
/*     */         
/*     */         try {
/*  59 */           mhudHideCustomHudMethod = mhudClass.getMethod("hideCustomHud", new Class[] { Player.class, PlayerRef.class, String.class });
/*     */         }
/*  61 */         catch (NoSuchMethodException noSuchMethodException) {}
/*     */ 
/*     */ 
/*     */         
/*  65 */         ((HytaleLogger.Api)LOGGER.atInfo()).log("MHUD plugin detected - using its API for HUD management");
/*     */       } else {
/*  67 */         ((HytaleLogger.Api)LOGGER.atWarning()).log("MHUD class found but getInstance() returned null");
/*     */       } 
/*  69 */     } catch (ClassNotFoundException e) {
/*     */       
/*  71 */       ((HytaleLogger.Api)LOGGER.atInfo()).log("MHUD plugin not found - HUD management disabled");
/*  72 */     } catch (Exception e) {
/*  73 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Error checking for MHUD plugin: " + e.getMessage());
/*  74 */       e.printStackTrace();
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
/*     */   public void setCustomHud(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull String hudIdentifier, @Nonnull CustomUIHud customHud) {
/*  90 */     checkMhudPlugin();
/*     */ 
/*     */     
/*  93 */     if (mhudInstance != null && mhudSetCustomHudMethod != null) {
/*     */       try {
/*  95 */         mhudSetCustomHudMethod.invoke(mhudInstance, new Object[] { player, playerRef, hudIdentifier, customHud });
/*     */         return;
/*  97 */       } catch (Exception e) {
/*  98 */         ((HytaleLogger.Api)LOGGER.atWarning()).log("Error using MHUD plugin API for " + hudIdentifier + ": " + e.getMessage());
/*     */       } 
/*     */     } else {
/* 101 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("MHUD plugin not available - cannot set HUD: " + hudIdentifier);
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
/*     */   public void hideCustomHud(@Nonnull Player player, @Nonnull PlayerRef playerRef, @Nonnull String hudIdentifier) {
/* 114 */     checkMhudPlugin();
/*     */ 
/*     */     
/* 117 */     if (mhudInstance != null && mhudHideCustomHudMethod != null) {
/*     */       try {
/* 119 */         mhudHideCustomHudMethod.invoke(mhudInstance, new Object[] { player, playerRef, hudIdentifier });
/*     */         return;
/* 121 */       } catch (Exception e) {
/* 122 */         ((HytaleLogger.Api)LOGGER.atWarning()).log("Error using MHUD plugin hideCustomHud API: " + e.getMessage());
/*     */       } 
/*     */     } else {
/* 125 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("MHUD plugin not available - cannot hide HUD: " + hudIdentifier);
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
/*     */   @Nullable
/*     */   public CustomUIHud getCustomHud(@Nonnull Player player, @Nonnull String hudIdentifier) {
/* 139 */     checkMhudPlugin();
/*     */     
/* 141 */     if (mhudInstance == null) {
/* 142 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 147 */       CustomUIHud currentCustomHud = player.getHudManager().getCustomHud();
/* 148 */       if (currentCustomHud == null) {
/* 149 */         return null;
/*     */       }
/*     */ 
/*     */       
/* 153 */       Class<?> hudClass = currentCustomHud.getClass();
/* 154 */       String className = hudClass.getName();
/* 155 */       if (className.equals("com.buuz135.mhud.MultipleCustomUIHud")) {
/* 156 */         Method getCustomHudsMethod = hudClass.getMethod("getCustomHuds", new Class[0]);
/*     */         
/* 158 */         HashMap<String, CustomUIHud> huds = (HashMap<String, CustomUIHud>)getCustomHudsMethod.invoke(currentCustomHud, new Object[0]);
/* 159 */         return huds.get(hudIdentifier);
/*     */       } 
/* 161 */     } catch (Exception e) {
/* 162 */       ((HytaleLogger.Api)LOGGER.atWarning()).log("Error getting HUD from MHUD wrapper: " + e.getMessage());
/*     */     } 
/*     */     
/* 165 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\hud\HudManagerService.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */