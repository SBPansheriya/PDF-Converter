package com.androidmarket.pdfcreator.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.provider.OpenableColumns;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import androidmarket.R;
import butterknife.ButterKnife;

import com.androidmarket.pdfcreator.Constants;
import com.androidmarket.pdfcreator.fragment.ImageToPdfFragment;
import com.androidmarket.pdfcreator.util.FileUtils;
import com.androidmarket.pdfcreator.util.StringUtils;
import com.canhub.cropper.CropImage;
import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.canhub.cropper.CropImageView;
//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;

import static com.androidmarket.pdfcreator.Constants.pdfDirectory;

public class ActivityCropImage extends AppCompatActivity {

    private int mCurrentImageIndex = 0;
    private ArrayList<String> mImages;
    private final HashMap<Integer, Uri> mCroppedImageUris = new HashMap<>();
    private boolean mCurrentImageEdited = false;
    private boolean mFinishedClicked = false;
    List<Uri> croppedUris = new ArrayList<>();
    ImageView imageView;
    ImageView back, done;
    TextView skip;
    Uri newUri;
    ActivityResultLauncher<CropImageContractOptions> cropImage;
    Uri currentUri;
    int index1;
    Button cropButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image_activity);
        getSupportActionBar().hide();
        ButterKnife.bind(this);

//        MaterialToolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

//        mCropImageView = findViewById(R.id.cropImageView);
        imageView = findViewById(R.id.crop_image);
        cropButton = findViewById(R.id.crop_button);
        back = findViewById(R.id.back);
        done = findViewById(R.id.done_cropping);
        skip = findViewById(R.id.skip);

        cropButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = imageView.getDrawable();

                Bitmap bitmap = getBitmapFromDrawable(drawable);

                if (bitmap != null) {
                    startCrop(currentUri);
                }
            }
        });

        cropImage = registerForActivityResult(new CropImageContract(), result -> {
            if (result.isSuccessful()) {
                newUri = result.getUriContent();
                String fileName = getFileNameFromUri(newUri);

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), newUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String image = saveToInternalStorage(bitmap,fileName);

                Uri myURI = Uri.parse(image);

                croppedUris.add(myURI);

                mImages.remove(index1);
                mImages.add(index1,myURI.toString());
                imageView.setImageURI(myURI);
                currentUri = myURI;
            }
        });

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
                setUpCropImageView();
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

        ImageView nextImageButton = findViewById(R.id.nextimageButton);
        nextImageButton.setOnClickListener(view -> nextImageClicked());
        ImageView previousImageButton = findViewById(R.id.previousImageButton);
        previousImageButton.setOnClickListener(view -> prevImgBtnClicked());
    }

    private String saveToInternalStorage(Bitmap bitmapImage,String name) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File mypath = new File(directory, name);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme() != null && uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    private Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }

    private void startCrop(Uri selectedImageUri) {
        CropImageOptions options = new CropImageOptions();
        options.guidelines = CropImageView.Guidelines.ON;
        CropImageContractOptions cropImageContractOptions = new CropImageContractOptions(selectedImageUri, options);
        cropImage.launch(cropImageContractOptions);
    }

    public void cropButtonClicked() {
        mCurrentImageEdited = false;
        String root = Environment.getExternalStorageDirectory().toString();
        File folder = new File(root + pdfDirectory);
        Uri uri = newUri;

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

    }

    public void nextImageClicked() {
        if (mImages.size() == 0)
            return;

        if (!mCurrentImageEdited) {
            mCurrentImageIndex = (mCurrentImageIndex + 1) % mImages.size();
            setImage(mCurrentImageIndex);
        } else {
            StringUtils.getInstance().showSnackbar(this, R.string.save_first);
        }
    }

    public void prevImgBtnClicked() {
        if (mImages.size() == 0)
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

        if (mFinishedClicked) {
            List<String> uriStrings = new ArrayList<>();
            for (Uri uri : croppedUris) {
                uriStrings.add(uri.toString());
            }

            Intent intent = new Intent();
            intent.putStringArrayListExtra(Constants.RESULT, mImages);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
//        mCropImageView.setOnCropImageCompleteListener((CropImageView view, CropImageView.CropResult result) -> {
//            mCroppedImageUris.put(mCurrentImageIndex, mCropImageView.getImageUri());
//            mCropImageView.setImageUriAsync(mCroppedImageUris.get(mCurrentImageIndex));
//
//            if (mFinishedClicked) {
//                Intent intent = new Intent();
//                intent.putExtra(CropImage.CROP_IMAGE_EXTRA_RESULT, mCroppedImageUris);
//                setResult(Activity.RESULT_OK, intent);
//                finish();
//            }
//        });
    }

    /**
     * Set image in crop image view & increment counters
     *
     * @param index - image index
     */
    @SuppressLint("DefaultLocale")
    private void setImage(int index) {

        mCurrentImageEdited = false;
        if (index < 0 || index >= mImages.size())
            return;
        TextView mImageCount = findViewById(R.id.imagecount);
        mImageCount.setText(String.format("%s %d of %d", getString(R.string.cropImage_activityTitle)
                , index + 1, mImages.size()));
        imageView.setImageURI(mCroppedImageUris.get(index));
        index1 = index;
        currentUri = mCroppedImageUris.get(index);
//        mCropImageView.setImageUriAsync(mCroppedImageUris.get(index));
    }
}
