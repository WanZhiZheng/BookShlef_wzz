package student.jnu.com.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.List;

/**
 * Created by ASUS on 2019/4/8.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {
    private List<Book> mBookList;
    MainActivity mainActivity;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View bookView;
        ImageView bookImage;
        TextView bookName;
        TextView press;
        TextView press_time;

        public ViewHolder(View view) {
            super(view);
            bookView=view;
            bookImage = (ImageView) view.findViewById(R.id.book_image);
            bookName = (TextView) view.findViewById(R.id.book_name);
            press = (TextView) view.findViewById(R.id.press);
            press_time = (TextView) view.findViewById(R.id.press_time);
        }
    }

        public BookAdapter(List<Book> bookList,MainActivity mainActivity){
            this.mainActivity=mainActivity;
            this.mBookList=bookList;
        }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,parent,false);
            final ViewHolder holder=new ViewHolder(view);
            holder.bookView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position=holder.getAdapterPosition();
                    Book book=mBookList.get(position);
                    Toast.makeText(v.getContext(),book.getBookName(),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(v.getContext(),BookDetailActivity.class);
                    intent.putExtra("book_item",book);
                    mainActivity.bookShelfManager.getBookShelfList().clear();
                    for(BookShelf bookShelf_temp:mainActivity.bookShelfList)
                        mainActivity.bookShelfManager.getBookShelfList().add(bookShelf_temp);
                    mainActivity.bookShelfManager.save(v.getContext());

                    SharedPreferences sp=mainActivity.getSharedPreferences("labelList", Activity.MODE_PRIVATE);
                    Gson gson=new Gson();
                    String data=gson.toJson(mainActivity.labelList);
                    SharedPreferences.Editor editor=sp.edit();
                    editor.putString("label_List_json",data);
                    editor.commit();

                    mainActivity.startActivity(intent);

                }
            });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Book book=mBookList.get(position);
       Bitmap bitmap=ImageManager.GetLocalBitmap(mainActivity,book.getUuid());
        holder.bookImage.setImageBitmap(bitmap);
       // holder.bookImage.setImageResource(book.getImageId());  //!!!!!!!!!!!
        holder.bookName.setText(book.getBookName());
        holder.press.setText(book.getPressName());
        holder.press_time.setText(book.getPressTime());
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

//    public void setFilter(List<Book>filter_bookList){
//        mBookList=filter_bookList;
//        notifyDataSetChanged();
//
//    }
}


