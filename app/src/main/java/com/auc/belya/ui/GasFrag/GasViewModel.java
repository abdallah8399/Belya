package com.auc.belya.ui.GasFrag;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GasViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GasViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is GasFrag fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}