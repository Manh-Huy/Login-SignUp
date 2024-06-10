package com.example.authenticationuseraccount.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.authenticationuseraccount.R;
import com.example.authenticationuseraccount.api.ApiService;
import com.example.authenticationuseraccount.common.ErrorUtils;
import com.example.authenticationuseraccount.common.LogUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Field;

public class PremiumActivity extends AppCompatActivity {
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Button btnPayment;
    private String paymentIntentClientSecret;
    private PaymentSheet paymentSheet;
    private RadioGroup pricingOptions;
    private int mAmount;
    private String paymentType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_premium);
        btnPayment = findViewById(R.id.buyVipButton);
        pricingOptions = findViewById(R.id.pricingOptions);
        pricingOptions.clearCheck();
        mAmount = 0;
        pricingOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // checkedId is the RadioButton ID that is selected
                switch (checkedId) {
                    case R.id.yearlyOption:
                        mAmount = 1799;
                        paymentType = "Yearly";
                        fetchPaymentIntent(mAmount);
                        break;
                    case R.id.monthlyOption:
                        mAmount = 299;
                        paymentType = "Monthly";
                        fetchPaymentIntent(mAmount);
                        break;
                }
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pricingOptions.getCheckedRadioButtonId() != -1) {
                    onPayClicked();
                }else{
                    ErrorUtils.showError(PremiumActivity.this,"Please choose your plan!");
                }

            }
        });
        //btnPayment.setEnabled(false);
        paymentSheet = new PaymentSheet(this, this::onPaymentSheetResult);


    }

    private void onPayClicked() {
        PaymentSheet.Configuration configuration = new PaymentSheet.Configuration.Builder("Example, Inc.")
                .build();

        // Present Payment Sheet
        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration);
    }

    private void fetchPaymentIntent(int amount) {
        ApiService.apiService.addItemsToCart(amount).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    showAlert("Failed to load page", "Error: " + response.toString());
                } else {
                    final JSONObject responseJson = parseResponse(response.body());
                    paymentIntentClientSecret = responseJson.optString("clientSecret");
                    btnPayment.setEnabled(true);
                    LogUtils.ApplicationLogI("Retrieved PaymentIntent: " + paymentIntentClientSecret);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                showAlert("Failed to load data", "Error: " + t.toString());
            }
        });
    }

    private void onPaymentSheetResult(final PaymentSheetResult paymentSheetResult) {
        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {
            ErrorUtils.showError(this, "Payment complete!");
            // api upgrade user premium
            upgradePremium(user.getUid(), paymentType, mAmount);
            finish();
        } else if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {
            ErrorUtils.showError(this, "Payment canceled!");
        } else if (paymentSheetResult instanceof PaymentSheetResult.Failed) {
            Throwable error = ((PaymentSheetResult.Failed) paymentSheetResult).getError();
            showAlert("Payment failed", error.getLocalizedMessage());
        }
    }

    private void showAlert(String title, @Nullable String message) {
        runOnUiThread(() -> {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("Ok", null)
                    .create();
            dialog.show();
        });
    }

    private JSONObject parseResponse(ResponseBody responseBody) {
        if (responseBody != null) {
            try {
                return new JSONObject(responseBody.string());
            } catch (IOException | JSONException e) {
                LogUtils.ApplicationLogE("Error parsing response: " + e);
            }
        }
        return new JSONObject();
    }

    @SuppressLint("CheckResult")
    private void upgradePremium(String userID, String paymentType, int balance) {
        ApiService.apiService.upgradePremium(userID, paymentType, balance)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    LogUtils.ApplicationLogD("Succefully");
                }, throwable -> {
                    LogUtils.ApplicationLogE("Failed");
                });
    }
}