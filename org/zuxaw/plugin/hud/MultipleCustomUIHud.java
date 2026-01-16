/*    */ package org.zuxaw.plugin.hud;
/*    */ 
/*    */ import com.hypixel.hytale.logger.HytaleLogger;
/*    */ import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
/*    */ import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
/*    */ import com.hypixel.hytale.server.core.universe.PlayerRef;
/*    */ import java.lang.reflect.Method;
/*    */ import java.util.HashMap;
/*    */ import java.util.logging.Level;
/*    */ import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class MultipleCustomUIHud
/*    */   extends CustomUIHud
/*    */ {
/* 21 */   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
/*    */   private static Method BUILD_METHOD;
/*    */   
/*    */   static {
/*    */     try {
/* 26 */       BUILD_METHOD = CustomUIHud.class.getDeclaredMethod("build", new Class[] { UICommandBuilder.class });
/* 27 */       BUILD_METHOD.setAccessible(true);
/* 28 */     } catch (NoSuchMethodException e) {
/* 29 */       BUILD_METHOD = null;
/* 30 */       LOGGER.at(Level.SEVERE).log("Could not find method 'build' in CustomUIHud");
/* 31 */       LOGGER.at(Level.SEVERE).log(e.getMessage());
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private final HashMap<String, CustomUIHud> customHuds;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MultipleCustomUIHud(@NonNullDecl PlayerRef playerRef, HashMap<String, CustomUIHud> customHuds) {
/* 44 */     super(playerRef);
/* 45 */     this.customHuds = customHuds;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void build(@NonNullDecl UICommandBuilder uiCommandBuilder) {
/* 50 */     for (String key : this.customHuds.keySet()) {
/* 51 */       CustomUIHud hud = this.customHuds.get(key);
/*    */       try {
/* 53 */         if (BUILD_METHOD != null) {
/* 54 */           BUILD_METHOD.invoke(hud, new Object[] { uiCommandBuilder });
/*    */         }
/* 56 */       } catch (IllegalAccessException|java.lang.reflect.InvocationTargetException e) {
/* 57 */         throw new RuntimeException(e);
/*    */       } 
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HashMap<String, CustomUIHud> getCustomHuds() {
/* 68 */     return this.customHuds;
/*    */   }
/*    */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\hud\MultipleCustomUIHud.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */