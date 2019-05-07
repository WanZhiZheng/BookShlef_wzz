package student.jnu.com.myapplication;

import java.util.Comparator;

/**
 * Created by ASUS on 2019/4/17.
 */

public class PressTimeComparable implements Comparator<Book> {
    @Override
    public int compare(Book book, Book t1) {
        String year1 = book.getPressTime().substring(0,4);
        int year_one = Integer.valueOf(year1);
        String year2 = t1.getPressTime().substring(0,4);
        int year_two = Integer.valueOf(year2);
        if(year_one>year_two){
            return 1;
        }else if(year_one<year_two){
            return -1;
        }else{
            String month1 = book.getPressTime().substring(4,6);
            int month_one = Integer.valueOf(month1);
            String month2 = book.getPressTime().substring(4,6);
            int month_two = Integer.valueOf(month2);
            if(month_one > month_two){
                return 1;
            }else if(month_one<month_two){
                return -1;
            }else{
                return 0;
            }
        }
    }
}
