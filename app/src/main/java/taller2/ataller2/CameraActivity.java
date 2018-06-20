package taller2.ataller2;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class CameraActivity extends Activity {
    private static int TAKE_IMAGE = 1;
    private static int PICK_IMAGE = 2;

    private Button btn_hacerfoto;
    private Button btm_publicar;
    private Button btn_subirfoto;

    private ImageView btn_editar;
    private ImageView ivPhoto;

    private File myFilesDir;

    Uri file = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_crear_historia_larga);
        ivPhoto = (ImageView) findViewById(R.id.imgMostrar);

        myFilesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.project/files");
        System.out.println (myFilesDir);
        myFilesDir.mkdirs();

        btn_hacerfoto = (Button) this.findViewById(R.id.btn_camara);
        btn_hacerfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTakeFoto(v.getContext());
            }
        });

        btn_subirfoto = (Button) this.findViewById(R.id.btn_subir_foto);
        btn_subirfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btm_publicar = (Button) this.findViewById(R.id.button5);
        btm_publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_editar = (ImageView) this.findViewById(R.id.botoneditar);
        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void openTakeFoto(Context context){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        String authority =  context.getApplicationContext().getPackageName() + ".my.package.name.provider";
        file = FileProvider.getUriForFile(context, authority, getOutputMediaFile());
        i.putExtra(MediaStore.EXTRA_OUTPUT, file);
        startActivityForResult(i, TAKE_IMAGE);
    }

    private static File getOutputMediaFile()
    {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "Android APP");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==TAKE_IMAGE){
            try {
                ivPhoto.setImageURI(file);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        if (requestCode==PICK_IMAGE){
            Uri imageUri = data.getData();
            ivPhoto.setImageURI(imageUri);
        }

    }

}
