/*    */ package org.zuxaw.plugin.utils;
/*    */ 
/*    */ import java.awt.Color;
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
/*    */ final class StyleState
/*    */   extends Record
/*    */ {
/*    */   private final Color color;
/*    */   private final boolean bold;
/*    */   private final boolean italic;
/*    */   private final boolean underlined;
/*    */   private final boolean mono;
/*    */   
/*    */   public final String toString() {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> toString : (Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;)Ljava/lang/String;
/*    */     //   6: areturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #44	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;
/*    */   }
/*    */   
/*    */   public final int hashCode() {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: <illegal opcode> hashCode : (Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;)I
/*    */     //   6: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #44	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	7	0	this	Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;
/*    */   }
/*    */   
/*    */   public final boolean equals(Object o) {
/*    */     // Byte code:
/*    */     //   0: aload_0
/*    */     //   1: aload_1
/*    */     //   2: <illegal opcode> equals : (Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;Ljava/lang/Object;)Z
/*    */     //   7: ireturn
/*    */     // Line number table:
/*    */     //   Java source line number -> byte code offset
/*    */     //   #44	-> 0
/*    */     // Local variable table:
/*    */     //   start	length	slot	name	descriptor
/*    */     //   0	8	0	this	Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;
/*    */     //   0	8	1	o	Ljava/lang/Object;
/*    */   }
/*    */   
/*    */   private StyleState(Color color, boolean bold, boolean italic, boolean underlined, boolean mono) {
/* 44 */     this.color = color; this.bold = bold; this.italic = italic; this.underlined = underlined; this.mono = mono; } public Color color() { return this.color; } public boolean bold() { return this.bold; } public boolean italic() { return this.italic; } public boolean underlined() { return this.underlined; } public boolean mono() { return this.mono; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   StyleState() {
/* 52 */     this(null, false, false, false, false);
/*    */   }
/*    */   
/*    */   StyleState withColor(Color color) {
/* 56 */     return new StyleState(color, this.bold, this.italic, this.underlined, this.mono);
/*    */   }
/*    */   
/*    */   StyleState withBold(boolean bold) {
/* 60 */     return new StyleState(this.color, bold, this.italic, this.underlined, this.mono);
/*    */   }
/*    */   
/*    */   StyleState withItalic(boolean italic) {
/* 64 */     return new StyleState(this.color, this.bold, italic, this.underlined, this.mono);
/*    */   }
/*    */   
/*    */   StyleState withUnderlined(boolean underlined) {
/* 68 */     return new StyleState(this.color, this.bold, this.italic, underlined, this.mono);
/*    */   }
/*    */   
/*    */   StyleState withMono(boolean mono) {
/* 72 */     return new StyleState(this.color, this.bold, this.italic, this.underlined, mono);
/*    */   }
/*    */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugi\\utils\TinyMsg$StyleState.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */