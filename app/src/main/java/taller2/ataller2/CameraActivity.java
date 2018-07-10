package taller2.ataller2;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import taller2.ataller2.model.Historia;
import taller2.ataller2.model.HistoriaCorta;
import taller2.ataller2.services.OnCallback;
import taller2.ataller2.services.OnCallbackImageUpload;
import taller2.ataller2.services.ServiceLocator;
import taller2.ataller2.services.HistoriasService;

public class CameraActivity extends Activity {

    static
    {
        System.loadLibrary("NativeImageProcessor");
    }

    private static int TAKE_IMAGE = 1;
    private static int PICK_IMAGE = 2;
    private static int PICK_VIDEO = 3;

    private ImageView btn_hacerfoto;
    private ImageView btm_publicar;
    private ImageView btn_subirfoto;
    private ImageView btn_subirvideo;

    private ImageView btn_editar;
    private ImageView ivPhoto;
    private VideoView vvVideo;

    private ProgressBar mProgressBar;
    private ConstraintLayout mConstraintLayout;

    private Uri uriVideo;

    private TextView tv;

    private CheckBox cb;

    private File myFilesDir;

    Uri file = null;

    int valueBrillo = 0;
    int valueContraste = 0;
    int valueSaturacion = 0;

    boolean videoOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_crear_historia_larga);

        mConstraintLayout = findViewById(R.id.constraint_layout_subir_historias);
        mProgressBar = findViewById(R.id.progressBar_crear_historia);

        ivPhoto = (ImageView) findViewById(R.id.imgMostrar);
        vvVideo = findViewById(R.id.videoMostrar);

        tv = (TextView) findViewById(R.id.text_input_historia);

        myFilesDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/com.example.project/files");
        System.out.println (myFilesDir);
        myFilesDir.mkdirs();

        btn_hacerfoto = (ImageView) this.findViewById(R.id.btn_camara);
        btn_hacerfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vvVideo.setVisibility(View.GONE);
                ivPhoto.setVisibility(View.VISIBLE);
                openTakeFoto(v.getContext());
                videoOn = false;
            }
        });

        btn_subirfoto = (ImageView) this.findViewById(R.id.btn_subir_foto);
        btn_subirfoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vvVideo.setVisibility(View.GONE);
                ivPhoto.setVisibility(View.VISIBLE);
                openGallery();
                videoOn = false;
            }
        });

        btn_subirvideo = (ImageView) this.findViewById(R.id.btn_subir_video);
        btn_subirvideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vvVideo.setVisibility(View.VISIBLE);
                ivPhoto.setVisibility(View.GONE);
                openGalleryVideo();
                videoOn = true;
            }
        });

        btm_publicar = (ImageView) this.findViewById(R.id.button5);
        btm_publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoOn){
                    ponerLoading();
                    publicarHistoriaVideo(new OnCallback(){
                        @Override
                        public void onFinish() {
                            finish();
                            Toast.makeText(CameraActivity.this,"Su historia se ha subido satisfactoriamente", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    ponerLoading();
                    publicarHistoria(new OnCallback() {
                        @Override
                        public void onFinish() {
                            finish();
                            Toast.makeText(CameraActivity.this,"Su historia se ha subido satisfactoriamente", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        btn_editar = (ImageView) this.findViewById(R.id.botoneditar);
        btn_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialogFilters(v.getContext());
            }
        });

        cb = (CheckBox) this.findViewById(R.id.checkBox);

    }

    private void publicarHistoria(final OnCallback callback ) {
        boolean checked = cb.isChecked();
        if (checked){
            final HistoriaCorta historia = new HistoriaCorta();
            historia.setPicture(drawableToBitmap(ivPhoto.getDrawable()));
            historia.setPictureUsr(drawableToBitmap(ivPhoto.getDrawable()));

            ServiceLocator.get(HistoriasService.class).uploadImageFromMemory(ivPhoto, new OnCallbackImageUpload() {
                @Override
                public void onFinish(Uri uri) {
                    historia.setUri(uri);
                    historia.setType("jpg");
                    getHistoriasService().crearHistoriaCorta(getFragmentManager(),historia,callback);
                }
            });
        }
        else{
            final Historia historia = new Historia(tv.getText().toString());
            historia.setPicture(drawableToBitmap(ivPhoto.getDrawable()));
            historia.setDescription("muy buena foto");

            ServiceLocator.get(HistoriasService.class).uploadImageFromMemory(ivPhoto, new OnCallbackImageUpload() {
                @Override
                public void onFinish(Uri uri) {
                    historia.setUri(uri);
                    historia.setType("jpg");
                    getHistoriasService().crearHistoria(getFragmentManager(),historia, callback);
                }
            });
        }
    }

    private void publicarHistoriaVideo(final OnCallback callback) {
        final Historia historia = new Historia(tv.getText().toString());
        historia.setVideo(uriVideo);
        historia.setDescription("muy buena foto");
       // getHistoriasService().crearHistoria(this.getFragmentManager(),historia);
        ServiceLocator.get(HistoriasService.class).uploadVideoFromMemory(vvVideo, new OnCallbackImageUpload() {
            @Override
            public void onFinish(Uri uri) {
                historia.setTieneVideo(true);
                historia.setUri(uri);
                historia.setType("mp3");
                getHistoriasService().crearHistoria(getFragmentManager(),historia, callback);
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

    private static File getOutputMediaFile() {
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

    private void openGalleryVideo(){
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
        //gallery.setType("video/*, image/*");
        startActivityForResult(gallery, PICK_VIDEO);
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
            //ServiceLocator.get(HistoriasService.class).updateHistoriasData(this);
        }

        if (requestCode==PICK_VIDEO){

            Uri uri = data.getData();
            uriVideo = uri;
            try{
                vvVideo.setVideoURI(uri);
                vvVideo.start();
            }catch(Exception e){
                e.printStackTrace();
            }
            //ServiceLocator.get(HistoriasService.class).updateHistoriasData(this);
        }

    }

    private void openDialogFilters(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_filter);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        Button acceptButton = dialog.findViewById(R.id.boton_aceptar);
        Button cancelButton = dialog.findViewById(R.id.boton_rechazar);


        SeekBar brillo = dialog.findViewById(R.id.seekBar1);
        SeekBar contraste = dialog.findViewById(R.id.seekBar4);
        final SeekBar saturacion = dialog.findViewById(R.id.seekBar3);
        SeekBar asd = dialog.findViewById(R.id.seekBar2);

        valueBrillo = brillo.getProgress();
        valueContraste = contraste.getProgress();
        valueSaturacion = saturacion.getProgress();

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ivPhoto.getDrawable() != null) {
                    Filter myFilter = new Filter();
                    //brillo mayor a 0
                    myFilter.addSubFilter(new BrightnessSubFilter(30));
                    //contrast 0-2
                    float contraste = (float) valueContraste/200;
                    myFilter.addSubFilter(new ContrastSubFilter(1.1f));
                    //saturacion 0-2
                    float saturacion = (float) valueSaturacion /200;
                    myFilter.addSubFilter(new SaturationSubfilter(1.3f));
                    Bitmap inputImage = redimensionarImagenMaximo(drawableToBitmap(ivPhoto.getDrawable()),100,100);
                    Bitmap outputImage = myFilter.processFilter(inputImage);
                    ivPhoto.setImageBitmap(outputImage);
                    dialog.dismiss();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap redimensionarImagenMaximo(Bitmap mBitmap, float newWidth, float newHeigth){
        //Redimensionamos
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeigth) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
    }

    private HistoriasService getHistoriasService() {
        return ServiceLocator.get(HistoriasService.class);
    }

    private void ponerLoading(){
        mConstraintLayout.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);
    }

}
