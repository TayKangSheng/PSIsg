package co.kopigao.psisg.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.kopigao.psisg.R;

public class AlertDialogFragment extends DialogFragment {

    public static AlertDialogFragment newInstance(String message, String positive, String negative) {
        Log.d("PSIDebug", "AlertDialogFragment newInstance");

        AlertDialogFragment fragment = new AlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("positive", positive);
        args.putString("negative", negative);
        fragment.setArguments(args);
        return fragment;
    }

    private String message;
    private String positive_message;
    private String negative_message;
    private AlertDialogListener mListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d("PSIDebug", "AlertDialogFragment onCreate");

        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            message = getArguments().getString("message");
            positive_message = getArguments().getString("positive");
            negative_message = getArguments().getString("negative");
        }
    }

    @Override
    public void onAttach(Context context) {
        Log.d("PSIDebug", "AlertDialogFragment onAttach");

        super.onAttach(context);
        if (context instanceof AlertDialogListener) {
            mListener = (AlertDialogListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.d("PSIDebug", "AlertDialogFragment onCreateDialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog, null);

        ( (TextView) dialogView.findViewById(R.id.dialog_text) ).setText(message);

        builder.setView(dialogView);
//        builder.setMessage(message);
        builder.setPositiveButton(positive_message, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                mListener.onDialogPositiveClick(AlertDialogFragment.this);
            }
        });
        builder.setNegativeButton(negative_message, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){
                mListener.onDialogNegativeClick(AlertDialogFragment.this);
            }
        });

        return builder.create();
    }

    public interface AlertDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

}
