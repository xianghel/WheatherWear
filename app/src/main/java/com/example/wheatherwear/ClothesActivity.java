package com.example.wheatherwear;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
import com.example.wheatherwear.bean.LabelBean;
import com.example.wheatherwear.util.SDCardUtil;
import com.example.labels.LabelsView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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

    private LabelsView labelsView;

    private static boolean havePermission;
    private static final int TAKE_PHOTO = 1;

    private static final int COAT = 0;
    private static final int SKIRT = 1;
    private static final int INSIDE = 2;
    private static final int PANTS = 3;

    private static int TYPE;

    private AlertDialog customizeDialog;

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    showPhotoConfirmationDialog();
                }
                break;
            default:
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.coat:
                TYPE = COAT;
                setLayoutBackGround();
                break;
            case R.id.skirt:
                TYPE = SKIRT;
                setLayoutBackGround();
                break;
            case R.id.inside:
                TYPE = INSIDE;
                setLayoutBackGround();
                break;
            case R.id.pants:
                TYPE = PANTS;
                setLayoutBackGround();
            case R.id.add_ok:
                customizeDialog.dismiss();
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
        Glide.with(this).load(imageUri).into(imageView);
        labelsView = dialogView.findViewById(R.id.labels);
        setLabelList();
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

    private void setLabels(ArrayList<LabelBean> list) {
        labelsView.setLabels(list, new LabelsView.LabelTextProvider<LabelBean>() {
            @Override
            public CharSequence getLabelText(TextView label, int position, LabelBean data) {
                return data.getName();
            }
        });
        labelsView.setSelectType(LabelsView.SelectType.SINGLE_IRREVOCABLY);
        labelsView.setOnLabelClickListener(new LabelsView.OnLabelClickListener() {
            @Override
            public void onLabelClick(TextView label, Object data, int position) {
                Toast.makeText(ClothesActivity.this, position + " : " + data,
                        Toast.LENGTH_LONG).show();
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
