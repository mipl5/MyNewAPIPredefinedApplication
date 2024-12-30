package michael.co.mynewapipredefinedapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

public class PhoneCallActivity extends AppCompatActivity {
    private TextInputEditText       etGetPhoneNumber;
    private MaterialButton          btnDoCall;
    private MaterialButton          btnReturn;

    private ActivityResultLauncher<Intent> phoneCallLauncher;
    private ActivityResultLauncher<String> requestPermissionLauncher;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_phone_call);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initializeLaunchers();
        initializeViews();
    }

    private void checkPermissionCall() {
        if (ContextCompat.checkSelfPermission(
                PhoneCallActivity.this, android.Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED)
        {
            initiatePhoneCall();
        } else{
            if (shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)){
                showPermissionRationaleDialog();
            }
            else{
                requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE);
            }
        }
    }

    private void showPermissionRationaleDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Permission Request")
                .setMessage("This app needs permission to call directly. " +
                        "without permission we can't do so")
                .setPositiveButton("OK", (dialog, which) ->
                        requestPermissionLauncher.launch(Manifest.permission.CALL_PHONE))
                .setNegativeButton("Cancel", (dialog, which) ->
                        Toast.makeText(this, "denied", Toast.LENGTH_SHORT).show()).show();
    }

    private void initiatePhoneCall() {
        String phoneNumber = Objects.requireNonNull(etGetPhoneNumber.getText()).toString();
        if (!phoneNumber.isEmpty()){
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + phoneNumber));
            phoneCallLauncher.launch(i);
        }
        else{
            Toast.makeText(this, "provide phone number", Toast.LENGTH_SHORT).show();

        }    }

    private void initializeLaunchers() {
        phoneCallLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {

                    }
                }
        );
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean o) {
                        if (o){
                            initiatePhoneCall();
                        }
                        else{
                            Toast.makeText(PhoneCallActivity.this, "permission denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    private void initializeViews() {
        etGetPhoneNumber = (TextInputEditText)findViewById(R.id.etGetPhoneNumber);
        btnDoCall =        (MaterialButton)findViewById(R.id.btnDoCall);
        btnReturn =        (MaterialButton)findViewById(R.id.btnReturn);
        initializeListeners();
    }

    private void initializeListeners() {
        btnDoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermissionCall();
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PhoneCallActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}