/*     */ package org.zuxaw.plugin.utils;
/*     */ 
/*     */ import com.hypixel.hytale.protocol.MaybeBool;
/*     */ import com.hypixel.hytale.server.core.Message;
/*     */ import java.awt.Color;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class TinyMsg
/*     */ {
/*  19 */   private static final Pattern TAG_PATTERN = Pattern.compile("<(/?)([a-zA-Z0-9_]+)(?::([^>]+))?>");
/*     */   
/*  21 */   private static final Map<String, Color> NAMED_COLORS = new HashMap<>();
/*     */   
/*     */   static {
/*  24 */     NAMED_COLORS.put("black", new Color(0, 0, 0));
/*  25 */     NAMED_COLORS.put("dark_blue", new Color(0, 0, 170));
/*  26 */     NAMED_COLORS.put("dark_green", new Color(0, 170, 0));
/*  27 */     NAMED_COLORS.put("dark_aqua", new Color(0, 170, 170));
/*  28 */     NAMED_COLORS.put("dark_red", new Color(170, 0, 0));
/*  29 */     NAMED_COLORS.put("dark_purple", new Color(170, 0, 170));
/*  30 */     NAMED_COLORS.put("gold", new Color(255, 170, 0));
/*  31 */     NAMED_COLORS.put("gray", new Color(170, 170, 170));
/*  32 */     NAMED_COLORS.put("dark_gray", new Color(85, 85, 85));
/*  33 */     NAMED_COLORS.put("blue", new Color(85, 85, 255));
/*  34 */     NAMED_COLORS.put("green", new Color(85, 255, 85));
/*  35 */     NAMED_COLORS.put("aqua", new Color(85, 255, 255));
/*  36 */     NAMED_COLORS.put("red", new Color(255, 85, 85));
/*  37 */     NAMED_COLORS.put("light_purple", new Color(255, 85, 255));
/*  38 */     NAMED_COLORS.put("yellow", new Color(255, 255, 85));
/*  39 */     NAMED_COLORS.put("white", new Color(255, 255, 255));
/*     */     
/*  41 */     NAMED_COLORS.put("lime", new Color(85, 255, 85));
/*     */   }
/*     */   private static final class StyleState extends Record { private final Color color; private final boolean bold; private final boolean italic; private final boolean underlined; private final boolean mono;
/*  44 */     private StyleState(Color color, boolean bold, boolean italic, boolean underlined, boolean mono) { this.color = color; this.bold = bold; this.italic = italic; this.underlined = underlined; this.mono = mono; } public final String toString() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> toString : (Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;)Ljava/lang/String;
/*     */       //   6: areturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #44	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*  44 */       //   0	7	0	this	Lorg/zuxaw/plugin/utils/TinyMsg$StyleState; } public Color color() { return this.color; } public final int hashCode() { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: <illegal opcode> hashCode : (Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;)I
/*     */       //   6: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #44	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	7	0	this	Lorg/zuxaw/plugin/utils/TinyMsg$StyleState; } public final boolean equals(Object o) { // Byte code:
/*     */       //   0: aload_0
/*     */       //   1: aload_1
/*     */       //   2: <illegal opcode> equals : (Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;Ljava/lang/Object;)Z
/*     */       //   7: ireturn
/*     */       // Line number table:
/*     */       //   Java source line number -> byte code offset
/*     */       //   #44	-> 0
/*     */       // Local variable table:
/*     */       //   start	length	slot	name	descriptor
/*     */       //   0	8	0	this	Lorg/zuxaw/plugin/utils/TinyMsg$StyleState;
/*  44 */       //   0	8	1	o	Ljava/lang/Object; } public boolean bold() { return this.bold; } public boolean italic() { return this.italic; } public boolean underlined() { return this.underlined; } public boolean mono() { return this.mono; }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     StyleState() {
/*  52 */       this(null, false, false, false, false);
/*     */     }
/*     */     
/*     */     StyleState withColor(Color color) {
/*  56 */       return new StyleState(color, this.bold, this.italic, this.underlined, this.mono);
/*     */     }
/*     */     
/*     */     StyleState withBold(boolean bold) {
/*  60 */       return new StyleState(this.color, bold, this.italic, this.underlined, this.mono);
/*     */     }
/*     */     
/*     */     StyleState withItalic(boolean italic) {
/*  64 */       return new StyleState(this.color, this.bold, italic, this.underlined, this.mono);
/*     */     }
/*     */     
/*     */     StyleState withUnderlined(boolean underlined) {
/*  68 */       return new StyleState(this.color, this.bold, this.italic, underlined, this.mono);
/*     */     }
/*     */     
/*     */     StyleState withMono(boolean mono) {
/*  72 */       return new StyleState(this.color, this.bold, this.italic, this.underlined, mono);
/*     */     } }
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
/*     */   public static Message parse(String text) {
/*  94 */     if (text == null) {
/*  95 */       return Message.raw("");
/*     */     }
/*  97 */     if (text.isEmpty()) {
/*  98 */       return Message.raw(text);
/*     */     }
/*     */     
/* 101 */     Message root = Message.empty();
/* 102 */     Deque<StyleState> stateStack = new ArrayDeque<>();
/* 103 */     stateStack.push(new StyleState());
/*     */     
/* 105 */     Matcher matcher = TAG_PATTERN.matcher(text);
/* 106 */     int lastEnd = 0;
/*     */     
/* 108 */     while (matcher.find()) {
/*     */       
/* 110 */       if (matcher.start() > lastEnd) {
/* 111 */         String content = text.substring(lastEnd, matcher.start());
/* 112 */         if (!content.isEmpty()) {
/* 113 */           Message segmentMsg = createStyledMessage(content, stateStack.peek());
/* 114 */           root = Message.join(new Message[] { root, segmentMsg });
/*     */         } 
/*     */       } 
/*     */       
/* 118 */       boolean isClosing = matcher.group(1).equals("/");
/* 119 */       String tagName = matcher.group(2).toLowerCase();
/* 120 */       String tagArg = matcher.group(3);
/*     */       
/* 122 */       if (isClosing) {
/*     */         
/* 124 */         if (stateStack.size() > 1) {
/* 125 */           stateStack.pop();
/*     */         }
/*     */       } else {
/*     */         
/* 129 */         StyleState currentState = stateStack.peek();
/* 130 */         StyleState newState = currentState;
/*     */         
/* 132 */         switch (tagName) {
/*     */           case "color":
/* 134 */             if (tagArg != null) {
/* 135 */               Color color = parseColor(tagArg);
/* 136 */               if (color != null) {
/* 137 */                 newState = newState.withColor(color);
/*     */               }
/*     */             } 
/*     */             break;
/*     */ 
/*     */           
/*     */           case "gradient":
/* 144 */             if (tagArg != null) {
/* 145 */               String[] colors = tagArg.split(":");
/* 146 */               if (colors.length >= 2) {
/* 147 */                 Color startColor = parseColor(colors[0]);
/* 148 */                 Color endColor = parseColor(colors[1]);
/* 149 */                 if (startColor != null && endColor != null)
/*     */                 {
/* 151 */                   newState = newState.withColor(startColor);
/*     */                 }
/*     */               } 
/*     */             } 
/*     */             break;
/*     */           case "b":
/* 157 */             newState = newState.withBold(true);
/*     */             break;
/*     */           case "i":
/* 160 */             newState = newState.withItalic(true);
/*     */             break;
/*     */           case "u":
/* 163 */             newState = newState.withUnderlined(true);
/*     */             break;
/*     */           case "mono":
/* 166 */             newState = newState.withMono(true);
/*     */             break;
/*     */           case "reset":
/* 169 */             newState = new StyleState();
/*     */             break;
/*     */         } 
/*     */         
/* 173 */         stateStack.push(newState);
/*     */       } 
/*     */       
/* 176 */       lastEnd = matcher.end();
/*     */     } 
/*     */ 
/*     */     
/* 180 */     if (lastEnd < text.length()) {
/* 181 */       String content = text.substring(lastEnd);
/* 182 */       if (!content.isEmpty()) {
/* 183 */         Message segmentMsg = createStyledMessage(content, stateStack.peek());
/* 184 */         root = Message.join(new Message[] { root, segmentMsg });
/*     */       } 
/*     */     } 
/*     */     
/* 188 */     return root;
/*     */   }
/*     */   
/*     */   private static Message createStyledMessage(String content, StyleState state) {
/* 192 */     Message msg = Message.raw(content);
/*     */     
/* 194 */     if (state.color != null) {
/* 195 */       msg = msg.color(state.color);
/*     */     }
/* 197 */     if (state.bold) (msg.getFormattedMessage()).bold = MaybeBool.True; 
/* 198 */     if (state.italic) (msg.getFormattedMessage()).italic = MaybeBool.True; 
/* 199 */     if (state.underlined) (msg.getFormattedMessage()).underlined = MaybeBool.True;
/*     */ 
/*     */     
/* 202 */     return msg;
/*     */   }
/*     */   
/*     */   private static Message applyGradient(String text, StyleState state) {
/* 206 */     Message container = Message.empty();
/*     */ 
/*     */ 
/*     */     
/* 210 */     for (char ch : text.toCharArray()) {
/* 211 */       Message charMsg = Message.raw(String.valueOf(ch));
/* 212 */       if (state.color != null) {
/* 213 */         charMsg = charMsg.color(state.color);
/*     */       }
/* 215 */       if (state.underlined) (charMsg.getFormattedMessage()).underlined = MaybeBool.True; 
/* 216 */       container = Message.join(new Message[] { container, charMsg });
/*     */     } 
/* 218 */     return container;
/*     */   }
/*     */   
/*     */   private static Color parseColor(String colorStr) {
/* 222 */     if (colorStr == null || colorStr.isEmpty()) {
/* 223 */       return null;
/*     */     }
/*     */ 
/*     */     
/* 227 */     Color namedColor = NAMED_COLORS.get(colorStr.toLowerCase());
/* 228 */     if (namedColor != null) {
/* 229 */       return namedColor;
/*     */     }
/*     */ 
/*     */     
/* 233 */     if (colorStr.startsWith("#")) {
/*     */       try {
/* 235 */         String hex = colorStr.substring(1);
/* 236 */         if (hex.length() == 6) {
/* 237 */           int r = Integer.parseInt(hex.substring(0, 2), 16);
/* 238 */           int g = Integer.parseInt(hex.substring(2, 4), 16);
/* 239 */           int b = Integer.parseInt(hex.substring(4, 6), 16);
/* 240 */           return new Color(r, g, b);
/* 241 */         }  if (hex.length() == 3) {
/* 242 */           int r = Integer.parseInt(hex.substring(0, 1), 16) * 17;
/* 243 */           int g = Integer.parseInt(hex.substring(1, 2), 16) * 17;
/* 244 */           int b = Integer.parseInt(hex.substring(2, 3), 16) * 17;
/* 245 */           return new Color(r, g, b);
/*     */         } 
/* 247 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 252 */     return null;
/*     */   }
/*     */ }


/* Location:              C:\Users\Acer\Downloads\RPGLeveling-0.0.3.jar!\org\zuxaw\plugi\\utils\TinyMsg.class
 * Java compiler version: 25 (69.0)
 * JD-Core Version:       1.1.3
 */