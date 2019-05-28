package student.jnu.com.myapplication;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by ASUS on 2019/4/3.
 */

public class Book implements Serializable{
   // private int imageId;    //这里要改的
    private String uuid;
   // Bitmap book_bitmap;
    private String bookName;
    private String ISBN;
    private String author;
    private String pressName;
    private String pressTime;
    private String readingstate="未读";
    private String bookshelf;
    private List <String> labelList;
    private String note="here is note";
    private String label="计算机（标签）";
    private String bookresource="douban.com";


    public Book(){
        this.uuid= UUID.randomUUID().toString().replaceAll("-","");
        this.labelList=new ArrayList<String>();
        this.bookName="";
        this.ISBN="";
        this.author="";
        this.pressName="";
        this.pressTime="";
        this.bookshelf="默认书架";
    }

    public Book(String bookName,String ISBN,String author,String pressName,String pressTime,String bookshelf){
//        this.imageId=imageId;
        this.bookName=bookName;
        this.ISBN=ISBN;
        this.author=author;
        this.pressName=pressName;
        this.pressTime=pressTime;
        this.bookshelf=bookshelf;
        this.uuid= UUID.randomUUID().toString().replaceAll("-","");
        this.labelList=new ArrayList<String>();
    }

    public String getUuid(){
        return uuid;
    }

    public List<String> getLabelList(){
        return labelList;
    }

    public void setLabelList(List<String> labelList){
        this.labelList=labelList;
    }

    public String getBookName(){
        return bookName;
    }

    public String getISBN(){
        return ISBN;
    }

    public String getAuthor(){
        return author;
    }

    public String getPressName(){
        return pressName;
    }

    public String getPressTime(){
        return pressTime;
    }

    public String getReadingstate(){return readingstate;}

    public String getBookshelf(){return bookshelf;}

    public String getNote(){return note;}

    public String getBookresource(){return bookresource;}

    public String getLabel(){return label;}



    public void setUuid(String uuid){
        this.uuid=uuid;
    }

    public void setBookName(String bookName){this.bookName=bookName;}

    public void  setISBN(String isbn){this.ISBN=isbn;}

    public void setAuthor(String author){this.author=author;}

    public void setPressName(String pressName){this.pressName=pressName;}

    public void setPressTime(String pressTime){this.pressTime=pressTime;}

    public void setReadingstate(String readingstate){this.readingstate=readingstate;}

    public void setBookshelf(String bookshelf){this.bookshelf=bookshelf;}

    public void setNote(String note){this.note=note;}

    public void setLabel(String label){this.label=label;}

    public void setBookresource(String bookresource){this.bookresource=bookresource;}




}
