package com.sentaroh.android.TinyPictureViewer3;
/*
The MIT License (MIT)
Copyright (c) 2011-2019 Sentaroh

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights to use,
copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
and to permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be included in all copies or
substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

*/

import android.content.Context;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;

import androidx.annotation.AttrRes;
import androidx.annotation.MenuRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.appcompat.view.SupportMenuInflater;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.view.menu.ShowableListMenu;
import androidx.appcompat.widget.ForwardingListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomPopupMenu {
    private static Logger log= LoggerFactory.getLogger(CustomPopupMenu.class);
    private final Context mContext;
    private final MenuBuilder mMenu;
    private final View mAnchor;
    final MenuPopupHelper mPopup;
    OnMenuItemClickListener mMenuItemClickListener;
    OnDismissListener mOnDismissListener;
    private View.OnTouchListener mDragListener;
    /**
     * Constructor to create a new popup menu with an anchor view.
     *
     * @param context Context the popup menu is running in, through which it
     *        can access the current theme, resources, etc.
     * @param anchor Anchor view for this popup. The popup will appear below
     *        the anchor if there is room, or above it if there is not.
     */
    public CustomPopupMenu(@NonNull Context context, @NonNull View anchor) {
        this(context, anchor, Gravity.NO_GRAVITY);
    }
    /**
     * Constructor to create a new popup menu with an anchor view and alignment
     * gravity.
     *
     * @param context Context the popup menu is running in, through which it
     *        can access the current theme, resources, etc.
     * @param anchor Anchor view for this popup. The popup will appear below
     *        the anchor if there is room, or above it if there is not.
     * @param gravity The {@link Gravity} value for aligning the popup with its
     *        anchor.
     */
    public CustomPopupMenu(@NonNull Context context, @NonNull View anchor, int gravity) {
        this(context, anchor, gravity, R.attr.popupMenuStyle, 0);
    }
    /**
     * Constructor a create a new popup menu with a specific style.
     *
     * @param context Context the popup menu is running in, through which it
     *        can access the current theme, resources, etc.
     * @param anchor Anchor view for this popup. The popup will appear below
     *        the anchor if there is room, or above it if there is not.
     * @param gravity The {@link Gravity} value for aligning the popup with its
     *        anchor.
     * @param popupStyleAttr An attribute in the current theme that contains a
     *        reference to a style resource that supplies default values for
     *        the popup window. Can be 0 to not look for defaults.
     * @param popupStyleRes A resource identifier of a style resource that
     *        supplies default values for the popup window, used only if
     *        popupStyleAttr is 0 or can not be found in the theme. Can be 0
     *        to not look for defaults.
     */
    public CustomPopupMenu(@NonNull Context context, @NonNull View anchor, int gravity,
                           @AttrRes int popupStyleAttr, @StyleRes int popupStyleRes) {
        mContext = context;
        mAnchor = anchor;
        mMenu = new MenuBuilder(context);
        mMenu.setCallback(new MenuBuilder.Callback() {
            @Override
            public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
                if (mMenuItemClickListener != null) {
                    return mMenuItemClickListener.onMenuItemClick(item);
                }
                return false;
            }
            @Override
            public void onMenuModeChange(MenuBuilder menu) {
            }
        });
        mPopup = new MenuPopupHelper(context, mMenu, anchor, false, popupStyleAttr, popupStyleRes);
        mPopup.setGravity(gravity);
        mPopup.setForceShowIcon(true);
        mPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                if (mOnDismissListener != null) {
                    mOnDismissListener.onDismiss(CustomPopupMenu.this);
                }
            }
        });
    }
    /**
     * Sets the gravity used to align the popup window to its anchor view.
     * <p>
     * If the popup is showing, calling this method will take effect only
     * the next time the popup is shown.
     *
     * @param gravity the gravity used to align the popup window
     * @see #getGravity()
     */
    public void setGravity(int gravity) {
        mPopup.setGravity(gravity);
    }
    /**
     * @return the gravity used to align the popup window to its anchor view
     * @see #setGravity(int)
     */
    public int getGravity() {
        return mPopup.getGravity();
    }
    /**
     * Returns an {@link View.OnTouchListener} that can be added to the anchor view
     * to implement drag-to-open behavior.
     * <p>
     * When the listener is set on a view, touching that view and dragging
     * outside of its bounds will open the popup window. Lifting will select
     * the currently touched list item.
     * <p>
     * Example usage:
     * <pre>
     * PopupMenu myPopup = new PopupMenu(context, myAnchor);
     * myAnchor.setOnTouchListener(myPopup.getDragToOpenListener());
     * </pre>
     *
     * @return a touch listener that controls drag-to-open behavior
     */
    @NonNull
    public View.OnTouchListener getDragToOpenListener() {
        if (mDragListener == null) {
            mDragListener = new ForwardingListener(mAnchor) {
                @Override
                protected boolean onForwardingStarted() {
                    show();
                    return true;
                }
                @Override
                protected boolean onForwardingStopped() {
                    dismiss();
                    return true;
                }
                @Override
                public ShowableListMenu getPopup() {
                    // This will be null until show() is called.
                    return mPopup.getPopup();
                }
            };
        }
        return mDragListener;
    }
    /**
     * Returns the {@link Menu} associated with this popup. Populate the
     * returned Menu with items before calling {@link #show()}.
     *
     * @return the {@link Menu} associated with this popup
     * @see #show()
     * @see #getMenuInflater()
     */
    @NonNull
    public Menu getMenu() {
        return mMenu;
    }
    /**
     * @return a {@link MenuInflater} that can be used to inflate menu items
     *         from XML into the menu returned by {@link #getMenu()}
     * @see #getMenu()
     */
    @NonNull
    public MenuInflater getMenuInflater() {
        return new SupportMenuInflater(mContext);
    }
    /**
     * Inflate a menu resource into this PopupMenu. This is equivalent to
     * calling {@code popupMenu.getMenuInflater().inflate(menuRes, popupMenu.getMenu())}.
     *
     * @param menuRes Menu resource to inflate
     */
    public void inflate(@MenuRes int menuRes) {
        getMenuInflater().inflate(menuRes, mMenu);
    }
    /**
     * Show the menu popup anchored to the view specified during construction.
     *
     * @see #dismiss()
     */
    public void show() {
        mPopup.show();
    }
    /**
     * Dismiss the menu popup.
     *
     * @see #show()
     */
    public void dismiss() {
        mPopup.dismiss();
    }
    /**
     * Sets a listener that will be notified when the user selects an item from
     * the menu.
     *
     * @param listener the listener to notify
     */
    public void setOnMenuItemClickListener(@Nullable OnMenuItemClickListener listener) {
        mMenuItemClickListener = listener;
    }
    /**
     * Sets a listener that will be notified when this menu is dismissed.
     *
     * @param listener the listener to notify
     */
    public void setOnDismissListener(@Nullable OnDismissListener listener) {
        mOnDismissListener = listener;
    }
    /**
     * Interface responsible for receiving menu item click events if the items
     * themselves do not have individual item click listeners.
     */
    public interface OnMenuItemClickListener {
        /**
         * This method will be invoked when a menu item is clicked if the item
         * itself did not already handle the event.
         *
         * @param item the menu item that was clicked
         * @return {@code true} if the event was handled, {@code false}
         * otherwise
         */
        boolean onMenuItemClick(MenuItem item);
    }
    /**
     * Callback interface used to notify the application that the menu has closed.
     */
    public interface OnDismissListener {
        /**
         * Called when the associated menu has been dismissed.
         *
         * @param menu the popup menu that was dismissed
         */
        void onDismiss(CustomPopupMenu menu);
    }

//    private void replacePopupMenuAdapter() {
//        final ArrayList<MenuItemImpl> ml=new ArrayList<MenuItemImpl>();
//        BaseAdapter adapter=(BaseAdapter) mPopup.getPopup()..getListView().getAdapter();
//
//        boolean w_icon_specified=false;
//        for(int i=0;i<adapter.getCount();i++) {
//            MenuItemImpl mii=(MenuItemImpl) adapter.getItem(i);
//            ml.add(mii);
//            if (mii.getIcon()!=null) w_icon_specified=true;
//        }
//        final boolean icon_specified=w_icon_specified;
//        BaseAdapter mListPopupAdapter = new BaseAdapter() {
//            class ViewHolder {
//                private TextView title;
//                private ImageView icon;
//            }
//
//            @Override
//            public int getCount() {
//                return ml.size();
//            }
//
//            @Override
//            public Object getItem(int position) {
//                return ml.get(position);
//            }
//
//            @Override
//            public long getItemId(int position) {
//                return position;
//            }
//
//            @Override
//            public View getView(int position, View convertView, ViewGroup parent) {
//                if (convertView == null) {
//                    convertView = LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.custom_popup_menu_item, parent, false);
//                    ViewHolder viewHolder = new ViewHolder();
//                    viewHolder.icon = (ImageView) convertView.findViewById(R.id.icon);
//                    viewHolder.title = (TextView) convertView.findViewById(R.id.title);
//                    convertView.setTag(viewHolder);
//                }
//
//                ViewHolder viewHolder = (ViewHolder) convertView.getTag();
//                viewHolder.title.setText(ml.get(position).getTitle());
//                if (icon_specified) {
//                    viewHolder.icon.setVisibility(ImageView.VISIBLE);
//                    viewHolder.icon.setImageDrawable(ml.get(position).getIcon());
//                } else {
//                    viewHolder.icon.setVisibility(ImageView.INVISIBLE);
//                }
//
//                return convertView;
//            }
//
//        };
//        mPopup.getPopup().getListView().setAdapter(mListPopupAdapter);
//
//    }

}
