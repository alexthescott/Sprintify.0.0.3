package com.spotify.sdk.android.authentication.sample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

enum ButtonsState{
    GONE,
    RIGHT_VISIBLE
}

public class SwipeController extends ItemTouchHelper.Callback {
    private boolean swipeBack = false;
    private ButtonsState buttonShowedState = ButtonsState.GONE;
    private RectF buttonInstance = null;
    private RecyclerView.ViewHolder currentItemViewHolder = null;
    private SwipeControllerActions buttonActions = null;
    private static final float buttonWidth = 200;
    private Context context;

    public SwipeController(Context ctx, SwipeControllerActions buttonActions){
        this.buttonActions = buttonActions;
        this.context = ctx;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0,  ItemTouchHelper.LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = false;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
            if(buttonShowedState != ButtonsState.GONE){
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) dX = Math.min(dX, -buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            else{
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
        if(buttonShowedState == ButtonsState.GONE){
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        currentItemViewHolder = viewHolder;
    }

    private void setTouchListener(final Canvas c, final RecyclerView rView, final RecyclerView.ViewHolder vHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        rView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if(swipeBack){
                    if(dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;

                    if(buttonShowedState != ButtonsState.GONE){
                        setTouchDownListener(c, rView, vHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(rView, false);
                    }
                }
                return false;
            }
        });
    }

    private void setTouchDownListener(final Canvas c, final RecyclerView rView, final RecyclerView.ViewHolder vHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        rView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, rView, vHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c, final RecyclerView rView, final RecyclerView.ViewHolder vHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive){
        rView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP){
                    SwipeController.super.onChildDraw(c, rView, vHolder, 0F, dY, actionState, isCurrentlyActive);
                    rView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(rView, true);
                    swipeBack = false;

                    if(buttonActions != null && buttonInstance != null && buttonInstance.contains(event.getX(), event.getY())){
                        if(buttonShowedState ==ButtonsState.RIGHT_VISIBLE){
                            buttonActions.onRightClicked(vHolder.getAdapterPosition());
                        }
                    }
                    buttonShowedState = ButtonsState.GONE;
                    currentItemViewHolder = null;
                }
                return false;
            }
        });
    }

    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder){
        float buttonWidthWithoutPadding = buttonWidth - 10;
        float corners = 5;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();

        RectF rButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(rButton, corners, corners, p);

        Resources res = context.getResources();
        Bitmap bpm = getBitmap(R.drawable.ic_delete_black_24dp);

        Log.d("BITMAP is NULL?", String.valueOf(bpm == null));


        c.drawBitmap(bpm, rButton.centerX() - (bpm.getWidth() / 2), rButton.centerY() - (bpm.getHeight()/2), null);
        buttonInstance = null;
        if(buttonShowedState == ButtonsState.RIGHT_VISIBLE){
            buttonInstance = rButton;
        }
    }


    public void onDraw(Canvas c){
        if(currentItemViewHolder != null){
            drawButtons(c, currentItemViewHolder);
        }
    }

    private void setItemsClickable(RecyclerView rv, boolean isClickable){
        for(int i = 0; i < rv.getChildCount(); ++i){
            rv.getChildAt(i).setClickable(isClickable);
        }
    }

    private Bitmap getBitmap(int drawableRes){
        Drawable drawable = context.getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }

}
