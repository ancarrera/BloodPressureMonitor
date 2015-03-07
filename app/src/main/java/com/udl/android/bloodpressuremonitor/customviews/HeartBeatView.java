package com.udl.android.bloodpressuremonitor.customviews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.udl.android.bloodpressuremonitor.R;
import com.udl.android.bloodpressuremonitor.fragments.HearRateMonitorFragment;

/**
 * This class extends the View class and is designed draw the heartbeat image.
 *
 * @author Justin Wetherell <phishman3579@gmail.com>
 * Modified by Adrian on 06/03/2015.
 */


public class HeartBeatView extends View {


        private static final Matrix matrix = new Matrix();
        private static final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private static Bitmap greenBitmap = null;
        private static Bitmap redBitmap = null;
        private static int parentWidth = 0;
        private static int parentHeight = 0;

        public HeartBeatView(Context context, AttributeSet attr) {
            super(context, attr);
            greenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blackheart);
            redBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.redheart);
        }
        public HeartBeatView(Context context) {
            super(context);
            greenBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.blackheart);
            redBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.redheart);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            parentWidth = MeasureSpec.getSize(widthMeasureSpec);
            parentHeight = MeasureSpec.getSize(heightMeasureSpec);
            setMeasuredDimension(parentWidth, parentHeight);
        }
        /**
         * {@inheritDoc}
         */
        @Override
        protected void onDraw(Canvas canvas) {
            if (canvas == null) throw new NullPointerException();
            Bitmap bitmap = null;
            if (HearRateMonitorFragment.getCurrent() == HearRateMonitorFragment.TYPE.GREEN) bitmap = greenBitmap;
            else bitmap = redBitmap;
            int bitmapX = bitmap.getWidth();
            int bitmapY = bitmap.getHeight();
            int parentX = parentWidth;
            int parentY = parentHeight;
            int centerX = (parentX - bitmapX)/2;
            int centerY = (parentY - bitmapY)/2;
            matrix.reset();
            matrix.postTranslate(centerX, centerY);
            canvas.drawBitmap(bitmap, matrix, paint);
        }
}
