package com.technicalround.iskconsadhanareport;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentInboxMessage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentInboxMessage#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentInboxMessage extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private int NUM_OF_ROWS = 10;//For friends

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private LinearLayout linearLayoutMessages;
    private Context context;
    private LayoutInflater inflater;
    private View viewFragMessageList;

    public FragmentInboxMessage() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentMessagesList.
     */
    public static FragmentInboxMessage newInstance(String param1, String param2) {
        FragmentInboxMessage fragment = new FragmentInboxMessage();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater_1, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        inflater = inflater_1;
        viewFragMessageList = inflater.inflate(R.layout.fragment_inbox_message, container, false);
        context = getContext();
        try {
            //Fetch Messages list
            linearLayoutMessages = (LinearLayout) viewFragMessageList.findViewById(R.id.linearLayoutMessages);
            if (linearLayoutMessages != null) {
                buildMessageList(linearLayoutMessages);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return viewFragMessageList;
    }

    // You can Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction("");
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void buildMessageList(LinearLayout linearLayoutMessages) {
        //Fetch juniors list
        if (BuildConfig.DEBUG) Log.d("ISS", "In buildMessageList under CreateView");
        linearLayoutMessages.removeAllViews();
        try {

            MessageEntry[] outMessageEntry = null; /*= new MessageEntry[NUM_OF_ROWS];
            for(int i=0; i<NUM_OF_ROWS; i++) {
                outMessageEntry[i] = new MessageEntry();
            }*/
            outMessageEntry = DatabaseActions.queryInboxMessageEntries(AppPreferences.user.id, outMessageEntry);
            if (outMessageEntry == null) return;
            for (int i = 0; i < outMessageEntry.length; i++) {
                addMessageInfoFrame(linearLayoutMessages, outMessageEntry[i]);
            }
        } catch (Exception e) {
            Log.d("ISS", e.toString());
            e.printStackTrace();
        }
    }

    private void addMessageInfoFrame(LinearLayout linearLayoutMessages, MessageEntry message) throws Exception {

        //Log.d("ISS", "TableRow Message Entry Add:isJunior:" + isJunior);
        View frameView = inflater.inflate(R.layout.layout_message_info, linearLayoutMessages, false);

        boolean isReportingActive = false;

        //Log.d("ISS", message.name);
        TextView textView = (TextView) frameView.findViewById(R.id.textViewSndrName);
        textView.setText(message.sndrName);

        textView = (TextView) frameView.findViewById(R.id.textViewMsg);
        textView.setText("::" + message.msg);

        textView = (TextView) frameView.findViewById(R.id.textViewDate);
        textView.setText("Date: " + ActivityMainDevotee.printDateFromDateIndex(message.date));

        linearLayoutMessages.addView(frameView);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // You can Update argument type and name
        public void onFragmentInteraction(String response);
    }

}
