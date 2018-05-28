package com.moufee.purduemenus.menus;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import com.moufee.purduemenus.repository.FavoritesRepository;
import com.moufee.purduemenus.repository.MenuRepository;
import com.moufee.purduemenus.util.DateTimeHelper;
import com.moufee.purduemenus.util.Resource;

import org.joda.time.DateTime;

import java.util.Set;

import javax.inject.Inject;


/**
 * A ViewModel representing all the dining menus for one day.
 */

public class DailyMenuViewModel extends ViewModel {


    private MenuRepository mMenuRepository;
    private FavoritesRepository mFavoritesRepository;
    private final MutableLiveData<DateTime> mCurrentDate = new MutableLiveData<>();
    private final MutableLiveData<Integer> mSelectedMealIndex = new MutableLiveData<>();
    private final LiveData<Set<String>> mFavoriteSet;
    private final LiveData<Resource<FullDayMenu>> mFullMenu = Transformations.switchMap(mCurrentDate, new Function<DateTime, LiveData<Resource<FullDayMenu>>>() {
        @Override
        public LiveData<Resource<FullDayMenu>> apply(DateTime input) {
            return mMenuRepository.getMenus(input);
        }
    });

    private final MediatorLiveData<Resource<FullDayMenu>> updatedMenu = new MediatorLiveData<>();

    @Inject
    public DailyMenuViewModel(MenuRepository menuRepository, FavoritesRepository favoritesRepository) {
        mMenuRepository = menuRepository;
        mFavoritesRepository = favoritesRepository;
        mFavoriteSet = mFavoritesRepository.getFavoriteIDSet();
        mSelectedMealIndex.setValue(DateTimeHelper.getCurrentMealIndex());
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

    public LiveData<Set<String>> getFavoriteSet() {
        return mFavoriteSet;
    }

    public void currentDay() {
        mCurrentDate.setValue(new DateTime());
    }
}
