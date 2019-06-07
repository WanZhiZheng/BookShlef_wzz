package student.jnu.com.myapplication;

import java.util.Comparator;

/**
 * Created by ASUS on 2019/4/17.
 */

public class PressTimeComparator implements Comparator<Book> {
    @Override
    public int compare(Book book, Book t1) {
        int year_one,year_two;
        String year1 = book.getPressTime();
        if(year1==null || year1==""){
            year_one=0;
        }else {
            year_one = Integer.valueOf(year1.substring(0,4));
        }
        String year2 = t1.getPressTime();
        if(year2==null || year2==""){
            year_two=0;
        }else {
            year_two = Integer.valueOf(year2.substring(0,4));
        }

        if(year_one==0 && year_two==0){
            return 0;
        }else if(year_one>year_two){
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
