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
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File imagesFolder = new File(Environment.getExternalStorageDirectory(), "AndroidFacil");
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "foto.jpg");
        if (image.exists ()) image.delete ();
        String authority =  context.getApplicationContext().getPackageName() + ".my.package.name.provider";
        Uri uriSavedImage = FileProvider.getUriForFile(context, authority, image);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(cameraIntent, TAKE_IMAGE);
    }

    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==TAKE_IMAGE){
            try {
                String exSD = Environment.getExternalStorageDirectory()+ "/AndroidFacil/" + "foto.jpg";
                String exSD2 = "/taller2.ataller2.my.package.name.provider/external_files/AndroidFacil/foto.jpg";

                //Bitmap bMap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+ "/AndroidFacil/"+"foto.jpg");
                Bitmap bMap2 = BitmapFactory.decodeFile(exSD2);
                Bitmap bMap =  BitmapFactory.decodeFile(exSD);
                //Bitmap bMap = BitmapFactory.decodeFile(myFilesDir.toString() + "/temp.jpg");
                Bitmap.createBitmap(bMap);
                ivPhoto.setImageBitmap(bMap);
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

    private void SaveImage(Bitmap finalBitmap) {
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_images");
        myDir.mkdirs();
        String fname = "Image.jpg";
        File file = new File (myDir, fname);
        if (file.exists ())
            file.delete ();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                Uri.parse("file://" + Environment.getExternalStorageDirectory())));
    }

}
