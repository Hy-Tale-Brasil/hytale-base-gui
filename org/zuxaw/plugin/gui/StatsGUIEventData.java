/*    */ package org.zuxaw.plugin.gui;
/*    */ 
/*    */ import com.hypixel.hytale.codec.Codec;
/*    */ import com.hypixel.hytale.codec.KeyedCodec;
/*    */ import com.hypixel.hytale.codec.builder.BuilderCodec;
/*    */ import java.util.function.Supplier;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class StatsGUIEventData
/*    */ {
/*    */   public static final BuilderCodec<StatsGUIEventData> CODEC;
/*    */   public String statName;
/*    */   public String action;
/*    */   public Integer amount;
/*    */   
/*    */   static {
/* 41 */     CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(StatsGUIEventData.class, StatsGUIEventData::new).append(new KeyedCodec("StatName", (Codec)Codec.STRING), (data, value) -> data.statName = (value != null) ? value : "", data -> (data.statName != null) ? data.statName : "").add()).append(new KeyedCodec("Action", (Codec)Codec.STRING), (data, value) -> data.action = (value != null) ? value : "", data -> (data.action != null) ? data.action : "").add()).append(new KeyedCodec("Amount", (Codec)Codec.STRING), (data, value) -> { if (value == null || value.isEmpty()) { data.amount = Integer.valueOf(0); } else { try { data.amount = Integer.valueOf(Integer.parseInt(value)); } catch (NumberFormatException e) { data.amount = Integer.valueOf(0); }  }  }data -> (data.amount != null) ? String.valueOf(data.amount) : "0").add()).build();
/*    */   }
/*    */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugin\gui\StatsGUIEventData.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */