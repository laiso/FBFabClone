package ash.glay.hbfavclone;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

import java.util.regex.Pattern;

import ash.glay.hbfavclone.util.Constants;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends AccountAuthenticatorActivity {

    @InjectView(R.id.input_id)
    EditText mInputedId;

    // ユーザーIDとして使える文字列
    final private static Pattern PATTERN = Pattern.compile("[a-zA-Z0-9_-]+");

    final private static InputFilter FILTER = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
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
            final Account account = new Account(userId, Constants.ACCOUNT_TYPE);
            AccountManager.get(this).addAccountExplicitly(account, null, null);

            final Intent intent = new Intent();
            intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, userId);
            intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            finish();

        } else {
            // エラー
        }
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.cancel)
    void onCancel() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
