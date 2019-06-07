package student.jnu.com.myapplication;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.util.Comparator;

/**
 * Created by ASUS on 2019/4/17.
 */

public class PressNameComparator implements Comparator<Book> {
    @Override
    public int compare(Book book, Book t1) {
        String convert1 ="";
        for(int j=0;j<book.getPressName().length();j++){
            char word = book.getPressName().charAt(j);
            String[] pinyinArray1 = PinyinHelper.toHanyuPinyinStringArray(word);
            if(pinyinArray1!=null){
                convert1 += pinyinArray1[0].charAt(0);
            }else{
                convert1+=word;
            }
        }
        String result1 = convert1.toUpperCase();
        String convert2="";
        for(int j=0;j<t1.getPressName().length();j++){
            char word = t1.getPressName().charAt(j);
            String[] pinyinArray2 = PinyinHelper.toHanyuPinyinStringArray(word);
            if(pinyinArray2!=null){
                convert2 += pinyinArray2[0].charAt(0);
            }else{
                convert2+=word;
            }
        }
        String result2 = convert2.toUpperCase();
        return (result1.compareTo(result2));
    }
}
