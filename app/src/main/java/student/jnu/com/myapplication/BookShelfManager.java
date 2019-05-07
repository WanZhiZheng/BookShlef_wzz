package student.jnu.com.myapplication;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ASUS on 2019/4/16.
 */

public class BookShelfManager {
    public static String BOOKSHELFSFILENAME = "bookshelfs";
    private List<BookShelf> bookShelfList;

    public BookShelfManager() {
        this.bookShelfList = new ArrayList<BookShelf>();
    }

    public List<BookShelf> getBookShelfList() {

        return bookShelfList;
    }

    public void setBookShelfList(List<BookShelf> bookShelfList) {
        this.bookShelfList = bookShelfList;
    }

    public boolean save(Context context){
        String dirsrc = context.getFilesDir()+"";
        File dir = new File(dirsrc);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String src = context.getFilesDir() + "/" + BOOKSHELFSFILENAME;
        File outputFile = new File(src);

        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            Log.e("BookShelf", "save: 创建文件失败");
            return false;
        }

        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = new FileOutputStream(outputFile);
            oos = new ObjectOutputStream(fos);
            for(int i=0; i<bookShelfList.size();i++){
                oos.writeObject(bookShelfList.get(i));
            }
            oos.flush();
            return true;
        } catch (FileNotFoundException e) {
            Log.e("BookShelf", "save: 文件不存在");
            return false;
        } catch (IOException e) {
            Log.e("BookShelf", "save: 输入输出错误");
            return false;
        } finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                    Log.e("BookShelf", "save: 关闭fos失败");
                }
            }
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    Log.e("BookShelf", "save: 关闭oos失败");
                }
            }
        }
    }

    public boolean read(Context context){
        bookShelfList.clear();
        String src = context.getFilesDir() + "/" + BOOKSHELFSFILENAME;
        File inputFile = new File(src);
        if(!inputFile.exists()) return false;

        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(inputFile);
            ois = new ObjectInputStream(fis);
            BookShelf bookShelf = null;

            while( (bookShelf = (BookShelf) ois.readObject()) != null){
                bookShelfList.add(bookShelf);
            }
            return true;

        } catch (FileNotFoundException e) {
            Log.e("BookShelf", "read: 文件未找到");
            return false;
        } catch (IOException e) {
            Log.e("BookShelf", "read: 输入输出错误");
            return false;
        } catch (ClassNotFoundException e) {
            Log.e("BookShelf", "read: 类没有找到");
            return false;
        } finally {
            if(fis != null){
                try {
                    fis.close();
                } catch (IOException e) {
                    Log.e("BookShelf", "read: 关闭fis失败");
                }
            }
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    Log.e("BookShelf", "read: 关闭ois失败");
                }
            }
        }
    }

    public boolean deleteBookShelfsFile(Context context){
        String src = context.getFilesDir() + "/" + BOOKSHELFSFILENAME;
        File file = new File(src);
        if(!file.exists()) return false;
        return file.delete();
    }
}
