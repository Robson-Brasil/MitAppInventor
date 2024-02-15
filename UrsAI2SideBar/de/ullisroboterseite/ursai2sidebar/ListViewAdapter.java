package de.ullisroboterseite.ursai2sidebar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;

import android.util.Log;

import java.util.*;

public class ListViewAdapter extends BaseAdapter {
   static final String LOG_TAG = UrsAI2SideBar.LOG_TAG;

   private Context context;
   private ItemDefinitionList itemDefinitions;
   private List<SideBarItemView> views = new ArrayList<SideBarItemView>();

   private SideBar sideBar;

   /**
    * OnClickListener für die List-Items
    */
   private class OnItemClickListener implements View.OnClickListener {
      private int position;

      OnItemClickListener(int position) {
         this.position = position;
      }

      public void onClick(View arg0) {

         ItemDefinition id = itemDefinitions.get(position);
         if (id.hasCheckbox) {
            id.isChecked = !id.isChecked;
            views.get(position).checkBox.setChecked(id.isChecked);
            views.get(position).checkBox.invalidate();
            sideBar.raiseCheckChanged(position);
         } else
            sideBar.raiseAfterSelecting(position);

      }
   }

   /**
    * OnCheckedChangeListener für die CheckBox
    */
   private class OnItemCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
      private int position;

      OnItemCheckedChangeListener(int position) {
         this.position = position;
      }

      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
         sideBar.raiseCheckChanged(position);
      }

   }

   public ListViewAdapter(Context context, ItemDefinitionList itemDefinitions, SideBar sideBar) {
      this.context = context;
      this.itemDefinitions = itemDefinitions;
      this.sideBar = sideBar;

      for (int i = 0; i < itemDefinitions.size(); i++) {
         SideBarItemView v = new SideBarItemView(context, itemDefinitions.get(i));
         v.setOnClickListener(new OnItemClickListener(i));
         v.setOnCheckedChangeListener(new OnItemCheckedChangeListener(i));
         views.add(v);
      }
   }

   @Override
   public int getCount() {
      return itemDefinitions.size();
   }

   /**
    * Get the data item associated with the specified position in the data set.
    * @param position
    * @return
    */
   @Override
   public Object getItem(int position) {
      return itemDefinitions.get(position);
   }

   @Override
   public long getItemId(int position) {
      return (long) position;
   }

   @Override
   public View getView(int position, View convertView, ViewGroup parent) {
      return views.get(position);
   }
}