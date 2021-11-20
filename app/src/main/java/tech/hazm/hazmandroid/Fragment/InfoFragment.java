package tech.hazm.hazmandroid.Fragment;

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

import butterknife.BindView;
import butterknife.ButterKnife;
import tech.hazm.hazmandroid.R;

public class InfoFragment extends BaseFragment {

    MainActivity mainActivity;
    @BindView(R.id.webView) WebView webView;
    @BindView(R.id.txvNoInternet) TextView txvNoInternet;

    String url = "http://hazm.tech/mobile/app/info.html";

    public InfoFragment(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View frm = inflater.inflate(R.layout.frm_info, container, false);
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

        return frm;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
