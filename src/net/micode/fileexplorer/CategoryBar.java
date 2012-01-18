/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.micode.fileexplorer;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class CategoryBar extends View {
    private static final String LOG_TAG = "CategoryBar";

    private static final int MARGIN = 12;

    private static final int ANI_TOTAL_FRAMES = 10;

    private static final int ANI_PERIOD = 100;
    private Timer timer;

    private class Category {
        public long value;

        public long tmpValue; // used for animation

        public long aniStep; // animation step

        public int resImg;
    }

    private ArrayList<Category> categories = new ArrayList<Category>();

    private long mFullValue;

    public void setFullValue(long value) {
        mFullValue = value;
    }

    public CategoryBar(Context context) {
        this(context, null);
    }

    public CategoryBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CategoryBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void addCategory(int categoryImg) {
        Category ca = new Category();
        ca.resImg = categoryImg;
        categories.add(ca);
    }

    public boolean setCategoryValue(int index, long value) {
        if (index < 0 || index >= categories.size())
            return false;
        categories.get(index).value = value;
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth() - MARGIN * 2;
        Drawable d = getDrawable(R.drawable.category_bar_empty);

        int left = MARGIN;
        Rect bounds = new Rect(left, 0, left + width, d.getIntrinsicHeight());
        d.setBounds(bounds);
        d.draw(canvas);
        if (mFullValue != 0) {
            for (Category c : categories) {
                long value = (timer == null ? c.value : c.tmpValue);
                int w = (int) (value * width / mFullValue);
                if (w == 0)
                    continue;
                bounds.left = left;
                bounds.right = left + w;
                d = getDrawable(c.resImg);
                bounds.bottom = bounds.top + d.getIntrinsicHeight();
                d.setBounds(bounds);
                d.draw(canvas);
                left += w;
            }
        }
        bounds.left = 0;
        bounds.right = bounds.left + getWidth();
        d = getDrawable(R.drawable.category_bar_mask);
        d.setBounds(bounds);
        d.draw(canvas);
    }

    private Drawable getDrawable(int id) {
        return getContext().getResources().getDrawable(id);
    }

    private void stepAnimation() {
        if (timer == null)
            return;

        int finished = 0;
        for (Category c : categories) {
            c.tmpValue += c.aniStep;
            if (c.tmpValue >= c.value) {
                c.tmpValue = c.value;
                finished++;
                if (finished >= categories.size()) {
                    // stop animation
                    timer.cancel();
                    timer = null;
                    Log.v(LOG_TAG, "Animation stopped");
                    break;
                }
            }
        }

        postInvalidate();
    }

    synchronized public void startAnimation() {
        if (timer != null) {
            return;
        }

        Log.v(LOG_TAG, "startAnimation");

        for (Category c : categories) {
            c.tmpValue = 0;
            c.aniStep = c.value / ANI_TOTAL_FRAMES;
        }

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                stepAnimation();
            }

        }, 0, ANI_PERIOD);
    }
}