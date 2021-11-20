package tech.hazm.hazmandroid.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import tech.hazm.hazmandroid.Activity.MainActivity;
import tech.hazm.hazmandroid.Base.BaseFragment;
import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Constant.Constant;
import tech.hazm.hazmandroid.Constant.PrefConst;
import tech.hazm.hazmandroid.OfflineWebview.SaveService;

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.hazm.hazmandroid.R;

public class HelpFragment extends BaseFragment {

    MainActivity mainActivity;

    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.txvNoInternet) TextView txvNoInternet;

    String url = "http://hazm.tech/mobile/app/help.html";

    public HelpFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @SuppressLint("JavascriptInterface")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View frm = LayoutInflater.from(mainActivity).inflate(R.layout.frm_help, container, false);
        ButterKnife.bind(this, frm);

        if (Common.isInternetAvailable){

            webView.setVisibility(View.VISIBLE);
            txvNoInternet.setVisibility(View.GONE);

            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);

        }else {
            webView.setVisibility(View.GONE);
            txvNoInternet.setVisibility(View.VISIBLE);
        }



        /*if (Common.isInternetAvailable){
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadUrl(url);
            startSave(url);
        }else {
            showOfflineWebview();
        }*/


        return frm;
    }

    private void showOfflineWebview() {
        showProgress();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void startSave(String url) {
        Intent intent = new Intent(mainActivity, SaveService.class);
        intent.putExtra(Intent.EXTRA_TEXT, url);
        intent.putExtra(Constant.FILE_PATH, PrefConst.FILE_PATH_HELP_WEBVIEW);
        mainActivity.startService(intent);
    }
}
