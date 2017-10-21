package com.epicqueststudios.bvtwitter.base.viewmodel;


public abstract class BaseViewModel<ITEM_T>  extends AbstractViewModel{
    protected ITEM_T item;

    public BaseViewModel(State savedInstanceState) {
        super(savedInstanceState);
    }

    public void setItem(ITEM_T item){
        this.item = item;
    }
}
