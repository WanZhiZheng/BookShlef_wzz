package student.jnu.com.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2019/4/15.
 */

public class BookShelf implements Serializable {
    private  String bookshelfName;
    private  List<Book> bookList;

    public BookShelf(String bookshelfName){
        this.bookshelfName=bookshelfName;
        this.bookList=new ArrayList<Book>();
    }

    public String getBookshelfName(){
        return  bookshelfName;
    }

    public List<Book> getBookList(){
        return bookList;
    }

    public void setBookshelfName(String bookshelfName){
        this.bookshelfName=bookshelfName;
    }

    public void setBookList(List<Book> bookList){
        this.bookList=bookList;
    }

}
