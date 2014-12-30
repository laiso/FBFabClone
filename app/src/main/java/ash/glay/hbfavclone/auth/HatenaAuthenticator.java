package ash.glay.hbfavclone.auth;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import ash.glay.hbfavclone.LoginActivity;
import ash.glay.hbfavclone.R;
import ash.glay.hbfavclone.util.Constants;

/**
 * はてなIDを管理するAuthenticator
 */
public class HatenaAuthenticator extends AbstractAccountAuthenticator {

    final private Context mContext;

    public HatenaAuthenticator(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
        final Intent intent = new Intent(mContext, LoginActivity.class);
        final Bundle bundle = new Bundle();
        intent.putExtra(Constants.ACCOUNT_AUTHTOKEN_TYPE, authTokenType);
        intent.putExtra(Constants.ACCOUNT_RESPONSE, response);
        bundle.putParcelable(AccountManager.KEY_INTENT, intent);
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        if (!authTokenType.equals(Constants.ACCOUNT_AUTHTOKEN_TYPE)) {
            final Bundle failure = new Bundle();
            failure.putString(AccountManager.KEY_ERROR_MESSAGE, mContext.getString(R.string.invalid_auth_token));
            return failure;
        }

        final Bundle success = new Bundle();
        success.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
        success.putString(AccountManager.KEY_ACCOUNT_TYPE, Constants.ACCOUNT_TYPE);
        return success;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
        throw new UnsupportedOperationException();
    }
}
