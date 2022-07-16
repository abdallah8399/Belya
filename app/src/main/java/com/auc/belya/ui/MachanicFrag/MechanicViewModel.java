package com.auc.belya.ui.MachanicFrag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MechanicViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MechanicViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is MachanicFrag fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}