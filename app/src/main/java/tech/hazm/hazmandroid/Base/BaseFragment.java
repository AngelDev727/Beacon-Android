package tech.hazm.hazmandroid.Base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import tech.hazm.hazmandroid.R;

public class BaseFragment extends Fragment {
    public Context context;
    private ProgressDialog progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        progressBar = new ProgressDialog(context, R.style.MyTheme);
        progressBar.setCancelable(false);
    }

    public void showProgress(){
        if (progressBar.isShowing()) return;
        progressBar.show();
    }

    public void showToast(String msg){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void serverFailed(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                closeProgress();
                showToast(getString(R.string.serverFailed));
            }
        });
    }

    public void closeProgress(){
        if (progressBar.isShowing()){
            progressBar.dismiss();
        }

    }
}
