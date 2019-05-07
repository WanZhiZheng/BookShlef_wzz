package student.jnu.com.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookEditActivity extends AppCompatActivity {
    String[] readingstate=new String[]{"已读","阅读中","未读"};
    CharSequence[] items={"拍照","从相册中选择"};
    public final static int CAMERA_REQUEST_CODE=0;
    public final static int GALLERY_REQUEST_CODE=1;
    List<BookShelf> bookShelfList =new ArrayList<>();
    List<String> labelList=new ArrayList<>();
    BookShelfManager bookShelfManager;
    Book book;
    Uri imageUri;
    String mTempPhotoPath;
    Spinner bookshelf_spinner;
    Spinner readingstate_spinner;
    int present_bookshelfspinner_selection;
    int present_readingstatespinner_selection;
    ImageView book_pic_edit;
    EditText book_name;
    EditText author;
    EditText press;
    EditText press_time;
    EditText isbn;
    EditText note_edit;
    EditText book_resource;
    TextView label;

    //String[] bookshelfList=new String[]{"默认书架","添加书架"};
    //书架保存的时候要去除最后一项(添加书架)保存
    //读取的时候 最后加上最后一项(添加书架)



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);


        //读取bookshelfList
        bookShelfManager=new BookShelfManager();
        bookShelfManager.read(BookEditActivity.this);
//        List<BookShelf> ttt=new ArrayList<BookShelf>();
//        ttt=bookShelfManager.getBookShelfList();
        for(BookShelf temp:bookShelfManager.getBookShelfList())
            bookShelfList.add(temp);
        //读取labelList
        SharedPreferences sp=this.getSharedPreferences("labelList", Activity.MODE_PRIVATE);
        String labelList_json=sp.getString("label_List_json","");
        if(!labelList_json.equals("")){
            Gson gson=new Gson();
            Type listType=new TypeToken<List<String>>(){}.getType();
            labelList=gson.fromJson(labelList_json,listType);

        }



        Log.d("size",String.valueOf(labelList.size()));
        if(bookShelfList.size()==0){
            bookShelfList.add(new BookShelf("所有"));
            bookShelfList.add(new BookShelf("默认书架"));
            bookShelfList.add(new BookShelf("添加书架"));
        }

        //read_into_bookshelflist(){
        //     bookshelfList.clear(); 先清除 然后再读入！
           //   然后在读入
           //
        // }


        //加载上方toolbar
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar_edit) ;
        setSupportActionBar(toolbar);

        //获得传过来的book对象
        Intent intent=getIntent();
        book=(Book)intent.getSerializableExtra("book_item");

        Log.d("uuid",book.getUuid());

        //初始化控件
        book_pic_edit=(ImageView)findViewById(R.id.bookpic_edit);
        book_name=(EditText) findViewById(R.id.book_name_edit);
        author=(EditText)findViewById(R.id.book_author_edit);
        press=(EditText) findViewById(R.id.book_press_edit);
        press_time=(EditText)findViewById(R.id.book_presstime_edit);
        isbn=(EditText) findViewById(R.id.book_isbn_edit);
        readingstate_spinner=(Spinner)findViewById(R.id.spinner_readingstate);
        bookshelf_spinner=(Spinner) findViewById(R.id.spinner_bookshelf_edit);
        note_edit=(EditText)findViewById(R.id.book_note_edit);
        book_resource=(EditText) findViewById(R.id.book_source_edit);
        label=(TextView)findViewById(R.id.book_label_edit);  //这里还不知道要不要设置成Spinner  后面处理

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(BookEditActivity.this);
                dialog.setTitle("提示");
                dialog.setMessage("图书信息未保存，请问是否继续？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Intent intent1=new Intent(BookEditActivity.this,MainActivity.class);
                        //startActivity(intent1);
                        finish();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
        //获取该图书信息 赋值到各个图书的信息项
        setMessage_frombook();


        //点击图片 换图片
        book_pic_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(BookEditActivity.this);
                dialog.setTitle("选择图片");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0://拍照
                                if(ContextCompat.checkSelfPermission(BookEditActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(BookEditActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_REQUEST_CODE);
                                }else{
                                    takePhoto();
                                }
                                break;
                            case 1://从相册中选择
                                if(ContextCompat.checkSelfPermission(BookEditActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){

                                    ActivityCompat.requestPermissions(BookEditActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},CAMERA_REQUEST_CODE);
                                }else{
                                    choosePhoto();
                                }
                                break;
                        }
                    }
                });
                dialog.show();
            }
        });



        readingstate_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //标记当前的选择的阅读状态
                present_readingstatespinner_selection=position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //标签点击
        label.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int label_count=labelList.size();
                final String[] labelList_str=new String[label_count];
                for(int i=0;i<labelList.size();i++){
                    labelList_str[i]=labelList.get(i);
                }


                //要根据这个book的 labelList 设置这个selected
                final boolean []selected=new boolean[label_count];
                for(int i=0;i<label_count;i++){
                    for(int j=0;j<book.getLabelList().size();j++){
                        if(labelList_str[i].equals(book.getLabelList().get(j))){
                            selected[i]=true;
                            break;
                        }
                    }
                }


                AlertDialog.Builder dialog=new AlertDialog.Builder(BookEditActivity.this);
                dialog.setTitle("选择标签");
                dialog.setMultiChoiceItems(labelList_str, selected, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        //doing nothing
                    }
                });
                dialog.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String label_content="";
                        book.getLabelList().clear();
                        for(int i=0;i<label_count;i++){
                            if(selected[i]){
                                if(i==0){
                                    label_content=label_content+labelList_str[i];

                                }else{
                                    label_content=label_content+","+labelList_str[i];
                                }
                                book.getLabelList().add(labelList_str[i]);
                            }
                        }
                        Log.d("abc", String.valueOf(book.getLabelList().size()));
                        label.setText(label_content);
                        //这个book的 labelList要更新

                    }
                });


                dialog.show();
            }
        });





        //书架的spinner 监听鼠标点击
        bookshelf_spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //如果在spinner里面点击的是“添加书架”这一项  则弹出对话框dialog
                if(position==bookshelf_spinner.getAdapter().getCount()-1){
                    final EditText bookshelf_name_edit=new EditText(BookEditActivity.this);
                    bookshelf_name_edit.setHint("请输入书架名称");
                    AlertDialog.Builder dialog=new AlertDialog.Builder(BookEditActivity.this);
                        dialog.setTitle("新建书架");                //dialog标题
                        dialog.setCancelable(false);                //按back键不可取消dialog
                        dialog.setView(bookshelf_name_edit);        //把editText放入dialog中
                        //确定按钮
                        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String bookshelfname=bookshelf_name_edit.getText().toString();
                                //在倒数第二位置添加新书架到书架的list中
                                bookShelfList.add(bookShelfList.size()-1 , new BookShelf(bookshelfname));
                               //刷新一下spinner的内容以及所选内容
                                refresh_bookshelf_spinner();

                            }
                        });
                    //取消按钮
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //取消添加书架，则默认回到当前的书架选择，以避免spinner显示"添加书架"这一项
                                //标记当前书架选择
                                    bookshelf_spinner.setSelection(present_bookshelfspinner_selection,true);
                            }
                        });
                    dialog.show();


                }else{
                    //标记当前书架选择
                    present_bookshelfspinner_selection=position;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Log.d("msg",String.valueOf(bookShelfManager.getBookShelfList().get(2).getBookList().size()));

    }

    public void refresh_bookshelf_spinner(){
        //刷新下拉框spinner内的内容
        int count=bookShelfList.size();
        String[] bookshelfList_names=new String[count-1];
        for(int i=1;i<count;i++){
            bookshelfList_names[i-1] = bookShelfList.get(i).getBookshelfName();
        }
        ArrayAdapter<String> bookshelflist_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,bookshelfList_names);
        bookshelflist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookshelf_spinner.setAdapter(bookshelflist_adapter);
        //标记当前书架选择
        present_bookshelfspinner_selection=count-3;
        bookshelf_spinner.setSelection(present_bookshelfspinner_selection,true);



    }


    public void setMessage_frombook(){
        Bitmap bitmap=ImageManager.GetLocalBitmap(BookEditActivity.this,book.getUuid());
        if(bitmap==null){
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.book_cover_default);
            ImageManager.SaveImage(getApplicationContext(),bitmap,book.getUuid());
        }
            book_pic_edit.setImageBitmap(bitmap);


        //book_pic_edit.setImageResource(book.getImageId());
        book_name.setText(book.getBookName());
        author.setText(book.getAuthor());
        press.setText(book.getPressName());
        press_time.setText(book.getPressTime());
        isbn.setText(book.getISBN());
        note_edit.setText(book.getNote());
        book_resource.setText(book.getBookresource());

        //加载标签
        String label_content="";
        for(int i=0;i<book.getLabelList().size();i++){
            if(i==0){
                label_content=label_content+book.getLabelList().get(i);
            }else{
                label_content=label_content+","+book.getLabelList().get(i);
            }

        }
        label.setText(label_content);
        //bitmap=null;
        //book_label

        //加载阅读状态下拉框
        ArrayAdapter<String> readingstate_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,readingstate);
        readingstate_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        readingstate_spinner.setAdapter(readingstate_adapter);

        //设置阅读状态下拉框默认选中值
        SpinnerAdapter readingstate_spinnerAdapter=readingstate_spinner.getAdapter();
        int k_r=readingstate_spinnerAdapter.getCount();
        //readingstate_spinner.setSelection(2,true);
        for(int i=0;i<k_r;i++){
            if(book.getReadingstate().equals(readingstate_spinnerAdapter.getItem(i).toString())){
                //标记当前书架选择
                present_readingstatespinner_selection=i;
                readingstate_spinner.setSelection(present_readingstatespinner_selection,true);
                break;
            }
        }

        //加载书架下拉框

        int count=bookShelfList.size();
        String[] bookshelfList_names=new String[count-1];
        for(int i=1;i<count;i++){
            bookshelfList_names[i-1] = bookShelfList.get(i).getBookshelfName();
        }
        ArrayAdapter<String> bookshelflist_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,bookshelfList_names);
        bookshelflist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookshelf_spinner.setAdapter(bookshelflist_adapter);

        //设置书架下拉框默认选中值
        SpinnerAdapter bookshelf_spinnerAdapter=bookshelf_spinner.getAdapter();
        int k_b=bookshelf_spinnerAdapter.getCount();
        for(int i=0;i<k_b;i++){
            if(book.getBookshelf().equals(bookshelf_spinnerAdapter.getItem(i).toString())){
                //标记当前书架选择
                present_bookshelfspinner_selection=i;
                bookshelf_spinner.setSelection(present_bookshelfspinner_selection,true);
                break;
            }
        }
    }

    private void takePhoto(){
        Intent intentToTakePhoto=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTempPhotoPath= Environment.getExternalStorageDirectory()+ File.separator+"photo.jpeg";
        imageUri= FileProvider.getUriForFile(BookEditActivity.this,BookEditActivity.this.getApplicationContext().getPackageName()+".my.provider",new File(mTempPhotoPath));
        intentToTakePhoto.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
        startActivityForResult(intentToTakePhoto,CAMERA_REQUEST_CODE);
    }

    private void choosePhoto(){
        Intent intentToPickPic = new Intent(Intent.ACTION_PICK, null);
        // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型" 所有类型则写 "image/*"
        intentToPickPic.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intentToPickPic, GALLERY_REQUEST_CODE);
    }

    public void setBook(){
        book.setBookName(book_name.getText().toString());
        book.setAuthor(author.getText().toString());
        book.setISBN(isbn.getText().toString());
        book.setPressName(press.getText().toString());
        book.setPressTime(press_time.getText().toString());
        book.setBookshelf(bookshelf_spinner.getAdapter().getItem(present_bookshelfspinner_selection).toString());
        book.setReadingstate(readingstate_spinner.getAdapter().getItem(present_readingstatespinner_selection).toString());
        book.setBookresource(book_resource.getText().toString());
        book.setNote(note_edit.getText().toString());
        //book.setLabel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_menu,menu);

        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:

                //   B.
                // book.getBookshelf()
                   //得知这本书从哪个书架来，并从bookshelfList中删除这个对象

                String  bookshelfname=book.getBookshelf();
                String uuid=book.getUuid();
                setBook();
                String new_bookshelfname=book.getBookshelf();
//                String new_isbn=book.getISBN();
                    int bookshelf_selected=-1,bookInsert_place=-1;
                //从该book原来的bookshelf中删除该book
                    for(int i=0;i<bookShelfList.size()-1;i++){  //size-1.  是因为最后一个 “添加书架”并不是真正的书架，只是起到按钮作用  所以不必遍历，防止用户添加名为“添加书籍”这一书架避免冲突
                       if(bookshelfname.equals(bookShelfList.get(i).getBookshelfName()))
                       {
                           bookshelf_selected=i;  //肯定可以找到这个值i ，因为bookshelf的值本来就是从bookshelflist中取来的
                           List<Book> temp=new ArrayList<>();
                            temp=bookShelfList.get(i).getBookList();
                           for(int m=0;m<temp.size();m++)
                               if(uuid.equals(temp.get(m).getUuid())) {
                                   bookInsert_place=m;
                                   bookShelfList.get(i).getBookList().remove(m);
                                   break;
                               }

                          break;
                         }
                    }

                    //“所有”里面也要删除
                boolean isExist=false;
                int target=0;
                for(int k=0;k<bookShelfList.get(0).getBookList().size();k++){
                    if(bookShelfList.get(0).getBookList().get(k).getUuid().equals(uuid)){

                        isExist=true;
                        target=k;
                        break;
                    }
                }
                if(isExist){  //如果书籍存在于“所有”书架 则删除
                    bookShelfList.get(0).getBookList().remove(target);
                }

                //在“所有”书架中添加新书籍
                if(isExist){
                    bookShelfList.get(0).getBookList().add(target,book);
                }else{
                    bookShelfList.get(0).getBookList().add(book);
                }


                        //在新书架中添加新书籍

                        //如果编辑过程中书架项没变
                    if(bookshelfname.equals(new_bookshelfname)){
                        if(bookInsert_place==-1)//该书不存在，或者说现在是添加书籍步骤，那就直接在后面添加
                            bookShelfList.get(bookshelf_selected).getBookList().add(book);
                        else//说明书架里本来是有这本书的，只不过改了里面其他信息，我们希望在原位置插入书籍吧，不破坏原来顺序
                            bookShelfList.get(bookshelf_selected).getBookList().add(bookInsert_place,book);
                    }else{
                        //编辑过程中书架项发送变化
                        bookShelfList.get(present_bookshelfspinner_selection+1).getBookList().add(book);

                    }


                //Log.d("msg",String.valueOf(bookShelfManager.getBookShelfList().get(2).getBookList().size()));

                bookShelfManager.getBookShelfList().clear();
                for(BookShelf bookShelf_temp:bookShelfList)
                    bookShelfManager.getBookShelfList().add(bookShelf_temp);
                bookShelfManager.save(getApplicationContext());

                //跳转
                Intent intent1=new Intent(BookEditActivity.this,MainActivity.class);
                startActivity(intent1);
                finish();

                break;
        }

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        book_pic_edit.findViewById(R.id.bookpic_edit);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case CAMERA_REQUEST_CODE:
                    try{
                        //Bitmap bit=BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        Bitmap bit=ImageManager.decodeSampledBitmapFromUri(getApplicationContext(),imageUri);
                        ImageManager.SaveImage(getApplicationContext(),bit,book.getUuid());
                        book_pic_edit.setImageBitmap(bit);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case GALLERY_REQUEST_CODE:
                    try {
                        //该uri就是照片文件夹对应的uri

                        imageUri=data.getData();
                        Bitmap bit =ImageManager.decodeSampledBitmapFromUri(getApplicationContext(),imageUri);
                        ImageManager.SaveImage(getApplicationContext(),bit,book.getUuid());
                        book_pic_edit.setImageBitmap(bit);
                        // 给相应的ImageView设置图片 未裁剪
                        //mImageView.setImageBitmap(bit);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

            }
        }


        super.onActivityResult(requestCode, resultCode, data);
    }
}
