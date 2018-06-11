package com.shubhammobiles.shubhammobiles.Sharing;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.shubhammobiles.shubhammobiles.R;
import com.shubhammobiles.shubhammobiles.util.Constants;

public class ShareActivity extends AppCompatActivity {

    private Bundle bundle;
    private String brandKey;
    private String brandName;

    private String accountKey;
    private String accountName;

    private String action;

    public static ShareActivity getInstance(){
        return new ShareActivity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        Intent intent = this.getIntent();
        action = intent.getAction();
        bundle = new Bundle();

        if (action != null && action.equals(Constants.SHARE_STOCK)){
            brandKey = intent.getStringExtra(Constants.KEY_BRAND_KEY);
            brandName = intent.getStringExtra(Constants.KEY_BRAND_NAME);

            bundle.putString(Constants.KEY_BRAND_KEY, brandKey);
            bundle.putString(Constants.KEY_BRAND_NAME, brandName);

            ShareStock shareStock = new ShareStock();
            shareStock.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.share_fragment, shareStock)
                    .commit();

        } else if (action != null && action.equals(Constants.SHARE_ACCOUNTS)){
            accountKey = intent.getStringExtra(Constants.KEY_ACCOUNT_KEY);
            accountName = intent.getStringExtra(Constants.KEY_ACCOUNT_NAME);

            bundle.putString(Constants.KEY_ACCOUNT_KEY, accountKey);
            bundle.putString(Constants.KEY_ACCOUNT_NAME, accountName);

            ShareAccounts shareAccounts = new ShareAccounts();
            shareAccounts.setArguments(bundle);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.share_fragment, shareAccounts)
                    .commit();
        }


    }
}
