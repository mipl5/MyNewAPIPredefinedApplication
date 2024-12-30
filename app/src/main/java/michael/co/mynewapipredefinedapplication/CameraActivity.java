package michael.co.mynewapipredefinedapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private ImageView       ivPicture;
    private MaterialButton  btnDoCamera;
    private MaterialButton  btnReturn;

    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_camera);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        initializeViews();
        initializeLaunchers();
    }

    private void initializeLaunchers() {
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicturePreview(),
                new ActivityResultCallback<Bitmap>() {
                    @Override
                    public void onActivityResult(Bitmap o) {
                        ivPicture.setImageBitmap(o);
                    }
                });
        galleryLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult o) {
                        if (o.getResultCode() == RESULT_OK && o.getData() != null){
                            Uri imageUri = o.getData().getData();
                            try {
                                ivPicture.setImageBitmap(uriToBitmap(imageUri));
                            }
                            catch (Exception ignored) {}
                        }
                    }
                });
    }

    private Bitmap uriToBitmap(Uri uri) throws IOException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P){
            return ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
        } else{
            return MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
        }
    };

    private void initializeViews() {
        ivPicture =         (ImageView)findViewById(R.id.ivPicture);
        btnDoCamera =       (MaterialButton)findViewById(R.id.btnDoCamera);
        btnReturn =         (MaterialButton)findViewById(R.id.btnReturn);
        initializeOnClickListeners();
    }

    private void initializeOnClickListeners() {
        btnDoCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialAlertDialogBuilder(CameraActivity.this)
                        .setTitle("Picture")
                        .setMessage("Take picture")
                        .setPositiveButton("Camera", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cameraLauncher.launch(null);
                            }
                        })
                        .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                galleryLauncher.launch(galleryIntent);
                            }
                        }).setNeutralButton("Cancel", null).setCancelable(true).show();
            }
        });
        btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CameraActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}