package com.example.pruebavision.ui.RepPrivado;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;
    private MutableLiveData<String> prueba;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public void setText(String input)
    {
        prueba = new MutableLiveData<>();
        prueba.setValue(input);
    }
    public LiveData<String> getTextPrueba() {
        return prueba;
    }

    public LiveData<String> getText() {
        return mText;
    }
}