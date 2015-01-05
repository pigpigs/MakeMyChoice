package com.pewpewpew.user.makemychoice;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by User on 05/11/14.
 */
public class CommentDialogFragment extends DialogFragment {
    private static final String TAG = "CommentDialogFragment_debug";
    public static String KEY_REPLY = "reply_key";
    private String mReplyTarget; // "Post or xxx's comment
    private Post mPost;
    //test
    /**
     * DialogFragment that takes in the user it replies to as an argument and set "@ user" as the default text,
     * or replies to the main post
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReplyTarget = getArguments().getString(KEY_REPLY);

        // Post if not replying to user, else someone's comment
        // FUTURE - implement replying to other user's comment
//        mReplyTarget = mReplyTarget .equals("")? "Post":mReplyTarget+"'s comment";

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View v = View.inflate(getActivity(), R.layout.fragment_comment, null);
        final EditText replyEditText = (EditText)v.findViewById(R.id.comment_reply);
        builder.setView(v)
                .setTitle(String.format("Replying to %s.", "Post"))
                .setPositiveButton(R.string.fragment_dialog_reply, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String content = replyEditText.getText().toString();
                        // note- dont have to check for size or wtv here, truncate in the listview and make them expandable

                        Log.i(TAG, "Content: " + content);

                        Intent intent = new Intent().putExtra(KEY_REPLY, content);
                        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
                        Toast.makeText(getActivity(), "Reply: " + content, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.fragment_dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //  TODO- check for content, ask for confirmation, then exit?
                        dismiss();
                    }
                });

        return builder.create();
    }
}
