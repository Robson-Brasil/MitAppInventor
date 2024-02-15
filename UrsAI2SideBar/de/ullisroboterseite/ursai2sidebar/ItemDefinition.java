package de.ullisroboterseite.ursai2sidebar;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Html;
import android.content.res.ColorStateList;

import android.util.Log;

import java.util.*;

/**
 * Speichert die Informationen, die SideBarItemView zum Anzeigen benötigt.
 */
public class ItemDefinition {
    static final String LOG_TAG = UrsAI2SideBar.LOG_TAG;
    // TODO: hier weg, nach oben
    private static Typeface iconFont;
    ItemDefinitionList parent;
    int id;
    String iconName;
    private String text; // Nur über Setter änderbar
    int iconColor = Color.BLACK;
    boolean enabled = true;
    String plainText = "";
    SpannableString spanString;
    boolean hasCheckbox = false;
    boolean isChecked = false;

    public ItemDefinition(int id, String text, String iconName, int iconColor) {
        this.iconName = iconName;
        this.id = id;
        this.iconColor = iconColor;

        setText(text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        spanString = new SpannableString(Html.fromHtml(text));
        plainText = spanString.toString();
    }

    public Typeface getIconFont() {
        return UrsAI2SideBar.iconFont;
    }

    public int getTextColor() {
        return parent.textColor;
    }

    public int getTextColorInactive() {
        return parent.textColorInactive;
    }

    public float getTextSize() {
        return parent.textSize;
    }

    public int getBarSize() {
        return parent.sideBarWidth;
    }

    public boolean hasIcons() {
        return parent.hasIcons;
    }

    public boolean useSwitches() {
        return parent.useSwitches;
    }

    public int paddingIcon() {
        return parent.paddingIcon;
    }

    public int paddingText() {
        return parent.paddingText;
    }

    ColorStateList getThumbColors() {
        return parent.getThumbColorStateList();
    }

    ColorStateList getTrackColors() {
        return parent.getTrackColorStateList();
    }

    public boolean getFontBold() {
        return parent.fontBold;
    }

    public boolean getFontItalic() {
        return parent.fontItalic;
    }

    public String getFontTypeface() {
        return parent.fontTypeface;
    }

}