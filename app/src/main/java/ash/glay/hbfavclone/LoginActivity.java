package ash.glay.hbfavclone;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.regex.Pattern;

import ash.glay.hbfavclone.util.Constants;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends AccountAuthenticatorActivity {

    @InjectView(R.id.input_id)
    EditText mInputedId;

    @InjectView(R.id.alert)
    TextView mAlertText;
    @InjectView(R.id.alert_image)
    ImageView mAlertImage;

    boolean mHasError = false;

    // ユーザーIDとして使える文字列
    final private static Pattern PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");

    final private InputFilter FILTER = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (mHasError) {
                mInputedId.setHint(getString(R.string.input_hatena_id));
                mAlertText.setVisibility(View.INVISIBLE);
                mAlertImage.setVisibility(View.INVISIBLE);
                mHasError = false;
            }

            if (PATTERN.matcher(source).matches()) {
                return source;
            } else {
                return "";
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);

        mAlertText.setVisibility(View.INVISIBLE);
        mAlertImage.setVisibility(View.INVISIBLE);

        // 入力フィルタを設定
        InputFilter[] filters = new InputFilter[mInputedId.getFilters().length + 1];
        System.arraycopy(mInputedId.getFilters(), 0, filters, 0, mInputedId.getFilters().length);
        filters[mInputedId.getFilters().length] = FILTER;
        mInputedId.setFilters(filters);
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.logon)
    void onLogon() {
        final String userId = mInputedId.getText().toString();
        if (userId.matches("[a-zA-Z][a-zA-Z0-9_-]{1,30}[a-zA-Z0-9]")) {
            //TODO:はてなIDが存在するかどうかの判定作れるなら入れる
            final Account account = new Account(userId, Constants.ACCOUNT_TYPE);
            AccountManager.get(this).addAccountExplicitly(account, null, null);

            final Intent intent = new Intent();
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, userId);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();

        } else {
            // エラーメッセージを表示
            mInputedId.setText("");
            mInputedId.setHint("");
            mAlertText.setVisibility(View.VISIBLE);
            mAlertImage.setVisibility(View.VISIBLE);
            mHasError = true;
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cancel)
    void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
