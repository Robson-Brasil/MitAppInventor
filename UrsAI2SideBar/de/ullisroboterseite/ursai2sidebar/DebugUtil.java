package de.ullisroboterseite.ursai2sidebar;

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.annotations.androidmanifest.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.*;

import android.os.Bundle;
import android.os.BaseBundle;
import android.util.*;

import java.util.*;

import java.lang.reflect.*;
import java.io.StringWriter;
import java.io.PrintWriter;

class DebugUtil {
    static final String LOG_TAG = UrsAI2SideBar.LOG_TAG;
     /**
     * Liefert eine AUflistung der Felder eines Objekts.
     * @param obj
     * @return
     */
    static String spyFields(Object obj) {
        StringBuffer buffer = new StringBuffer();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (!Modifier.isStatic(f.getModifiers())) {
                buffer.append(f.getType().getSimpleName());
                buffer.append(" ");
                buffer.append(f.getName());
                buffer.append(" = ");
                Object value = "*not accessible*";
                try {
                    f.setAccessible(true);
                    value = f.get(obj);
                } catch (Exception e) {
                    // nichts zu tun
                }
                if (f.getType().isArray())
                    buffer.append(spyArray(value));
                else if (value instanceof Bundle) {
                    Bundle extras = (Bundle) value;
                    buffer.append("" + value + "\n");
                    for (String key : extras.keySet()) { //extras is the Bundle containing info
                        Object val = extras.get(key); //get the current object
                        buffer.append("--- " + key + ":" + val + "\n");
                    }
                    buffer.append("--- end Bundle");
                }

                else
                    buffer.append("" + value);
                buffer.append("\n");
            }
        }
        return buffer.toString();
    }

    private static String spyArray(Object obj) {
        try {
            return Arrays.toString((boolean[]) obj);
        } catch (Exception e) {
        }
        try {
            return Arrays.toString((byte[]) obj);
        } catch (Exception e) {
        }
        try {
            return Arrays.toString((char[]) obj);
        } catch (Exception e) {
        }
        try {
            return Arrays.toString((double[]) obj);
        } catch (Exception e) {
        }
        try {
            return Arrays.toString((float[]) obj);
        } catch (Exception e) {
        }
        try {
            return Arrays.toString((int[]) obj);
        } catch (Exception e) {
        }
        try {
            return Arrays.toString((long[]) obj);
        } catch (Exception e) {
        }
        try {
            return Arrays.toString((short[]) obj);
        } catch (Exception e) {
        }
        try {
            return Arrays.deepToString((Object[]) obj);
        } catch (Exception e) {
        }
        return "*Invalid Array definition*";
    }

    /**
      * Liefert dern Wert eines privaten Feldes
      * @param object Das Objekt, vom dem der Wert ermittelt werden soll
      * @param name Bezeichnung des Feldes
      * @return Der Wert des Feldes
      * @throws Exception
      */
    static Object getPrivateField(Object object, String name) throws Exception {
        Class objectsClass = object.getClass();
        Field f = null;
        while (objectsClass != null) {
            try {
                f = objectsClass.getDeclaredField(name);
            } catch (NoSuchFieldException e) { // Feld nicht gefunden
                objectsClass = objectsClass.getSuperclass(); // nächste Ebene
                continue;
            } catch (Exception e) {
                throw e;
            }

            f.setAccessible(true);
            return f.get(object);
        }

        throw new NoSuchFieldException(name);
    }

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

  /**
   * Gibt die Exception und den Stracktrace im Log aus.
   * @param e
   */
  static void LogExecption(Throwable e) {
    Log.d(LOG_TAG, e.toString() + "\n");
    Throwable cause = e.getCause();
    if (cause != null)
      Log.d(LOG_TAG, "cause: " + cause.toString() + "\n");
    Log.d(LOG_TAG, getStackTrace(e));
  }

  /**
   * Gibt den Text, die Exception und den Stracktrace im Log aus.
   * @param text
   * @param e
   */
  static void LogExecption(String text, Throwable e) {
    Log.d(LOG_TAG, text + ": " + e.toString() + "\n");
    if (e != null) {
      Throwable cause = e.getCause();
      if (cause != null)
        Log.d(LOG_TAG, "cause: " + cause.toString() + "\n");
    }
    Log.d(LOG_TAG, getStackTrace(e));
  }
}