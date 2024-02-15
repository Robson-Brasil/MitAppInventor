package de.ullisroboterseite.ursai2sidebar;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.annotations.androidmanifest.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.*;

import android.view.View;
import android.view.MotionEvent;

import android.util.Log;

class TouchListener implements View.OnTouchListener {
   static final String LOG_TAG = UrsAI2SideBar.LOG_TAG;
   static final float MIN_DISTANCE = 100.0f;
   private float downX;
   private float downY;
   private float upX;
   private float upY;

  SideBar parentSideBar;

   public TouchListener(SideBar parentExension) {
      this.parentSideBar = parentExension;
   }

   public void onLeftToRightSwipe() {
      //TODO: Was hier passieren soll, sollte nicht hier entschieden werden.
      parentSideBar.showWithEvent(false);
   }

   public void onRightToLefttSwipe() {
      //TODO: Was hier passieren soll, sollte nicht hier entschieden werden.
      parentSideBar.hideWithEvent(false);
   }


   public boolean onTouch(View v, MotionEvent event) {
       switch (event.getAction()) {
         case MotionEvent.ACTION_DOWN:
            downX = event.getX();
            downY = event.getY();
            return true;
         case MotionEvent.ACTION_UP:
            upX = event.getX();
            upY = event.getY();
            float deltaX = downX - upX;
            float deltaY = downY - upY;
            if (Math.abs(deltaX) <= MIN_DISTANCE) {
               // nichts zu tun
            } else {
               if (deltaX < 0.0f) {
                  onLeftToRightSwipe();
                  return true;
               } else {
                  onRightToLefttSwipe();
                  return true;
               }
            }
            return false;
         default:
            return false;
      }
   }
}