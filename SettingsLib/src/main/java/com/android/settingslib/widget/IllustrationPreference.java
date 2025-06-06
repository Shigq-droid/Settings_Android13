/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settingslib.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.RawRes;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.android.settingslib.R;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * IllustrationPreference is a preference that can play lottie format animation
 */
public class IllustrationPreference extends Preference {

    private static final String TAG = "IllustrationPreference";

    private static final boolean IS_ENABLED_LOTTIE_ADAPTIVE_COLOR = false;
    private static final int SIZE_UNSPECIFIED = -1;

    private int mMaxHeight = SIZE_UNSPECIFIED;
    private int mImageResId;
    private boolean mIsAutoScale;
    private Uri mImageUri;
    private Drawable mImageDrawable;
    private View mMiddleGroundView;

    private final Animatable2.AnimationCallback mAnimationCallback =
            new Animatable2.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    ((Animatable) drawable).start();
                }
            };

    private final Animatable2Compat.AnimationCallback mAnimationCallbackCompat =
            new Animatable2Compat.AnimationCallback() {
                @Override
                public void onAnimationEnd(Drawable drawable) {
                    ((Animatable) drawable).start();
                }
            };

    public IllustrationPreference(Context context) {
        super(context);
        init(context, /* attrs= */ null);
    }

    public IllustrationPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public IllustrationPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public IllustrationPreference(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        final ImageView backgroundView =
                (ImageView) holder.findViewById(R.id.background_view);
        final FrameLayout middleGroundLayout =
                (FrameLayout) holder.findViewById(R.id.middleground_layout);
        final LottieAnimationView illustrationView =
                (LottieAnimationView) holder.findViewById(R.id.lottie_view);

        // To solve the problem of non-compliant illustrations, we set the frame height
        // to 300dp and set the length of the short side of the screen to
        // the width of the frame.
        final int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        final int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
        final FrameLayout illustrationFrame = (FrameLayout) holder.findViewById(
                R.id.illustration_frame);
        final LayoutParams lp = (LayoutParams) illustrationFrame.getLayoutParams();
        lp.width = screenWidth < screenHeight ? screenWidth : screenHeight;
        illustrationFrame.setLayoutParams(lp);

        handleImageWithAnimation(illustrationView);
        handleImageFrameMaxHeight(backgroundView, illustrationView);

        if (mIsAutoScale) {
            illustrationView.setScaleType(mIsAutoScale
                            ? ImageView.ScaleType.CENTER_CROP
                            : ImageView.ScaleType.CENTER_INSIDE);
        }

        handleMiddleGroundView(middleGroundLayout);

        if (IS_ENABLED_LOTTIE_ADAPTIVE_COLOR) {
            ColorUtils.applyDynamicColors(getContext(), illustrationView);
        }
    }

    /**
     * Sets the middle ground view to preference. The user
     * can overlay a view on top of the animation.
     */
    public void setMiddleGroundView(View view) {
        if (view != mMiddleGroundView) {
            mMiddleGroundView = view;
            notifyChanged();
        }
    }

    /**
     * Removes the middle ground view of preference.
     */
    public void removeMiddleGroundView() {
        mMiddleGroundView = null;
        notifyChanged();
    }

    /**
     * Enables the auto scale feature of animation view.
     */
    public void enableAnimationAutoScale(boolean enable) {
        if (enable != mIsAutoScale) {
            mIsAutoScale = enable;
            notifyChanged();
        }
    }

    /**
     * Sets the lottie illustration resource id.
     */
    public void setLottieAnimationResId(int resId) {
        if (resId != mImageResId) {
            resetImageResourceCache();
            mImageResId = resId;
            notifyChanged();
        }
    }

    /**
     * Gets the lottie illustration resource id.
     */
    public int getLottieAnimationResId() {
        return mImageResId;
    }

    /**
     * Sets the image drawable to display image in {@link LottieAnimationView}.
     *
     * @param imageDrawable the drawable of an image
     */
    public void setImageDrawable(Drawable imageDrawable) {
        if (imageDrawable != mImageDrawable) {
            resetImageResourceCache();
            mImageDrawable = imageDrawable;
            notifyChanged();
        }
    }

    /**
     * Gets the image drawable from display image in {@link LottieAnimationView}.
     *
     * @return the drawable of an image
     */
    public Drawable getImageDrawable() {
        return mImageDrawable;
    }

    /**
     * Sets the image uri to display image in {@link LottieAnimationView}.
     *
     * @param imageUri the Uri of an image
     */
    public void setImageUri(Uri imageUri) {
        if (imageUri != mImageUri) {
            resetImageResourceCache();
            mImageUri = imageUri;
            notifyChanged();
        }
    }

    /**
     * Gets the image uri from display image in {@link LottieAnimationView}.
     *
     * @return the Uri of an image
     */
    public Uri getImageUri() {
        return mImageUri;
    }

    /**
     * Sets the maximum height of the views, still use the specific one if the maximum height was
     * larger than the specific height from XML.
     *
     * @param maxHeight the maximum height of the frame views in terms of pixels.
     */
    public void setMaxHeight(int maxHeight) {
        if (maxHeight != mMaxHeight) {
            mMaxHeight = maxHeight;
            notifyChanged();
        }
    }

    private void resetImageResourceCache() {
        mImageDrawable = null;
        mImageUri = null;
        mImageResId = 0;
    }

    private void handleMiddleGroundView(ViewGroup middleGroundLayout) {
        middleGroundLayout.removeAllViews();

        if (mMiddleGroundView != null) {
            middleGroundLayout.addView(mMiddleGroundView);
            middleGroundLayout.setVisibility(View.VISIBLE);
        } else {
            middleGroundLayout.setVisibility(View.GONE);
        }
    }

    private void handleImageWithAnimation(LottieAnimationView illustrationView) {
        if (mImageDrawable != null) {
            resetAnimations(illustrationView);
            illustrationView.setImageDrawable(mImageDrawable);
            final Drawable drawable = illustrationView.getDrawable();
            if (drawable != null) {
                startAnimation(drawable);
            }
        }

        if (mImageUri != null) {
            resetAnimations(illustrationView);
            illustrationView.setImageURI(mImageUri);
            final Drawable drawable = illustrationView.getDrawable();
            if (drawable != null) {
                startAnimation(drawable);
            } else {
                // The lottie image from the raw folder also returns null because the ImageView
                // couldn't handle it now.
                startLottieAnimationWith(illustrationView, mImageUri);
            }
        }

        if (mImageResId > 0) {
            resetAnimations(illustrationView);
            illustrationView.setImageResource(mImageResId);
            final Drawable drawable = illustrationView.getDrawable();
            if (drawable != null) {
                startAnimation(drawable);
            } else {
                // The lottie image from the raw folder also returns null because the ImageView
                // couldn't handle it now.
                startLottieAnimationWith(illustrationView, mImageResId);
            }
        }
    }

    private void handleImageFrameMaxHeight(ImageView backgroundView, ImageView illustrationView) {
        if (mMaxHeight == SIZE_UNSPECIFIED) {
            return;
        }

        final Resources res = backgroundView.getResources();
        final int frameWidth = res.getDimensionPixelSize(R.dimen.settingslib_illustration_width);
        final int frameHeight = res.getDimensionPixelSize(R.dimen.settingslib_illustration_height);
        final int restrictedMaxHeight = Math.min(mMaxHeight, frameHeight);
        backgroundView.setMaxHeight(restrictedMaxHeight);
        illustrationView.setMaxHeight(restrictedMaxHeight);

        // Ensures the illustration view size is smaller than or equal to the background view size.
        final float aspectRatio = (float) frameWidth / frameHeight;
        illustrationView.setMaxWidth((int) (restrictedMaxHeight * aspectRatio));
    }

    private void startAnimation(Drawable drawable) {
        if (!(drawable instanceof Animatable)) {
            return;
        }

        if (drawable instanceof Animatable2) {
            ((Animatable2) drawable).registerAnimationCallback(mAnimationCallback);
        } else if (drawable instanceof Animatable2Compat) {
            ((Animatable2Compat) drawable).registerAnimationCallback(mAnimationCallbackCompat);
        } else if (drawable instanceof AnimationDrawable) {
            ((AnimationDrawable) drawable).setOneShot(false);
        }

        ((Animatable) drawable).start();
    }

    private static void startLottieAnimationWith(LottieAnimationView illustrationView,
            Uri imageUri) {
        final InputStream inputStream =
                getInputStreamFromUri(illustrationView.getContext(), imageUri);
        illustrationView.setFailureListener(
                result -> Log.w(TAG, "Invalid illustration image uri: " + imageUri, result));
        illustrationView.setAnimation(inputStream, /* cacheKey= */ null);
        illustrationView.setRepeatCount(LottieDrawable.INFINITE);
        illustrationView.playAnimation();
    }

    private static void startLottieAnimationWith(LottieAnimationView illustrationView,
            @RawRes int rawRes) {
        illustrationView.setFailureListener(
                result -> Log.w(TAG, "Invalid illustration resource id: " + rawRes, result));
        illustrationView.setAnimation(rawRes);
        illustrationView.setRepeatCount(LottieDrawable.INFINITE);
        illustrationView.playAnimation();
    }

    private static void resetAnimations(LottieAnimationView illustrationView) {
        resetAnimation(illustrationView.getDrawable());

        illustrationView.cancelAnimation();
    }

    private static void resetAnimation(Drawable drawable) {
        if (!(drawable instanceof Animatable)) {
            return;
        }

        if (drawable instanceof Animatable2) {
            ((Animatable2) drawable).clearAnimationCallbacks();
        } else if (drawable instanceof Animatable2Compat) {
            ((Animatable2Compat) drawable).clearAnimationCallbacks();
        }

        ((Animatable) drawable).stop();
    }

    private static InputStream getInputStreamFromUri(Context context, Uri uri) {
        try {
            return context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            Log.w(TAG, "Cannot find content uri: " + uri, e);
            return null;
        }
    }

    private void init(Context context, AttributeSet attrs) {
        setLayoutResource(R.layout.illustration_preference);

        mIsAutoScale = false;
        if (attrs != null) {
            final TypedArray a = context.obtainStyledAttributes(attrs,
                    com.airbnb.lottie.R.styleable.LottieAnimationView, 0 /*defStyleAttr*/, 0 /*defStyleRes*/);
            mImageResId = a.getResourceId(com.airbnb.lottie.R.styleable.LottieAnimationView_lottie_rawRes, 0);
            a.recycle();
        }
    }
}
