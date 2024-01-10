package com.androidmarket.pdfcreator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import androidmarket.R;
import butterknife.ButterKnife;

import com.androidmarket.pdfcreator.Constants;
import com.androidmarket.pdfcreator.fragment.ImageToPdfFragment;
import com.androidmarket.pdfcreator.util.FileUtils;
import com.androidmarket.pdfcreator.util.StringUtils;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static com.androidmarket.pdfcreator.Constants.pdfDirectory;

public class ActivityCropImage extends AppCompatActivity {

    private int mCurrentImageIndex = 0;
    private ArrayList<String> mImages;
    private final HashMap<Integer, Uri> mCroppedImageUris = new HashMap<>();
    private boolean mCurrentImageEdited = false;
    private boolean mFinishedClicked = false;
    private CropImageView mCropImageView;
//    ImageView imageView;
//    Button button;
    ImageView back,done;
    TextView skip;
//    Uri newUri;
//    ActivityResultLauncher<CropImageContractOptions> cropImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_activity);
        ButterKnife.bind(this);

//        MaterialToolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        mCropImageView = findViewById(R.id.cropImageView);
//        imageView = findViewById(R.id.crop_image);
//        button = findViewById(R.id.btn);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done_cropping);
        skip = findViewById(R.id.skip);

//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Drawable drawable = imageView.getDrawable();
//
//                // If the drawable is a BitmapDrawable, get the Bitmap
//                Bitmap bitmap = getBitmapFromDrawable(drawable);
//
//                // If the bitmap is not null, proceed with cropping
//                if (bitmap != null) {
//                    // Get the Uri from the Bitmap
//                    Uri imageUri = getImageUri(ActivityCropImage.this, bitmap);
//
//                    // Start cropping with the obtained Uri
//                    startCrop(imageUri);
//                }
//            }
//        });
//
//        cropImage = registerForActivityResult(new CropImageContract(), result -> {
//            if (result.isSuccessful()) {
//                newUri = result.getUriContent();
//                imageView.setImageURI(newUri);
//            }
//        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFinishedClicked = true;
                cropButtonClicked();
//                setUpCropImageView();
            }
        });

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrentImageEdited = false;
                nextImageClicked();
            }
        });

        setUpCropImageView();

        mImages = ImageToPdfFragment.mImagesUri;
        mFinishedClicked = false;

        for (int i = 0; i < mImages.size(); i++)
            mCroppedImageUris.put(i, Uri.fromFile(new File(mImages.get(i))));

        if (mImages.size() == 0)
            finish();

        setImage(0);
        Button cropImageButton = findViewById(R.id.cropButton);
        cropImageButton.setOnClickListener(view -> cropButtonClicked());

        Button rotateButton = findViewById(R.id.rotateButton);
        rotateButton.setOnClickListener(view -> rotateButtonClicked());

        ImageView nextImageButton = findViewById(R.id.nextimageButton);
        nextImageButton.setOnClickListener(view -> nextImageClicked());
        ImageView previousImageButton = findViewById(R.id.previousImageButton);
        previousImageButton.setOnClickListener(view -> prevImgBtnClicked());
    }

//    private Uri getImageUri(Context context, Bitmap bitmap) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
//        return Uri.parse(path);
//    }
//
//    private Bitmap getBitmapFromDrawable(Drawable drawable) {
//        if (drawable instanceof BitmapDrawable) {
//            return ((BitmapDrawable) drawable).getBitmap();
//        } else if (drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
//            // If it's not a BitmapDrawable, but it has intrinsic dimensions, create a Bitmap
//            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bitmap);
//            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
//            drawable.draw(canvas);
//            return bitmap;
//        }
//        return null;
//    }
//
//    private void startCrop(Uri selectedImageUri) {
//        CropImageOptions options = new CropImageOptions();
//        options.guidelines = CropImageView.Guidelines.ON;
//        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(selectedImageUri, options);
//        cropImage.launch(cropImageContractOptions);
//    }

    public void cropButtonClicked() {
        mCurrentImageEdited = false;
        String root = Environment.getExternalStorageDirectory().toString();
        File folder = new File(root + pdfDirectory);
        Uri uri = mCropImageView.getImageUri();
//        Uri uri = newUri;

        if (uri == null) {
            StringUtils.getInstance().showSnackbar(this, R.string.error_uri_not_found);
            return;
        }

        String path = uri.getPath();
        String filename = "cropped_im";
        if (path != null)
            filename = "cropped_" + FileUtils.getFileName(path);

        File file = new File(folder, filename);
        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();

        mCropImageView.saveCroppedImageAsync(Uri.fromFile(file));
    }

    public void rotateButtonClicked() {
        mCurrentImageEdited = true;
        mCropImageView.rotateImage(90);
    }

    public void nextImageClicked() {
        if ( mImages.size() == 0)
            return;

        if (!mCurrentImageEdited) {
            mCurrentImageIndex = (mCurrentImageIndex + 1) % mImages.size();
            setImage(mCurrentImageIndex);
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
        }
    }

    public void prevImgBtnClicked() {
        if ( mImages.size() == 0)
            return;

        if (!mCurrentImageEdited) {
            if (mCurrentImageIndex == 0) {
                mCurrentImageIndex = mImages.size();
            }
            mCurrentImageIndex = (mCurrentImageIndex - 1) % mImages.size();
            setImage(mCurrentImageIndex);
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_crop_image, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else if (item.getItemId() == R.id.action_done) {
            mFinishedClicked = true;
            cropButtonClicked();
        } else if (item.getItemId() == R.id.action_skip) {
            mCurrentImageEdited = false;
            nextImageClicked();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Initial setup of crop image view
     */
    private void setUpCropImageView() {
//        if (mFinishedClicked) {
//            Intent intent = new Intent();
//            intent.putExtra(Constants.RESULT, newUri.toString());
////            intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, mCroppedImageUris);
//            setResult(Activity.RESULT_OK, intent);
//            finish();
//        }
        mCropImageView.setOnCropImageCompleteListener((CropImageView view, CropImageView.CropResult result) -> {
            mCroppedImageUris.put(mCurrentImageIndex, mCropImageView.getImageUri());
            mCropImageView.setImageUriAsync(mCroppedImageUris.get(mCurrentImageIndex));

            if (mFinishedClicked) {
                Intent intent = new Intent();
                intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, mCroppedImageUris);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }

    /**
     * Set image in crop image view & increment counters
     * @param index - image index
     */
    private void setImage(int index) {

        mCurrentImageEdited = false;
        if (index < 0 || index >= mImages.size())
            return;
        TextView mImageCount = findViewById(R.id.imagecount);
        mImageCount.setText(String.format("%s %d of %d", getString(R.string.cropImage_activityTitle)
                , index + 1, mImages.size()));
//        imageView.setImageURI(mCroppedImageUris.get(index));
        mCropImageView.setImageUriAsync(mCroppedImageUris.get(index));
    }
}
