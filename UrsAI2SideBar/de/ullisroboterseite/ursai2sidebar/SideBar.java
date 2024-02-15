package de.ullisroboterseite.ursai2sidebar;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.annotations.androidmanifest.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.*;

import android.app.Activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.Display;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.util.Log;

import java.io.*;
import java.util.*;

public class SideBar {
   static final String LOG_TAG = UrsAI2SideBar.LOG_TAG;
   static final int TAG_MARKER = -45307337; // Ich hoffe, die benutzt niemand im AI2

   final UrsAI2SideBar parentExtension;
   final Form form;
   final SideBar thisInstance = this;

   // Properties
   boolean isOpened = false;
   boolean isEnabled = true;
   int swipeDetecorWidth = 24; // Größe in DIP
   int imageAlignment = 0; // Left
   String imageName = "";
   boolean fitImageSize = true;
   int widthSidebarPercent = 80; // Breite der eigentlichen Sidebar in % des Display-Größe
   int colorBG = 0xFFFFFFFF; // weiß

   ItemDefinitionList listViewItemDefs = new ItemDefinitionList();

   int sideBarFrameWidth = 0; // SideBar + Shadow = aktuelle Display-Breite
   float density = 1.0f; // Umrechnung Pixel <-> DIP

   int elevation = 24;
   Point displaySize = new Point();

   static Typeface iconFont;
   SwipeDetector swipeDetector;
   RelativeLayout sideBarFrame;
   ListView listView = null;
   ListViewAdapter listViewAdapter = null;

   SideBar(UrsAI2SideBar parentExtension, Form activity) {
      this.parentExtension = parentExtension;
      form = activity;
      float density = form.getResources().getDisplayMetrics().density;
      swipeDetector = SwipeDetector.addDetectorToActivity(form, (int) (density * swipeDetecorWidth), this);

      displaySize = new Point();
      form.getWindowManager().getDefaultDisplay().getSize(displaySize);
   }

   /**
    * Öffnet die SideBar ohne das Ereignis BeforeOpen auszulösen (bei
    * onOrientationChange).
    */
   public void showNoEvent() {
      show(false, false);
   }

   /**
    * Öffnet die SideBar und löst das Ereignis BeforeOpen aus.
    * 
    * @param byCommand true, bei Anweisung Show, false bei Wischen.
    */
   public void showWithEvent(boolean byCommand) {
      show(byCommand, true);
   }

   /**
    * Erstellt die View-Struktur für die Sidebar und zeigt die SideBar an.
    */
   public void show(boolean byCommand, boolean raiseEvent) {
      if (isOpened)
         return;
      isOpened = true;

      if (raiseEvent)
         parentExtension.BeforeOpening(byCommand);

      LinearLayout listViewContainer = null;
      LinearLayout imageViewContainer = null;
      final LinearLayout shadowField = new LinearLayout(form); // wird im Animator benötigt
      Bitmap topViewImage = null;
      ImageView topImageView = null;

      float density = form.getResources().getDisplayMetrics().density;
      int size48 = (int) (((float) 48) * density);
      int size24 = (int) (((float) 24) * density);
      int size16 = (int) (((float) 16) * density);
      int size12 = (int) (((float) 12) * density);
      int elevationdp = (int) (((float) elevation) * density);

      int barWidth = 0; // Breite der Sidebar
      if (displaySize.x < displaySize.y)
         barWidth = (int) (((float) displaySize.x) * widthSidebarPercent / 100);
      else
         barWidth = (int) (((float) displaySize.y) * widthSidebarPercent / 100);
      listViewItemDefs.sideBarWidth = barWidth;

      sideBarFrameWidth = displaySize.x;

      try {
         topViewImage = BitmapFactory.decodeStream(form.openAsset(imageName));
         if (fitImageSize) {
            topViewImage = Util.scaleBitmapAndKeepRatio(topViewImage, barWidth);
         }
      } catch (IOException e) {
         // nichts tun
      }

      topImageView = new ImageView(form);
      topImageView.setImageBitmap(topViewImage);

      imageViewContainer = new LinearLayout(form); // Zur Ausrichtung des Imnages
      imageViewContainer.setOrientation(LinearLayout.HORIZONTAL);
      imageViewContainer.setLayoutParams(new LinearLayout.LayoutParams(barWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
      switch (imageAlignment) {
         case 3: // Center
            imageViewContainer.setGravity(Gravity.CENTER);
            break;
         case 2: // Right
            imageViewContainer.setGravity(Gravity.RIGHT);
            break;
         default:
            imageViewContainer.setGravity(Gravity.LEFT);
      }
      imageViewContainer.setOnTouchListener(new TouchListener(this));
      imageViewContainer.addView(topImageView); // Ohne Bild ist topImageView == null

      listViewContainer = new LinearLayout(form);
      listViewContainer.setOrientation(LinearLayout.VERTICAL);
      listViewContainer.setLayoutParams(new LinearLayout.LayoutParams(barWidth, displaySize.y));
      listViewContainer.setGravity(Gravity.LEFT);

      listViewContainer.setBackgroundColor(colorBG);
      if (Build.VERSION.SDK_INT >= 21) {
         listViewContainer.setElevation((float) elevationdp);
      }

      listView = new ListView(form);
      listViewAdapter = new ListViewAdapter(form, listViewItemDefs, this);
      listView.setAdapter((ListAdapter) listViewAdapter);
      listView.setLayoutParams(
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

      listViewContainer.setOnTouchListener(new TouchListener(this));
      listViewContainer.addView(imageViewContainer);
      listViewContainer.addView(listView);

      // Wird weiter oben angelegt
      shadowField.setOrientation(LinearLayout.HORIZONTAL);
      shadowField.setLayoutParams(new LinearLayout.LayoutParams(displaySize.x - barWidth, displaySize.y));
      shadowField.setX((float) barWidth);
      shadowField.setBackgroundColor(0);

      shadowField.setOnClickListener(new View.OnClickListener() {
         public void onClick(View v) {
            shadowField.animate().alpha(0.0f).setInterpolator(new LinearInterpolator()).setDuration(50).start();
            thisInstance.hideWithEvent(false);
         }
      });

      // Hintergrund, füllt nach dem Aufklappen den gesamten Bildschirm
      // Ist Container für die eigentliche SideBar
      sideBarFrame = new RelativeLayout(form);
      sideBarFrame.setLayoutParams(
            new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
      sideBarFrame.setGravity(80);

      sideBarFrame.setBackgroundColor(0);
      sideBarFrame.addView(listViewContainer);
      sideBarFrame.addView(shadowField);
      sideBarFrame.setX((float) (-sideBarFrameWidth));
      RelativeLayout.LayoutParams layoutParam22 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
            ViewGroup.LayoutParams.FILL_PARENT);
      layoutParam22.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
      sideBarFrame.setTag(TAG_MARKER, "abc");
      form.addContentView(sideBarFrame, layoutParam22);

      sideBarFrame.animate().translationX(0).setInterpolator(new LinearInterpolator()).setDuration(400)
            .setListener(new Animator.AnimatorListener() {
               public void onAnimationStart(Animator animation) {
               }

               public void onAnimationEnd(Animator animation) {
                  shadowField.animate().alpha(0.3f).setInterpolator(new LinearInterpolator()).setDuration(100).start();
               }

               public void onAnimationCancel(Animator animation) {
               }

               public void onAnimationRepeat(Animator animation) {
               }
            });
   }

   /**
    * Schließt die SideBar (bei onOrientationChange)
    */
   public void hideNoEvent() {
      hide(false, false);
   }

   /**
    * Schließt die SideBar und löst das Ereignis AfterClosing aus.
    */
   public void hideWithEvent(boolean byCommand) {
      hide(byCommand, true);
   }

   private void hide(final boolean byCommand, final boolean raiseEvent) {
      if (!isOpened)
         return;
      isOpened = false;

      Interpolator interp = new LinearInterpolator();
      sideBarFrame.animate().translationX((float) (-sideBarFrameWidth)).setInterpolator(interp).setDuration(400)
            .setListener(new Animator.AnimatorListener() {
               public void onAnimationStart(Animator animation) {
               }

               public void onAnimationEnd(Animator animation) {
                  // bereits existierenden entfernen
                  ViewGroup pg = (ViewGroup) sideBarFrame.getParent(); // Das ist ein FrameLayout
                  for (int i = 0; i < pg.getChildCount(); i++) {
                     if (pg.getChildAt(i).getTag(TAG_MARKER) != null) {
                        pg.removeView(pg.getChildAt(i));
                        break;
                     }
                  }
                  listView = null;
                  if (raiseEvent)
                     parentExtension.AfterClosing(byCommand);
               }

               public void onAnimationCancel(Animator animation) {
               }

               public void onAnimationRepeat(Animator animation) {
               }
            });
   }

   // =============================================================
   // Setters & Getters
   // =============================================================

   /**
    * Aktiviert / Deaktiviert die SideBar
    */
   public void setEnabled(boolean value) {
      if (isEnabled == value)
         return;

      isEnabled = value;

      if (value)
         swipeDetector = SwipeDetector.addDetectorToActivity(form, (int) (density * swipeDetecorWidth), this);
      else
         swipeDetector.removeFromActivity();
   }

   /**
    * Ruft den Aktivierungszustand ab.
    */
   public boolean getEnabled() {
      return isEnabled;
   }

   /**
    * Legt die Breite der Detectorfläche für's nach rechts Wischen fest.
    * 
    * @param value Angabe in DIP
    */
   void setSwipeDetectorWidth(int value) {
      swipeDetecorWidth = value;
      swipeDetector.setWidth((int) (density * value));
   }

   /**
    * Ruft die Breite der Detectorfläche für's nach rechts Wischen ab.
    * 
    * @return Angabe in DIP
    */
   int getSwipeDetectorWidth() {
      return swipeDetecorWidth;
   }

   /**
    * Legt die Breite der SideBar in % der Display-Größe fest.
    */
   void setSidebarWidthPercentage(int value) {
      widthSidebarPercent = value;
   }

   /**
    * Ruft die Breite der SideBar in % der Display-Größe ab.
    */
   int getSidebarWidthPercentage() {
      return widthSidebarPercent;
   }

   /**
    * Legt die Items der SideBar fest.
    */
   void setItems(YailList listItems) {
      listViewItemDefs.prePareItemsList(listItems.toStringArray());
   }

   /**
    * Lagt die horizontale Ausrichtung des Titelbildes fest.
    * 
    * @param value 1: Left, 2: Right, 3: Center
    */
   void setImageAlignment(int value) {
      imageAlignment = value;
   }

   /**
    * Legt die Textfarbe der SideBar fest.
    * 
    * @param argb Die zu verwendende Textfarbe.
    */
   public void setTextColor(int argb) {
      listViewItemDefs.textColor = argb;
   }

   /**
    * Ruft die aktuelle Textfarbe für inaktive Elemente der SideBar ab.
    * 
    * @return Die aktuelle Textfarbe.
    */
   public int getTextColorInactive() {
      return listViewItemDefs.textColorInactive;
   }

   /**
    * Legt die Textfarbe für inaktive Elemente der SideBar fest.
    * 
    * @param argb Die zu verwendende Textfarbe.
    */
   public void setTextColorInactive(int argb) {
      listViewItemDefs.textColorInactive = argb;
   }

   /**
    * Ruft die Standard-Textfarbe der SideBar ab.
    * 
    * @return Die Standard-Textfarbe.
    */
   public int getTextColor() {
      return listViewItemDefs.textColor;
   }

   /**
    * Legt die Standard-Schriftart der SideBar fest.
    * 
    * @param bold Die zu verwendende Schriftart.
    */
   public void setFontTypeface(String ff) {
      listViewItemDefs.fontTypeface = ff;
   }

   /**
    * Ruft die Standard-Schriftart der SideBar ab.
    * 
    * @return Die Standard-Schriftstärke.
    */
   public String getFontTypeface() {
      return listViewItemDefs.fontTypeface;
   }

   /**
    * Legt die Standard-Schriftstärke der SideBar fest.
    * 
    * @param bold Die zu verwendende Schriftstärke.
    */
   public void setTextBold(boolean bold) {
      listViewItemDefs.fontBold = bold;
   }

   /**
    * Ruft die Standard-Schriftstärke der SideBar ab.
    * 
    * @return Die Standard-Schriftstärke.
    */
   public boolean getTextBold() {
      return listViewItemDefs.fontBold;
   }

   /**
    * Legt die Standard-Schriftlage der SideBar fest.
    * 
    * @param italic Die zu verwendende Schriftlage.
    */
   public void setTextItalic(boolean italic) {
      listViewItemDefs.fontItalic = italic;
   }

   /**
    * Ruft die Standard-Schriftlage der SideBar ab.
    * 
    * @return Die Standard-Schriftlage.
    */
   public boolean getTextItalic() {
      return listViewItemDefs.fontItalic;
   }

   /**
    * Legt die Hintergrundfarbe fest.
    */
   void setBackgroundColor(int argb) {
      colorBG = argb;
   }

   /**
    * Ruft die Hintergrundfrabe ab.
    */
   int getBackgroundColor() {
      return colorBG;
   }

   /**
    * Legt die Standardfarbe für die Symbole fest.
    */
   void setIconColor(int argb) {
      listViewItemDefs.defaultIconColor = argb;
   }

   /**
    * Ruft die Standardfarbe für die Symbole ab.
    */
   int getIconColor() {
      return listViewItemDefs.defaultIconColor;
   }

   /**
    * Legt die Schriftgröße der SideBar fest.
    * 
    * @param size Die zu verwendende Schriftgröße für die SideBar.
    */
   public void setFontSize(float size) {
      listViewItemDefs.textSize = size;
   }

   /**
    * Ruft die aktuelle Schriftgröße der SideBar ab.
    * 
    * @return Die aktuelle Schriftgröße für die SideBar.
    */
   public float getFontSize() {
      return listViewItemDefs.textSize;
   }

   /**
    * Legt fest, ob anstatt der CheckBox ein Switch angezeigt werden soll.
    */
   public void setUseSwitches(Boolean value) {
      listViewItemDefs.useSwitches = value;
   }

   /**
    * Legt den Namen für das Titelbild fest.
    */
   public void setImageName(String value) {
      imageName = value;
   }

   /**
    * Legt fest, ob die Breite des Bildes an die Breite der SideBar angepasst
    * werden soll.
    * Das Seitenverhältnis bleibt dabei erhalten.
    */
   public void setFitImageSize(boolean value) {
      fitImageSize = value;
   }

   /**
    * Ruft den Namen für das Titelbild ab.
    */
   public String getImageName() {
      return imageName;
   }

   /**
    * Legt den Abstand des Icons vom linken Rand fest, Angabe in DIP.
    */
   public void setPaddingIcon(int value) {
      listViewItemDefs.paddingIcon = value;
   }

   /**
    * Ruft den Abstand des Icons vom linken Rand ab, Angabe in DIP.
    */
   public int getPaddingIcon() {
      return listViewItemDefs.paddingIcon;
   }

   /**
    * Legt den Abstand des Textes vom Icon fest, Angabe in DIP.
    */
   public void setPaddingText(int value) {
      listViewItemDefs.paddingText = value;
   }

   /**
    * Ruft den Abstand des Textes vom Icon ab, Angabe in DIP.
    */
   public int getPaddingText() {
      return listViewItemDefs.paddingText;
   }

   public void setThumbColorActive(int argb) {
      listViewItemDefs.thumbColorActive = argb;

   }

   public int getThumbColorActive() {
      return listViewItemDefs.thumbColorActive;
   }

   public void setThumbColorInactive(int argb) {
      listViewItemDefs.thumbColorInactive = argb;
   }

   public int getThumbColorInactive() {
      return listViewItemDefs.thumbColorInactive;
   }

   public void setTrackColorActive(int argb) {
      listViewItemDefs.trackColorActive = argb;
   }

   public int getTrackColorActive() {
      return listViewItemDefs.trackColorActive;
   }

   public void setTrackColorInactive(int argb) {
      listViewItemDefs.trackColorInactive = argb;
   }

   public int getTrackColorInactive() {
      return listViewItemDefs.trackColorInactive;
   }

   /**
    * Legt den Enabled-Zustand für das SideBar Item fest.
    * 
    * @param position Position des Items in der SideBar, nullbasiert
    */
   void setItemEnabled(int position, boolean value) {
      listViewItemDefs.setItemEnabled(position, value);
   }

   /**
    * Legt den Text für das SideBar Item fest.
    * 
    * @param position Position des Items in der SideBar, nullbasiert
    */
   void setItemText(int position, String value) {
      listViewItemDefs.setItemText(position, value);
   }

   /**
    * Legt das Icon für das SideBar Item fest.
    * 
    * @param position Position des Items in der SideBar, nullbasiert
    */
   void setItemIconName(int position, String value) {
      listViewItemDefs.setItemIconName(position, value);
      listViewItemDefs.prepareHasIcons();
   }

   /**
    * Legt die Icon-Farbe für das SideBar Item fest.
    * 
    * @param position Position des Items in der SideBar, nullbasiert
    */
   void setItemIconColor(int position, int value) {
      listViewItemDefs.setItemIconColor(position, value);
   }

   /**
    * Legt den Checked-Status des Items fest.
    * 
    * @param position Position des Items in der SideBar, nullbasiert
    */
   public void setItemChecked(int position, boolean Checked) {
      listViewItemDefs.setItemChecked(position, Checked);
   }

   /**
    * Ruft den Checked-Status des Items ab.
    * 
    * @param position Position des Items in der SideBar, nullbasiert
    */
   public boolean getItemChecked(int position) {
      return listViewItemDefs.getItemChecked(position);
   }

   /**
    * Ruft ab, ob das Icon eine CheckBox besitzt.
    * 
    * @param position Position des Items in der SideBar, nullbasiert
    */
   public boolean hasItemCheckBox(int position) {
      return listViewItemDefs.hasItemCheckBox(position);
   }

   /**
    * Lädt die Itemliste aus einer Asset-Datei
    */
   void loadItemsFromFile(String fileName) {

      try {
         String line;
         List<String> lines = new ArrayList<String>();
         InputStream in = form.openAsset(fileName);
         InputStreamReader ir = new InputStreamReader(in);
         BufferedReader reader = new BufferedReader(ir);
         while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (!line.isEmpty())
               lines.add(line);
         }

         listViewItemDefs.prePareItemsList(lines.toArray(new String[lines.size()]));

      } catch (Exception e) {
         // nichts tun
      }
   }

   /**
    * Wird ausgelöst, wenn ein Item angeklickt wurde
    * 
    * @param itemPosition Postion des Items, nullbasiert.
    */
   void raiseAfterSelecting(int itemPosition) {
      if (!listViewItemDefs.get(itemPosition).enabled)
         return;
      String plainText = listViewItemDefs.get(itemPosition).plainText;
      parentExtension.AfterSelecting(itemPosition + 1, plainText);

      switch (itemPosition) {
         case 0:
            parentExtension.Item1Selected(plainText);
            break;
         case 1:
            parentExtension.Item2Selected(plainText);
            break;
         case 2:
            parentExtension.Item3Selected(plainText);
            break;
         case 3:
            parentExtension.Item4Selected(plainText);
            break;
         case 4:
            parentExtension.Item5Selected(plainText);
            break;
      }

   }

   /**
    * Wird asugelöst, wenn sich der Checked-State des Items ändert
    * 
    * @param itemPosition Postion des Items, nullbasiert.
    * @param ckecked      Neuer Chacked-State.
    */
   void raiseCheckChanged(int itemPosition) {
      if (!listViewItemDefs.get(itemPosition).enabled)
         return;
      String plainText = listViewItemDefs.get(itemPosition).plainText;
      boolean isChecked = listViewItemDefs.get(itemPosition).isChecked;
      parentExtension.ItemCheckedChanged(itemPosition + 1, plainText, isChecked);
   }
}