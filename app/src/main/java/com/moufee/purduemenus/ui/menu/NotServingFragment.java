package com.moufee.purduemenus.ui.menu;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moufee.purduemenus.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotServingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotServingFragment extends Fragment {

    private static final String ARG_MESSAGE = "com.moufee.purduemenus.notservingfragment.message";
    private String mMessage;

    public NotServingFragment() {
        // Required empty public constructor
    }

    public static NotServingFragment newInstance(String message) {
        NotServingFragment fragment = new NotServingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
                return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null)
//            mMessage = savedInstanceState.getString(ARG_MESSAGE, "Not Serving");
        Bundle args = getArguments();
        if (args != null)
            mMessage = args.getString(ARG_MESSAGE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_not_serving, container, false);
        TextView textView = (TextView) view.findViewById(R.id.not_serving_message_textview);
        if (mMessage != null)
        textView.setText(mMessage);

        return view;
    }

}
