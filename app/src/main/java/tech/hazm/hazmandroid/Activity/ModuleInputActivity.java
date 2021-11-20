package tech.hazm.hazmandroid.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.iamhabib.easy_preference.EasyPreference;
import tech.hazm.hazmandroid.Base.BaseActivity;
import tech.hazm.hazmandroid.Common.Common;
import tech.hazm.hazmandroid.Constant.PrefConst;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import tech.hazm.hazmandroid.R;

public class ModuleInputActivity extends BaseActivity {

    @BindView(R.id.btnActive) Button btnActive;
    @BindView(R.id.edtUuid) EditText edtUuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_input);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnActive) void gotoMainActivity(){
        String uuid = edtUuid.getText().toString().trim();
        if (isValidUUID(uuid)){
            Common.uuid = uuid;
            EasyPreference.with(this).addString(PrefConst.UUID, uuid).save();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }else {
            edtUuid.setText("");
        }
    }

    private boolean isValidUUID(String uuid){
        /*if (uuid.length() != 32 ) {
            showToast(getString(R.string.uuid_length_wrong));
            return false;
        }

        if (uuid.split("-").length != 5){
            showToast(getString(R.string.uuid_group_wrong));
            return false;
        }*/

        return true;
    }
}
