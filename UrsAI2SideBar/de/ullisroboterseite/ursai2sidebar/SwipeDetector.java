package de.ullisroboterseite.ursai2sidebar;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.annotations.androidmanifest.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.*;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.app.Activity;
import android.widget.RelativeLayout;

import android.util.Log;

/**
 * Eine transparente Fläche am linken Rand, die das Wischen von links nach rechts erkennt.
 */
public class SwipeDetector {
   static final String LOG_TAG = UrsAI2SideBar.LOG_TAG;
   static final int TAG_MARKER = -45307336; // Ich hoffe, die benutzt niemand im AI2
   RelativeLayout relativeLayout = null;

   /**
    * Erstellt eine neue Instanz der SwipeDetector-Klasse und fügt sie der Activiy als ContentView hinzu.
    * @param activity
    * @param width
    * @param parentSideBar
    * @return
    */
   static SwipeDetector addDetectorToActivity(Activity activity, int width, SideBar parentSideBar) {
      SwipeDetector swipeDetector = new SwipeDetector();
      swipeDetector.relativeLayout = new RelativeLayout(activity);

      swipeDetector.relativeLayout.setBackgroundColor(0); // transparent
      swipeDetector.relativeLayout.setOnTouchListener(new TouchListener(parentSideBar));
      RelativeLayout.LayoutParams layoutParam = new RelativeLayout.LayoutParams(width,
            ViewGroup.LayoutParams.FILL_PARENT);
      activity.addContentView(swipeDetector.relativeLayout, layoutParam);

      // ggf. bereits existierenden entfernen
      ViewGroup pg = (ViewGroup) swipeDetector.relativeLayout.getParent(); // Das ist ein FrameLayout
      for (int i = 0; i < pg.getChildCount(); i++) {
         if (pg.getChildAt(i).getTag(TAG_MARKER) != null) {
            pg.removeView(pg.getChildAt(i));
            break;
         }
      }

      // Neu hinzugefügten markieren
      swipeDetector.relativeLayout.setTag(TAG_MARKER, "abc");
      return swipeDetector;
   }

   void removeFromActivity() {
      ((ViewGroup) relativeLayout.getParent()).removeView(relativeLayout);
      relativeLayout = null;
   }

   void setWidth(int value) {
      ViewGroup.LayoutParams params = relativeLayout.getLayoutParams();
      params.width = value;
      relativeLayout.setLayoutParams(params);
   }


}