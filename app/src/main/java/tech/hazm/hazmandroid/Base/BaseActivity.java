package tech.hazm.hazmandroid.Base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import tech.hazm.hazmandroid.R;

public class BaseActivity extends AppCompatActivity {

    private ProgressDialog progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
    }

    public void showToast(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void showProgress(String msg){
        if (progressBar.isShowing()) return;
        progressBar.setTitle(getString(R.string.wait));
        progressBar.setMessage(msg);
        progressBar.show();
    }

    public void closeProgress(){
        if (progressBar.isShowing()){
            progressBar.dismiss();
        }

    }

    public void showAlertDialog(String title, String msg){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.ok),

                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }
}
