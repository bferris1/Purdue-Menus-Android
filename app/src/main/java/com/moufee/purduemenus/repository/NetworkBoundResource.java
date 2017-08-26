package com.moufee.purduemenus.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.moufee.purduemenus.util.Resource;

import retrofit2.Response;

/**
 * Created by Ben on 13/08/2017.
 */

public abstract class NetworkBoundResource<ResultType, RequestType> {

    private final MediatorLiveData<Resource<ResultType>> result = new MediatorLiveData<>();


    NetworkBoundResource() {
        //initialize result to an empty resource of the correct type with loading status
        result.setValue(Resource.<ResultType>loading(null));
        //get from local source
        final LiveData<ResultType> localSource = loadFromFile();
        result.addSource(localSource, new Observer<ResultType>() {
            @Override
            public void onChanged(@Nullable ResultType data) {
                //stop watching, file data won't change
                result.removeSource(localSource);
                if (shouldFetch(data))
                    fetchFromNetwork(localSource);
                else result.addSource(localSource, new Observer<ResultType>() {
                    @Override
                    public void onChanged(@Nullable ResultType resultType) {
                        result.setValue(Resource.success(resultType));
                    }
                });

            }
        });

    }

    private void fetchFromNetwork(final LiveData<ResultType> localSource){
        final LiveData<Response<RequestType>> apiResponse = createCall();
        result.addSource(localSource, new Observer<ResultType>() {
            @Override
            public void onChanged(@Nullable ResultType resultType) {
                result.setValue(Resource.loading(resultType));
            }
        });
        result.addSource(apiResponse, new Observer<Response<RequestType>>() {
            @Override
            public void onChanged(@Nullable Response<RequestType> requestTypeResponse) {
                result.removeSource(localSource);
                result.removeSource(apiResponse);
                //here you would check the response and save it to the database
                //and add back the local/database source to result
                //which would update when saving is complete, setting result to a successful Resource
            }
        });

    }




    protected void onFetchFailed() {
    }

    public LiveData<Resource<ResultType>> asLiveData() {
        return result;
    }

    @WorkerThread
    protected RequestType processResponse(Response<RequestType> response) {
        return response.body();
    }

    @WorkerThread
    protected abstract void saveCallResult(@NonNull RequestType item);

    @MainThread
    protected abstract boolean shouldFetch(@Nullable ResultType data);

    @NonNull
    @MainThread
    protected abstract LiveData<ResultType> loadFromFile();

    @NonNull
    @MainThread
    protected abstract LiveData<Response<RequestType>> createCall();
}
