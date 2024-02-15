package de.ullisroboterseite.ursai2sidebar;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.*;

import android.graphics.Matrix;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.util.Log;

/**
 * Statische Hilfsfunktionen
 */
public class Util {
   static final String LOG_TAG = UrsAI2SideBar.LOG_TAG;

   /**
    * Liefert den StackTrace der Exception als String.
    * @param e Die Exception, zu der der StackTrace zurück geliefert werden soll.
    * @return Der ermittelte StackTrace
    */
   static String getStackTrace(Throwable e) {
      StringWriter sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      e.printStackTrace(pw);
      String stackTrace = sw.toString(); // stack trace as a string
      return stackTrace;
   }

   public static Bitmap scaleBitmapAndKeepRatio(Bitmap sourceBitmap, int reqWidthInPixels) {
      Matrix matrix = new Matrix();
      int reqHeightInPixels = sourceBitmap.getHeight() * reqWidthInPixels / sourceBitmap.getWidth();
      matrix.setRectToRect(new RectF(0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight()),
            new RectF(0, 0, reqWidthInPixels, reqHeightInPixels), Matrix.ScaleToFit.CENTER);
      Bitmap scaledBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, sourceBitmap.getWidth(), sourceBitmap.getHeight(), matrix,
            true);
      return scaledBitmap;
   }
}