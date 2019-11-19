package com.example.camerascan.imageeditor.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.camerascan.R;
import com.example.camerascan.imageeditor.ImageEditorIntentBuilder;
import com.example.camerascan.imageeditor.ModuleConfig;
import com.example.camerascan.imageeditor.fragment.crop.CropFragment;
import com.example.camerascan.imageeditor.fragment.paint.PaintFragment;


public class MainMenuFragment extends BaseEditFragment implements View.OnClickListener {
    public static final int INDEX = ModuleConfig.INDEX_MAIN;

    public static final String TAG = MainMenuFragment.class.getName();
    private View mainView;

    private View stickerBtn;

    private View cropBtn;
    private View rotateBtn;
    private View mTextBtn;
    private View mPaintBtn;

    private Bundle intentBundle;

    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_edit_image_main_menu,
                null);
        intentBundle = getArguments();
        return mainView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        stickerBtn = mainView.findViewById(R.id.btn_stickers);

        cropBtn = mainView.findViewById(R.id.btn_crop);
        rotateBtn = mainView.findViewById(R.id.btn_rotate);
        mTextBtn = mainView.findViewById(R.id.btn_text);
        mPaintBtn = mainView.findViewById(R.id.btn_paint);


        if (intentBundle.getBoolean(ImageEditorIntentBuilder.STICKER_FEATURE, false)) {
            stickerBtn.setVisibility(View.VISIBLE);
            stickerBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.FILTER_FEATURE, false)) {

        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.CROP_FEATURE, false)) {
            cropBtn.setVisibility(View.VISIBLE);
            cropBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.ROTATE_FEATURE, false)) {
            rotateBtn.setVisibility(View.VISIBLE);
            rotateBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.ADD_TEXT_FEATURE, false)) {
            mTextBtn.setVisibility(View.VISIBLE);
            mTextBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.PAINT_FEATURE, false)) {
            mPaintBtn.setVisibility(View.VISIBLE);
            mPaintBtn.setOnClickListener(this);
        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.BEAUTY_FEATURE, false)) {

        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.BRIGHTNESS_FEATURE, false)) {

        }

        if (intentBundle.getBoolean(ImageEditorIntentBuilder.SATURATION_FEATURE, false)) {

        }
    }

    @Override
    public void onShow() {
        // do nothing
    }

    @Override
    public void backToMain() {
        //do nothing
    }

    @Override
    public void onClick(View v) {
        if (v == stickerBtn) {
            onStickClick();
        } else if (v == cropBtn) {
            onCropClick();
        } else if (v == rotateBtn) {
            onRotateClick();
        } else if (v == mTextBtn) {
            onAddTextClick();
        } else if (v == mPaintBtn) {
            onPaintClick();
        }
    }

    private void onStickClick() {
        activity.bottomGallery.setCurrentItem(StickerFragment.INDEX);
        activity.stickerFragment.onShow();
    }


    private void onCropClick() {
        activity.bottomGallery.setCurrentItem(CropFragment.INDEX);
        activity.cropFragment.onShow();
    }

    private void onRotateClick() {
        activity.bottomGallery.setCurrentItem(RotateFragment.INDEX);
        activity.rotateFragment.onShow();
    }


    private void onAddTextClick() {
        activity.bottomGallery.setCurrentItem(AddTextFragment.INDEX);
        activity.addTextFragment.onShow();
    }

    private void onPaintClick() {
        activity.bottomGallery.setCurrentItem(PaintFragment.INDEX);
        activity.paintFragment.onShow();
    }


}
