package com.moufee.purduemenus.menus;

import android.app.Application;
import android.arch.core.util.Function;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;

import com.moufee.purduemenus.repository.MenuRepository;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;


/**
 * Created by Ben on 22/07/2017.
 * A ViewModel representing all the dining menus for one day.
 */

public class DailyMenuViewModel extends AndroidViewModel {


    private MenuRepository mMenuRepository;
    private final MutableLiveData<DateTime> mCurrentDate = new MutableLiveData<>();
    private final MutableLiveData<Integer> mSelectedMealIndex = new MutableLiveData<>();
    private final LiveData<Resource<FullDayMenu>> mFullMenu = Transformations.switchMap(mCurrentDate, new Function<DateTime, LiveData<Resource<FullDayMenu>>>() {
        @Override
        public LiveData<Resource<FullDayMenu>> apply(DateTime input) {
            return mMenuRepository.getMenus(DailyMenuViewModel.this.getApplication(), input);
        }
    });

    public DailyMenuViewModel(Application application) {
        super(application);
        this.mMenuRepository = MenuRepository.get();
        mSelectedMealIndex.setValue(0);
        setDate(new DateTime());

    }

    public LiveData<Integer> getSelectedMealIndex() {
        return mSelectedMealIndex;
    }

    public void setSelectedMealIndex(int index) {
        mSelectedMealIndex.setValue(index);
    }


    public LiveData<Resource<FullDayMenu>> getFullMenu() {
        return mFullMenu;
    }

    public void setDate(DateTime date) {
        mCurrentDate.setValue(date);
    }

    public LiveData<DateTime> getCurrentDate() {
        return mCurrentDate;
    }

    public void nextDay() {
        if (mCurrentDate.getValue() != null)
            mCurrentDate.setValue(mCurrentDate.getValue().plusDays(1));
    }

    public void previousDay() {
        if (mCurrentDate.getValue() != null)
            mCurrentDate.setValue(mCurrentDate.getValue().plusDays(-1));
    }

    public void currentDay() {
        mCurrentDate.setValue(new DateTime());
    }
}
