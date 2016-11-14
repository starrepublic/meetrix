package com.starrepublic.meetrix2.utils;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.transition.Transition;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;

/**
 * Created by admin on 2015-08-13.
 */
public class LUtils {

    public static Interpolator loadInterpolatorWithFallback(Context context, int resource, int fallbackResource) {
        try{
            return AnimationUtils.loadInterpolator(context, resource);
        }catch (Resources.NotFoundException ex){
            return AnimationUtils.loadInterpolator(context,fallbackResource);
        }

    }

    public static abstract class CircularRevealListener{
        public void onAnimationEnd(Animator animation){

        }
        public void onAnimationStart(Animator animation){

        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static class TransitionListenerAdapter implements Transition.TransitionListener {
        @Override
        public void onTransitionStart(Transition transition) {
        }

        @Override
        public void onTransitionEnd(Transition transition) {
        }

        @Override
        public void onTransitionCancel(Transition transition) {
        }

        @Override
        public void onTransitionPause(Transition transition) {
        }

        @Override
        public void onTransitionResume(Transition transition) {
        }
    }

    public static class CircularRevealBuilder{

        private final View view;
        private int cx = -1;
        private int cy = -1;
        private int radius = -1;
        private Interpolator interpolator;
        private int duration;
        private CircularRevealListener listener;
        private boolean withLayer;
        private int delay;
        private boolean adjustRadius;

        public CircularRevealBuilder(View view){
            this.view = view;
        };



        public CircularRevealBuilder cx(int cx){
            this.adjustRadius = true;
            this.cx = cx;
            return this;
        }

        public CircularRevealBuilder cy(int cy){
            this.adjustRadius = true;
            this.cy = cy;
            return this;
        }

        public CircularRevealBuilder radius(int radius){
            this.radius = radius;
            return this;
        }

        public CircularRevealBuilder interpolator(Interpolator interpolator){
            this.interpolator = interpolator;
            return this;
        }

        public CircularRevealBuilder duration(int duration){
            this.duration = duration;
            return this;
        }

        public CircularRevealBuilder listener(CircularRevealListener listener){
            this.listener = listener;
            return this;
        }

        public CircularRevealBuilder withLayer(){
            this.withLayer = true;
            return this;
        }

        public CircularRevealBuilder delay(int delay) {
            this.delay = delay;
            return this;
        }

        public void createAndStart(){

            if(isLollopop()){
                if(!view.isAttachedToWindow()){
                    ViewTreeObserver vto = view.getViewTreeObserver();
                    vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                        public void onGlobalLayout() {
                            view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            create().start();
                        }
                    });
                }else{
                    create().start();
                }
            }else if(listener!=null){
                listener.onAnimationStart(null);
                listener.onAnimationEnd(null);
            }
        }



        public Animator create(){
            Animator animator = null;
            if(isLollopop()){

                int viewWidth = view.getMeasuredWidth();
                int viewHeight = view.getMeasuredHeight();

                cx = cx==-1?viewWidth / 2:cx;
                cy = cy==-1?viewHeight / 2:cy;





                int largest;
                int smallest;

                if(adjustRadius){

                    largest = Math.max(cy, viewHeight-cy);
                    smallest = Math.max(cx, viewWidth-cx);

                }else{
                    largest = Math.max(viewWidth, viewHeight) / 2;
                    smallest = Math.min(viewWidth,viewHeight) / 2;


                }

                radius = radius == -1? (int) Math.hypot(largest,smallest) :radius;


                animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, radius);
                animator.setDuration(duration);
                animator.setStartDelay(delay);
                if(interpolator!=null) {
                    animator.setInterpolator(interpolator);
                }
                if(withLayer||listener!=null){
                    animator.addListener(new Animator.AnimatorListener() {
                        public int layerType;

                        @Override
                        public void onAnimationStart(Animator animation) {
                            if (withLayer) {
                                layerType = view.getLayerType();
                                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                            }
                            if (listener != null) {
                                listener.onAnimationStart(animation);
                            }
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (withLayer) {
                                view.setLayerType(layerType, null);
                            }
                            if (listener != null) {
                                listener.onAnimationEnd(animation);
                            }
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                }
            }
            return animator;
        }


    }

    public static boolean isLollopop(){
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
