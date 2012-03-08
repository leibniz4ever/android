package com.overfitters;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

class Panel extends SurfaceView implements SurfaceHolder.Callback {
	private Bitmap bitmap;
	private int width, height, offsetX, offsetY;
 
    public Panel(Activity act) {
        super(act);
        getHolder().addCallback(this);
        setFocusable(true);
        width = height = ContentManager.getContentManager().getSize();
    }
 
    @Override
    public void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);
        if(bitmap != null)
        	canvas.drawBitmap(bitmap, offsetX, offsetY, null);
    }
 
    private void repaint(SurfaceHolder holder) {
    	Canvas c = holder.lockCanvas();
    	//used to have synchronized(holder)
    	if(c != null) {
	        this.onDraw(c);
	        holder.unlockCanvasAndPost(c);
    	}
    }
 
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    	repaint(holder);
    }
 
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		this.width = width;
		this.height = height;
		offsetY = height - bitmap.getHeight();
		offsetY /= 2;
		offsetX = width - bitmap.getWidth();
		offsetX /= 2;
		repaint(holder);
	}
    
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    	//do nothing
	}

	public void setImageBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
		//this.bWidth = bitmap.getWidth();
		//this.bHeight = bitmap.getHeight();
		
		offsetX = width-bitmap.getWidth();
		offsetX/=2;
		offsetY = height-bitmap.getHeight();
		offsetY/=2;
		repaint();
	}

	public void repaint() {
		SurfaceHolder holder = this.getHolder();
		if(holder != null)
			repaint(holder);
	}

	public void unsetImageBitmap() {
		bitmap = null;
	}
}