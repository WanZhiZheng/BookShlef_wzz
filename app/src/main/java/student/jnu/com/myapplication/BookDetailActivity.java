package student.jnu.com.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BookDetailActivity extends AppCompatActivity {

        Book book;
        List<BookShelf> bookShelfList=new ArrayList<>();
        ImageButton edit_button;
        //书本信息  这个界面没有 note 这个属性
        BookShelfManager bookShelfManager;
        String thisBookshelf;
        ImageView book_image;
        TextView book_name;
        TextView author;
        TextView press;
        TextView press_time;
        TextView isbn;
        TextView reading_state;
        TextView bookshelf;
        TextView label;
        TextView book_resource;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);




        Toolbar toolbar_detail=(Toolbar)findViewById(R.id.toolbar_detail) ;
        setSupportActionBar(toolbar_detail);
        //获取书本对象
        Intent intent=getIntent();
        book=(Book)intent.getSerializableExtra("book_item");

        //初始化控件
        edit_button=(ImageButton)findViewById(R.id.btn_edit);
        book_image=(ImageView)findViewById(R.id.book_pic_detail);
        book_name=(TextView)findViewById(R.id.book_name_detail);
        author=(TextView)findViewById(R.id.author_detail);
        press=(TextView)findViewById(R.id.press_detail);
        press_time=(TextView)findViewById(R.id.press_time_detail);
        isbn=(TextView)findViewById(R.id.isbn_detail);
        reading_state=(TextView)findViewById(R.id.readingstate_detail);
        bookshelf=(TextView)findViewById(R.id.bookshelf_detail);
        label=(TextView)findViewById(R.id.label_datail);
        book_resource=(TextView)findViewById(R.id.book_source_detail);
        //获取该图书信息
        setMessage_frombook();
//        book_image.setImageResource(book.getImageId());
//        book_name.setText(book.getBookName());
//        author.setText(book.getAuthor());
//        press.setText(book.getPressName());
//        press_time.setText(book.getPressTime());
//        isbn.setText(book.getISBN());

        //左上角返回按钮
        toolbar_detail.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

//        //获取该图书信息
//

        //编辑按钮
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(BookDetailActivity.this,BookEditActivity.class);
                intent.putExtra("book_item",book);
                startActivity(intent);
                finish();
            }
        });


    }


    public void setMessage_frombook(){
        //Bitmap bitmap=ImageManager.GetLocalBitmap(BookDetailActivity.this,book.getUuid());
        //book_image.setImageBitmap(bitmap);
        Bitmap bitmap=ImageManager.GetLocalBitmap(BookDetailActivity.this,book.getUuid());
        //book_image.setImageResource(book.getImageId());
        book_image.setImageBitmap(bitmap);
        book_name.setText(book.getBookName());
        author.setText(book.getAuthor());
        press.setText(book.getPressName());
        press_time.setText(book.getPressTime());
        isbn.setText(book.getISBN());
        reading_state.setText(book.getReadingstate());
        bookshelf.setText(book.getBookshelf());
//        label.setText(book.getLabel());
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_menu,menu);
        return true;
    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode){
//            case 77:
//                if(resultCode==RESULT_OK){
//                    Book book_return=(Book)data.getSerializableExtra("return_item");
//                    setBook(book_return);
//
//                }
//
//        }
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_book:
                bookShelfManager=new BookShelfManager();
                bookShelfManager.read(BookDetailActivity.this);
//        List<BookShelf> ttt=new ArrayList<BookShelf>();
//        ttt=bookShelfManager.getBookShelfList();
                for(BookShelf temp:bookShelfManager.getBookShelfList())
                    bookShelfList.add(temp);

                thisBookshelf=book.getBookshelf();

                //从所属书架中删除
                for(int i=0;i<bookShelfList.size()-1;i++){
                    if(bookShelfList.get(i).getBookshelfName().equals(thisBookshelf))
                    {
                        for(int j=0;j<bookShelfList.get(i).getBookList().size();j++){
                            if(bookShelfList.get(i).getBookList().get(j).getUuid().equals(book.getUuid())) {
                                bookShelfList.get(i).getBookList().remove(j);
                                break;
                            }
                        }
                        break;
                    }

                }
                //从“所有”中删除
                for(int k=0;k<bookShelfList.get(0).getBookList().size();k++){
                    if (bookShelfList.get(0).getBookList().get(k).getUuid().equals(book.getUuid())){
                        bookShelfList.get(0).getBookList().remove(k);
                    }
                }




                //从所属标签book.getlabel中移除这本书


                //保存
                bookShelfManager.getBookShelfList().clear();
                for(BookShelf bookShelf_temp:bookShelfList)
                    bookShelfManager.getBookShelfList().add(bookShelf_temp);
                bookShelfManager.save(getApplicationContext());

                // 然后保存bookshelfList
                Toast.makeText(this,"delete successfully!",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(BookDetailActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return true;
    }
}
