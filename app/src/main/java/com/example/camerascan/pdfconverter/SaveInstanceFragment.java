package com.example.camerascan.pdfconverter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

public class SaveInstanceFragment extends Fragment {
    /**
     * Fragment đóng vai trò lưu giữ Instance <-- Lưu giữ instance ảnh có thể nặng, vượt quá giới
     * hạn bundle SavedInstanceState (~1MB)
     */

    private static final String TAG = "SavedInstanceFragment";
    private static Bundle mInstanceBundle = null;

    public SaveInstanceFragment() { // This will only be called once be cause of setRetainInstance()
        super();
        setRetainInstance(true);
    }

    //lưu giữ instance
    public SaveInstanceFragment pushData(Bundle instanceState) {
        if (mInstanceBundle == null) {
            mInstanceBundle = instanceState;
        } else {
            mInstanceBundle.putAll(instanceState);
        }
        return this;
    }

    //lấy instance được lưu
    public Bundle popData() {
        Bundle out = mInstanceBundle;
        mInstanceBundle = null;
        return out;
    }

    public static final SaveInstanceFragment getInstance(FragmentManager fragmentManager) {
        SaveInstanceFragment out = (SaveInstanceFragment) fragmentManager.findFragmentByTag(TAG);

        if (out == null) {
            out = new SaveInstanceFragment();
            fragmentManager.beginTransaction().add(out, TAG).commit();
        }
        return out;
    }
}
