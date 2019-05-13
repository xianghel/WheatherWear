package com.example.wheatherwear;

import android.Manifest;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.wheatherwear.adapter.ClothesAdapter;
import com.example.wheatherwear.bean.ImageBean;
import com.example.wheatherwear.bean.LabelBean;
import com.example.wheatherwear.db.Coat;
import com.example.wheatherwear.db.Inside;
import com.example.wheatherwear.db.Pants;
import com.example.wheatherwear.db.Skirt;
import com.example.wheatherwear.util.SDCardUtil;
import com.example.labels.LabelsView;

import org.litepal.crud.DataSupport;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClothesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ClothesActivity";

    private LinearLayout coatLayout;
    private LinearLayout skirtLayout;
    private LinearLayout insideLayout;
    private LinearLayout pantsLayout;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    private File dir;
    private Uri imageUri;
    private String path;

    private LabelsView labelsView;

    private static boolean havePermission;
    private static final int TAKE_PHOTO = 1;
    private static final int CHOOSE_PHOTO = 2;

    private static final int COAT = 0;
    private static final int SKIRT = 1;
    private static final int INSIDE = 2;
    private static final int PANTS = 3;

    private static int TYPE;

    private AlertDialog customizeDialog;

    private int category;
    private String name;

    private List<Coat> coatList = new ArrayList<>();
    private List<Inside> insideList = new ArrayList<>();
    private List<Pants> pantsList = new ArrayList<>();
    private List<Skirt> skirtList = new ArrayList<>();
    private List<ImageBean> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothes);
        toolbar = findViewById(R.id.clothes_toolbar);
        setSupportActionBar(toolbar);
        initView();
        checkPermission();
        initDir();
    }

    @Override
    public void onResume() {
        super.onResume();
        initClothes();
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        ClothesAdapter adapter = new ClothesAdapter(imageList);
        recyclerView.setAdapter(adapter);
    }

    private void initClothes() {
        imageList.clear();
        switch (TYPE) {
            case 0:
                coatList = DataSupport.findAll(Coat.class);
                for (Coat coat : coatList) {
                    imageList.add(new ImageBean(coat.getId(),coat.getFilePath(), coat.getName()));
                }
                break;
            case 1:
                skirtList = DataSupport.findAll(Skirt.class);
                for (Skirt skirt : skirtList) {
                    imageList.add(new ImageBean(skirt.getId(),skirt.getFilePath(), skirt.getName()));
                }
                break;
            case 2:
                insideList = DataSupport.findAll(Inside.class);
                for (Inside inside : insideList) {
                    imageList.add(new ImageBean(inside.getId(),inside.getFilePath(), inside.getName()));
                }
                break;
            case 3:
                pantsList = DataSupport.findAll(Pants.class);
                for (Pants pants : pantsList) {
                    imageList.add(new ImageBean(pants.getId(),pants.getFilePath(), pants.getName()));
                }
                break;
            default:
        }
    }

    private void initView() {
        coatLayout = findViewById(R.id.coat);
        skirtLayout = findViewById(R.id.skirt);
        insideLayout = findViewById(R.id.inside);
        pantsLayout = findViewById(R.id.pants);
        recyclerView = findViewById(R.id.clothes_list);
        coatLayout.setOnClickListener(this);
        skirtLayout.setOnClickListener(this);
        insideLayout.setOnClickListener(this);
        pantsLayout.setOnClickListener(this);
        TYPE = COAT;//默认为外套
        setLayoutBackGround();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.clothes_toolbar, menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_clothes:
                if (havePermission) {
                    showSelectionEntryDialog();
                } else {
                    Log.d(TAG, havePermission + "");
                    Toast.makeText(this, "拒绝权限无法使用添加", Toast.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    private void showSelectionEntryDialog() {
        final String[] items = {"拍摄", "从相册选择"};
        AlertDialog.Builder listDialog = new AlertDialog.Builder(this);
        listDialog.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        File outputImage = new File(dir, System.currentTimeMillis() + ".jpg");
                        path = outputImage.getPath();
                        try {
                            if (outputImage.exists()) {
                                outputImage.delete();
                            }
                            outputImage.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageUri = FileProvider.getUriForFile(ClothesActivity.this, "com.example.wheatherwear.fileprovider", outputImage);
                        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                        startActivityForResult(intent, TAKE_PHOTO);
                        break;
                    case 1:
                        openAlbum();
                        break;
                    default:
                }
            }
        });
        AlertDialog alertDialog = listDialog.create();
        ListView listView = alertDialog.getListView();
        listView.setDivider(new ColorDrawable(Color.BLACK));
        listView.setDividerHeight(1);
        alertDialog.show();
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    showPhotoConfirmationDialog();
                }
                break;
            case CHOOSE_PHOTO:
                handleImageOnKitKat(data);
                break;
            default:
        }
    }

    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://download/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imagePath = uri.getPath();
            }
        }
        File outputImage = new File(dir, System.currentTimeMillis() + ".jpg");
        path = outputImage.getPath();
        saveBitmap(path, openBitmap(imagePath));
        showPhotoConfirmationDialog();
    }

    private void saveBitmap(String path, Bitmap bitmap) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(path));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap openBitmap(String path) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(
                    new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coat:
                TYPE = COAT;
                setLayoutBackGround();
                onResume();
                break;
            case R.id.skirt:
                TYPE = SKIRT;
                setLayoutBackGround();
                onResume();
                break;
            case R.id.inside:
                TYPE = INSIDE;
                setLayoutBackGround();
                onResume();
                break;
            case R.id.pants:
                TYPE = PANTS;
                setLayoutBackGround();
                onResume();
                break;
            case R.id.add_ok:
                saveDataInDB();
                customizeDialog.dismiss();
                onResume();
                break;
            case R.id.cancel_image:
                new File(path).delete();
                customizeDialog.dismiss();
                break;
            default:
        }
    }

    private void saveDataInDB() {
        switch (TYPE) {
            case 0:
                Coat coat = new Coat();
                coat.setCategory(category);
                coat.setFilePath(path);
                coat.setName(name);
                coat.save();
                break;
            case 1:
                Skirt skirt = new Skirt();
                skirt.setCategory(category);
                skirt.setFilePath(path);
                skirt.setName(name);
                skirt.save();
                break;
            case 2:
                Inside inside = new Inside();
                inside.setCategory(category);
                inside.setFilePath(path);
                inside.setName(name);
                inside.save();
                break;
            case 3:
                Pants pants = new Pants();
                pants.setCategory(category);
                pants.setFilePath(path);
                pants.setName(name);
                pants.save();
                break;
            default:
        }
    }


    private void initDir() {
        if (SDCardUtil.checkSdCard()) {
            SDCardUtil.createFileDir(SDCardUtil.FILE_DIR);
            dir = SDCardUtil.createFileDir(SDCardUtil.FILE_DIR + SDCardUtil.IMAGE_DIR);
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            havePermission = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    havePermission = true;
                } else {
                    Toast.makeText(this, "拒绝权限无法使用程序", Toast.LENGTH_SHORT).show();
                    havePermission = false;
                }
                break;
            default:
        }
    }

    private void showPhotoConfirmationDialog() {
        AlertDialog.Builder customizeDialogBuilder = new AlertDialog.Builder(this);
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.image_dialog_layout, null);
        customizeDialogBuilder.setView(dialogView);
        RelativeLayout relativeLayout = dialogView.findViewById(R.id.add_ok);
        relativeLayout.setOnClickListener(this);
        ImageView imageView = dialogView.findViewById(R.id.image);
        Glide.with(this).load(path).into(imageView);
        labelsView = dialogView.findViewById(R.id.labels);
        setLabelList();
        ImageView cancelImage = dialogView.findViewById(R.id.cancel_image);
        cancelImage.setOnClickListener(this);
        customizeDialog = customizeDialogBuilder.create();
        customizeDialog.show();
    }

    private void setLabelList() {
        switch (TYPE) {
            case 0:
                ArrayList<LabelBean> coatList = new ArrayList<>();
                coatList.add(new LabelBean("风衣", 0));
                coatList.add(new LabelBean("卫衣", 1));
                coatList.add(new LabelBean("夹克", 2));
                coatList.add(new LabelBean("小西装", 3));
                coatList.add(new LabelBean("皮衣", 4));
                coatList.add(new LabelBean("防晒衫", 5));
                coatList.add(new LabelBean("毛呢", 6));
                coatList.add(new LabelBean("大衣", 7));
                coatList.add(new LabelBean("羽绒服", 8));
                coatList.add(new LabelBean("棉服", 9));
                setLabels(coatList);
                break;
            case 1:
                ArrayList<LabelBean> skirtList = new ArrayList<>();
                skirtList.add(new LabelBean("连衣裙", 0));
                skirtList.add(new LabelBean("半身裙", 7));//它将放在裤装的表里
                setLabels(skirtList);
                break;
            case 2:
                ArrayList<LabelBean> insideList = new ArrayList<>();
                insideList.add(new LabelBean("毛衣", 0));
                insideList.add(new LabelBean("长袖衬衫", 1));
                insideList.add(new LabelBean("雪纺衫", 2));
                insideList.add(new LabelBean("格子衫", 3));
                insideList.add(new LabelBean("长袖T恤", 4));
                insideList.add(new LabelBean("短袖T恤", 5));
                insideList.add(new LabelBean("短袖衬衫", 6));
                insideList.add(new LabelBean("羊绒衫", 7));
                insideList.add(new LabelBean("羊毛衫", 8));
                setLabels(insideList);
                break;
            case 3:
                ArrayList<LabelBean> pantsList = new ArrayList<>();
                pantsList.add(new LabelBean("牛仔", 0));
                pantsList.add(new LabelBean("休闲", 1));
                pantsList.add(new LabelBean("喇叭", 2));
                pantsList.add(new LabelBean("铅笔", 3));
                pantsList.add(new LabelBean("九分裤", 4));
                pantsList.add(new LabelBean("七分裤", 5));
                pantsList.add(new LabelBean("短裤", 6));
                setLabels(pantsList);
                break;
            default:
        }

    }

    private void setLabels(final ArrayList<LabelBean> list) {
        labelsView.setLabels(list, new LabelsView.LabelTextProvider<LabelBean>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                return data.getName();
            }
        });
        labelsView.setSelectType(LabelsView.SelectType.SINGLE);
        labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                category = list.get(position).getId();
                name = list.get(position).getName();
            }
        });
    }

    private void setLayoutBackGround() {
        switch (TYPE) {
            case 0:
                coatLayout.setBackgroundColor(getColor(R.color.colorPrimary));
                skirtLayout.setBackgroundColor(getColor(R.color.write));
                insideLayout.setBackgroundColor(getColor(R.color.write));
                pantsLayout.setBackgroundColor(getColor(R.color.write));
                break;
            case 1:
                coatLayout.setBackgroundColor(getColor(R.color.write));
                skirtLayout.setBackgroundColor(getColor(R.color.colorPrimary));
                insideLayout.setBackgroundColor(getColor(R.color.write));
                pantsLayout.setBackgroundColor(getColor(R.color.write));
                break;
            case 2:
                coatLayout.setBackgroundColor(getColor(R.color.write));
                skirtLayout.setBackgroundColor(getColor(R.color.write));
                insideLayout.setBackgroundColor(getColor(R.color.colorPrimary));
                pantsLayout.setBackgroundColor(getColor(R.color.write));
                break;
            case 3:
                coatLayout.setBackgroundColor(getColor(R.color.write));
                skirtLayout.setBackgroundColor(getColor(R.color.write));
                insideLayout.setBackgroundColor(getColor(R.color.write));
                pantsLayout.setBackgroundColor(getColor(R.color.colorPrimary));
                break;
            default:
        }
    }
}
