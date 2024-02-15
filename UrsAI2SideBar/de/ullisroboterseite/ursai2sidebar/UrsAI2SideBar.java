package de.ullisroboterseite.ursai2sidebar;

// Autor: https://UllisRoboterSeite.de

// Doku:  https://UllisRoboterSeite.de/android-AI2-Popup.html
//
// Version 1.0 (2021-02-13)
// -------------------------
// - Basis-Version
//
// Version 1.1 (2021-10-05)
// -------------------------
// - Fehler bei der Benennung des Events ItemCheckedChanged
//
// Version 1.2 (2022-09-01)
// -------------------------
// - Als Icon.Name können Asset-Images angegeben werden. Der Name muss dann mit "@" beginnen.
//
// Version 1.3 (2023-06-13)
// -------------------------
// - FontBold, FontItalic, FontTypeface hinzugefügt.
//
// Version 1.4 (2023-06-19)
// -------------------------
// - Exception bei TypeFace == Default beseitigt.

import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.annotations.androidmanifest.*;
import com.google.appinventor.components.common.*;
import com.google.appinventor.components.runtime.*;
import com.google.appinventor.components.runtime.util.*;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

import android.graphics.Typeface;

import android.util.Log;

import java.io.IOException;

import android.text.Html;


import java.lang.Thread;

@DesignerComponent(version = 1, //
        versionName = UrsAI2SideBar.VersionName, //
        dateBuilt = "2023-06-19", //
        description = "AI2 extension SideBar component.", //
        category = com.google.appinventor.components.common.ComponentCategory.EXTENSION, //
        nonVisible = true, //
        helpUrl = "https://UllisRoboterSeite.de/android-AI2-SideBar.html", //
        androidMinSdk = 14, //
        iconName = "aiwebres/icon.png")
@SimpleObject(external = true)
@UsesAssets(fileNames = "MaterialIcons-Regular.ttf")
public class UrsAI2SideBar extends AndroidNonvisibleComponent implements OnOrientationChangeListener {
    static final String LOG_TAG = "BAR";
    static final String VersionName = "1.4.0";

    final UrsAI2SideBar thisInstance = this;
    Handler handler = new Handler();

    static Typeface iconFont = null;
    SideBar sideBar;

    public UrsAI2SideBar(ComponentContainer container) {
        super(container.$form());

        Log.d(LOG_TAG, "======= UrsAI2SideBar " + VersionName);

        try {
            String fontPath = form.getAssetPathForExtension(this, "MaterialIcons-Regular.ttf");
            if (fontPath.startsWith(Form.ASSETS_PREFIX)) {
                iconFont = Typeface.createFromAsset(form.getAssets(), fontPath.substring(Form.ASSETS_PREFIX.length()));
            } else {
                iconFont = Typeface.createFromFile(fontPath.substring(("file://").length()));
            }
            Log.d(LOG_TAG, "Font loaded: " + iconFont.toString());
        } catch (Exception e) {
            Log.d(LOG_TAG, "Font load error");
            // nichts tun
        }

        sideBar = new SideBar(this, form);
        form.registerForOnOrientationChange(this);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                Log.e(LOG_TAG, paramThrowable.toString());
            }
        });
    }

    @Override
    public void onOrientationChange() {
        if (sideBar.isOpened) {
            sideBar.hideNoEvent();
            sideBar.showNoEvent();
        }
    }

    // =============================================================
    // Properties
    // =============================================================

    @SimpleProperty(description = "Returns the component's version name.")
    public String Version() {
        return VersionName;
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "True")
    @SimpleProperty(description = "Specifies whether the SideBar should be active and clickable.")
    public void Enabled(boolean value) {
        sideBar.setEnabled(value);
    }

    @SimpleProperty(description = "Returns true if the SideBar is active and clickable.")
    public boolean Enabled() {
        return sideBar.getEnabled();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_TEXTAREA, defaultValue = "")
    @SimpleProperty(description = "Set the list from a string of comma-separated values.")
    public void ItemsFromString(String itemstring) {
        sideBar.setItems(ElementsUtil.elementsFromString(itemstring));
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_ASSET, defaultValue = "")
    @SimpleProperty(description = "Loads the items from a file.", userVisible = false)
    public void ItemsFromFile(String FileName) {
        sideBar.loadItemsFromFile(FileName);
    }

    @SimpleProperty(description = "Sets the top image asset file name.")
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_ASSET, defaultValue = "")
    public void Image(String value) {
        sideBar.setImageName(value);
    }

    @SimpleProperty(description = "Gets the top image asset file name.")
    public String Image() {
        return sideBar.getImageName();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "True")
    @SimpleProperty(description = "Specifies wether the top image is scaled to the SideBar's width.")
    public void FitImageSize(boolean value) {
        sideBar.setFitImageSize(value);
    }

    @SimpleProperty(description = "Gets wether the SideBar is opened.")
    public boolean IsOpened() {
        return sideBar.isOpened;
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER, defaultValue = "24")
    @SimpleProperty(description = "Sets the size of the siwpe detector field.")
    public void SwipeDetectorWidth(int value) {
        sideBar.setSwipeDetectorWidth(value);
    }

    @SimpleProperty(description = "Gets the size of the siwpe detector field.")
    public int SwipeDetectorWidth() {
        return sideBar.getSwipeDetectorWidth();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER, defaultValue = "80")
    @SimpleProperty(description = "Specifies the width of the sidebar as a percentage of the display width [30..90].")
    public void SidebarWidthPercentage(int value) {
        if (value < 30)
            value = 30;
        if (value > 90)
            value = 90;
        sideBar.setSidebarWidthPercentage(value);
    }

    @SimpleProperty(description = "Gets the width of the sidebar as a percentage of the display width.")
    public int SidebarWidthPercentage() {
        return sideBar.getSidebarWidthPercentage();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_HORIZONTAL_ALIGNMENT, defaultValue = "1")
    @SimpleProperty(description = "Specifies the alignment of the title image: left, center, right).")
    public void ImageAlignment(int value) {
        sideBar.setImageAlignment(value);
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_BLACK)
    @SimpleProperty(description = "Specifies the SideBars's deafault text color as an alpha-red-green-blue integer.")
    public void TextColor(int argb) {
        sideBar.setTextColor(argb);
    }

    @SimpleProperty(description = "Gets the SideBars's deafult text color as an alpha-red-green-blue integer.")
    @IsColor
    public int TextColor() {
        return sideBar.getTextColor();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_GRAY)
    @SimpleProperty(description = "Specifies the SideBars's text color as an alpha-red-green-blue integer.")
    public void TextColorInactive(int argb) {
        sideBar.setTextColorInactive(argb);
    }

    @SimpleProperty(description = "Gets the SideBars's text color as an alpha-red-green-blue integer.")
    @IsColor
    public int TextColorInactive() {
        return sideBar.getTextColorInactive();
    }

    /**
     * The text font face of the Sidebar. Valid values are default, serif, sans
     * serif, monospace, and .ttf or .otf font file.
     *
     * @return one of TYPEFACE_DEFAULT, TYPEFACE_SERIF, TYPEFACE_SANSSERIF
     *         TYPEFACE_MONOSPACE}
     */
    @SimpleProperty(category = PropertyCategory.APPEARANCE, description = "Font family for %type% text.", userVisible = false)
    public String FontTypeface() {
        return sideBar.getFontTypeface();
    }

    /**
     * Specifies the text font face of the Sidebar as default, serif, sans
     * serif, monospace, or custom font typeface. To add a custom typeface,
     * upload a .ttf file to the project's media.
     *
     * @param typeface one of TYPEFACE_DEFAULT, TYPEFACE_SERIF, TYPEFACE_SANSSERIF
     *                 TYPEFACE_MONOSPACE}
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_TYPEFACE, defaultValue = Component.TYPEFACE_DEFAULT
            + "")
    @SimpleProperty(userVisible = false)
    public void FontTypeface(String typeface) {
        sideBar.setFontTypeface(typeface);
        Log.d(LOG_TAG, "TypeFace " + typeface);
    }

    /**
     * If set, the text of the Sidebar will attempt to use an italic font.
     * If italic has been requested, this property will return true even if the font
     * does not support italic.
     *
     * @return true indicates italic, false normal
     */
    @SimpleProperty(category = PropertyCategory.APPEARANCE, description = "If set, the text will be italicized by default.")
    public boolean FontItalic() {
        return sideBar.getTextItalic();
    }

    /**
     * Specifies whether the text of the Sidebar should be italic.
     * Some fonts do not support italic.
     *
     * @param italic true indicates italic, false normal
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "False")
    @SimpleProperty(category = PropertyCategory.APPEARANCE, description = "If set, the text will be italicized by default.")
    public void FontItalic(boolean italic) {
        sideBar.setTextItalic(italic);
    }

    /**
     * If set, the text of the Sidebar will attempt to use a bold font.
     * If bold has been requested, this property will return true,
     * even if the FontTypeface does not support bold.
     *
     * @return true indicates bold, false normal
     */
    @SimpleProperty(category = PropertyCategory.APPEARANCE, description = "If set, the text will be bold by default.")
    public boolean FontBold() {
        return sideBar.getTextBold();
    }

    /**
     * Specifies whether the text of the Sidebar should be bold.
     * Some fonts do not support bold.
     *
     * @param bold true indicates bold, false normal
     */
    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "False")
    @SimpleProperty(category = PropertyCategory.APPEARANCE, description = "If set, the text will be bold by default.")
    public void FontBold(boolean bold) {
        sideBar.setTextBold(bold);
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_BLACK)
    @SimpleProperty(description = "Specifies the SideBars's icon color as an alpha-red-green-blue integer.")
    public void IconColor(int argb) {
        sideBar.setIconColor(argb);
    }

    @SimpleProperty(description = "Gets the SideBars's icon color as an alpha-red-green-blue integer.")
    @IsColor
    public int IconColor() {
        return sideBar.getIconColor();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_WHITE)
    @SimpleProperty(description = "Specifies the SideBars's background color as an alpha-red-green-blue integer.")
    public void BackgroundColor(int argb) {
        sideBar.setBackgroundColor(argb);
    }

    @SimpleProperty(description = "Gets the SideBars's background color as an alpha-red-green-blue integer.")
    @IsColor
    public int BackgroundColor() {
        return sideBar.getBackgroundColor();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_FLOAT, defaultValue = "16.0")
    @SimpleProperty(description = "Specifies the default font size, measured in sp(scale-independent pixels).")
    public void FontSize(float size) {
        sideBar.setFontSize(size);
    }

    @SimpleProperty(description = "Gets the default font size.")
    public float FontSize() {
        return sideBar.getFontSize();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_BOOLEAN, defaultValue = "False")
    @SimpleProperty(description = "Specifies wether the SideBar displays Switches instead of CheckBoxes.")
    public void UseSwitches(boolean value) {
        sideBar.setUseSwitches(value);
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER, defaultValue = "16")
    @SimpleProperty(description = "Specifies the padding of the icon to the left border.")
    public void PaddingIcon(int value) {
        sideBar.setPaddingIcon(value);
    }

    @SimpleProperty(description = "Gets the padding of the icon to the left border.")
    public int PaddingIcon() {
        return sideBar.getPaddingIcon();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_NON_NEGATIVE_INTEGER, defaultValue = "32")
    @SimpleProperty(description = "Specifies the distance between the icon and the text.")
    public void PaddingText(int value) {
        sideBar.setPaddingText(value);
    }

    @SimpleProperty(description = "Gets the distance between the icon and the text.")
    public int PaddingText() {
        return sideBar.getPaddingText();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_WHITE)
    @SimpleProperty(description = "Specifies the Switch's thumb color when switch is in the On state.")
    public void SwitchThumbColorActive(int argb) {
        sideBar.setThumbColorActive(argb);

    }

    @SimpleProperty(description = "Gets the Switch's thumb color when switch is in the On state.")
    public int SwitchThumbColorActive() {
        return sideBar.getThumbColorActive();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_LTGRAY)
    @SimpleProperty(description = "Specifies the Switch's thumb color when switch is in the Off state.")
    public void SwitchThumbColorInactive(int argb) {
        sideBar.setThumbColorInactive(argb);
    }

    @SimpleProperty(description = "Gets the Switch's thumb color when switch is in the Off state.")
    public int SwitchThumbColorInactive() {
        return sideBar.getThumbColorInactive();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_GREEN)
    @SimpleProperty(description = "Color of the toggle track when switched on.")
    public void SwitchTrackColorActive(int argb) {
        sideBar.setTrackColorActive(argb);
    }

    @SimpleProperty(description = "Color of the toggle track when switched on.")
    public int SwitchTrackColorActive() {
        return sideBar.getTrackColorActive();
    }

    @DesignerProperty(editorType = PropertyTypeConstants.PROPERTY_TYPE_COLOR, defaultValue = Component.DEFAULT_VALUE_COLOR_DKGRAY)
    @SimpleProperty(description = "Color of the toggle track when switched off.")
    public void SwitchTrackColorInactive(int argb) {
        sideBar.setTrackColorInactive(argb);
    }

    @SimpleProperty(description = "Color of the toggle track when switched off.")
    public int SwitchTrackColorInactive() {
        return sideBar.getTrackColorInactive();
    }

    // =============================================================
    // Methods
    // =============================================================

    @SimpleFunction(description = "Defines the Sidebar items.")
    public void SetSideBarItems(YailList ListItems) {
        sideBar.setItems(ListItems);
    }

    @SimpleFunction(description = "Opens the SideBar.")
    public void Show() {
        try {
            sideBar.showWithEvent(true);
        } catch (Exception e) {
            DebugUtil.LogExecption(e);
        }
        
    }

    @SimpleFunction(description = "Hides the SideBar")
    public void Hide() {
        sideBar.hideWithEvent(true);

    }

    @SimpleFunction(description = "Loads the menu items from a file.", userVisible = false)
    public void LoadItemsFromFile(String FileName) {
        sideBar.loadItemsFromFile(FileName);
    }

    @SimpleFunction(description = "Sets enabled state of SideBar item. Counting starts with 1.")
    public void SetItemEnabled(int ItemNo, boolean Enabled) {
        sideBar.setItemEnabled(ItemNo - 1, Enabled);
    }

    @SimpleFunction(description = "Sets the text of the SideBar item. Counting starts with 1.")
    public void SetItemText(int ItemNo, String Text) {
        sideBar.setItemText(ItemNo - 1, Text);
    }

    @SimpleFunction(description = "Sets the icon color of the SideBar item. Counting starts with 1.")
    @IsColor()
    public void SetItemIconColor(int ItemNo, int Color) {
        sideBar.setItemIconColor(ItemNo - 1, Color);
    }

    @SimpleFunction(description = "Sets the icon name of the SideBar item. Counting starts with 1.")
    public void SetItemIconName(int ItemNo, String Name) {
        sideBar.setItemIconName(ItemNo - 1, Name);
    }

    @SimpleFunction(description = "Sets the checked state of the SideBar item. Counting starts with 1.")
    public void SetItemChecked(int ItemNo, boolean Checked) {
        sideBar.setItemChecked(ItemNo - 1, Checked);
    }

    @SimpleFunction(description = "Gets the checked state of the SideBar. Counting starts with 1.")
    public boolean GetItemChecked(int ItemNo) {
        return sideBar.getItemChecked(ItemNo - 1);
    }

    @SimpleFunction(description = "true, if item has a CheckBox. Counting starts with 1.")
    public boolean HasItemCheckBox(int ItemNo) {
        return sideBar.hasItemCheckBox(ItemNo - 1);
    }

    // =============================================================
    // Events
    // =============================================================

    @SimpleEvent(description = "Event for after item selection from the SideBar")
    public void AfterSelecting(int ItemNo, String Title) {
        EventDispatcher.dispatchEvent(this, "AfterSelecting", ItemNo, Title);
    }

    @SimpleEvent(description = "Item #1 was selected")
    public void Item1Selected(String Title) {
        EventDispatcher.dispatchEvent(this, "Item1Selected", Title);
    }

    @SimpleEvent(description = "Item #2 was selected")
    public void Item2Selected(String Title) {
        EventDispatcher.dispatchEvent(this, "Item2Selected", Title);
    }

    @SimpleEvent(description = "Item #3 was selected")
    public void Item3Selected(String Title) {
        EventDispatcher.dispatchEvent(this, "Item3Selected", Title);
    }

    @SimpleEvent(description = "Item #4 was selected")
    public void Item4Selected(String Title) {
        EventDispatcher.dispatchEvent(this, "Item4Selected", Title);
    }

    @SimpleEvent(description = "Item #5 was selected")
    public void Item5Selected(String Title) {
        EventDispatcher.dispatchEvent(this, "Item5Selected", Title);
    }

    @SimpleEvent(description = "Chacked state of a SideBar item has changed.")
    public void ItemCheckedChanged(final int ItemNo, final String Title, final boolean Checked) {
        EventDispatcher.dispatchEvent(thisInstance, "ItemCheckedChanged", ItemNo, Title, Checked);
    }

    @SimpleEvent(description = "SideBar is opening.")
    public void BeforeOpening(boolean ByCommand) {
        EventDispatcher.dispatchEvent(this, "BeforeOpening", ByCommand);
    }

    @SimpleEvent(description = "SideBar is closed.")
    public void AfterClosing(boolean ByCommand) {
        EventDispatcher.dispatchEvent(this, "AfterClosing", ByCommand);
    }
}