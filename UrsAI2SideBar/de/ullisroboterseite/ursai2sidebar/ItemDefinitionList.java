package de.ullisroboterseite.ursai2sidebar;

import com.google.appinventor.components.runtime.Component;

import android.graphics.Color;
import android.content.res.ColorStateList;

import android.util.Log;
import java.util.*;

// Liste von Item-Definitionen
public class ItemDefinitionList {
   static final String LOG_TAG = UrsAI2SideBar.LOG_TAG;
   private List<ItemDefinition> defList = new ArrayList<ItemDefinition>();

   int sideBarWidth;
   int textColor = Color.BLACK;  // Speicher für default Textfarbe
   int textColorInactive = Component.COLOR_GRAY;
   float textSize = 16.0f; // Speicher für default Textgröße
   String fontTypeface = Component.TYPEFACE_DEFAULT; // Speicher für default font typeface
   boolean fontBold = false; // Speicher für default Schriftstärke
   boolean fontItalic = false; // Speicher für default Schriftlage

   int defaultIconColor = Color.BLACK;
   boolean useSwitches = false;
   boolean hasIcons = false;
   int paddingIcon = 16;
   int paddingText = 32;

   int thumbColorActive = Component.COLOR_WHITE;
   int thumbColorInactive = Component.COLOR_LTGRAY;
   int trackColorActive = Component.COLOR_GREEN;
   int trackColorInactive = Component.COLOR_GRAY;

   public void add(ItemDefinition def) {
      defList.add(def);
      def.parent = this;
   }

   public int size() {
      return defList.size();
   }

   public ItemDefinition get(int index) {
      return defList.get(index);
   }

   private ColorStateList createSwitchColors(int active_color, int inactive_color) {
      return new ColorStateList(//
            new int[][] { //
                  new int[] { android.R.attr.state_checked }, //
                  new int[] {} //
            }, //
            new int[] { active_color, inactive_color }//
      );//
   }

   ColorStateList getThumbColorStateList() {
      return createSwitchColors(thumbColorActive, thumbColorInactive);
   }

   ColorStateList getTrackColorStateList() {
      return createSwitchColors(trackColorActive, trackColorInactive);
   }

   void setItemText(int position, String value) {
      try {
         defList.get(position).setText(value);
      } catch (Exception e) {
         // nichts tun
      }
   }

   String getItemText(int position) {
      try {
         return defList.get(position).getText();
      } catch (Exception e) {
         return "";
      }
   }

   void setItemIconColor(int position, int value) {
      try {
         defList.get(position).iconColor = value;
      } catch (Exception e) {
         // nichts tun
      }
   }

   void setItemIconName(int position, String value) {
      try {
         defList.get(position).iconName = value;
         prepareHasIcons();
      } catch (Exception e) {
         // nichts tun
      }
   }

   void setItemEnabled(int position, boolean value) {
      try {
         defList.get(position).enabled = value;
      } catch (Exception e) {
         // nichts tun
      }
   }

   public void setItemChecked(int position, boolean value) {
      try {
         defList.get(position).isChecked = value;
      } catch (Exception e) {
         // nichts tun
      }
   }

   public boolean getItemChecked(int position) {
      try {
         return defList.get(position).isChecked;
      } catch (Exception e) {
         return false;
      }
   }

   public boolean hasItemCheckBox(int position) {
      try {
         return defList.get(position).hasCheckbox;
      } catch (Exception e) {
         return false;
      }
   }

   void prePareItemsList(String[] itemsStrings) {
      ItemDefinition def = null;
      defList.clear();

      // <hypertext>::<icon name>::<icon color>::<checked>
      // The <b>quick</b> runs::directions_run::red::T

      for (int index = 0; index < itemsStrings.length; index++) {
         String[] params = itemsStrings[index].split("::");
         int color = defaultIconColor;
         switch (params.length) {
            case 0:
               continue; // keine leeren Zeilen
            case 1: // nur Text
               def = new ItemDefinition(index, params[0], "", color);
               break;
            case 2: // Text + Icon-Name
               def = new ItemDefinition(index, params[0], params[1].trim(), color);
               hasIcons = true;
               break;
            case 3: // Text + Icon-Name + Icon-Farbe
               params[2] = params[2].trim();
               if (!params[2].isEmpty())
                  color = Color.parseColor(params[2]);
               def = new ItemDefinition(index, params[0], params[1].trim(), color);
               hasIcons = true;
               break;
            default: // Text + Icon-Name + Icon-Farbe + Enabled (+ weiteres wird ignoriert)
               params[2] = params[2].trim();
               if (!params[2].isEmpty())
                  color = Color.parseColor(params[2]);
               def = new ItemDefinition(index, params[0], params[1].trim(), color);
               if (params[3].trim().toLowerCase().startsWith("f")) {
                  def.hasCheckbox = true;
                  def.isChecked = false;
               } else if (params[3].trim().toLowerCase().startsWith("t")) {
                  def.hasCheckbox = true;
                  def.isChecked = true;
               }
               hasIcons = true;
               break;
         }
         def.parent = this;
         defList.add(def);
      }
   }

   void prepareHasIcons() {
      hasIcons = false;
      for (ItemDefinition def : defList) {
         if (!def.iconName.isEmpty()) {
            hasIcons = true;
            return;
         }
      }
   }
}