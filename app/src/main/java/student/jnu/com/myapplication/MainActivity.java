package student.jnu.com.myapplication;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,SearchView.OnQueryTextListener{
    private List<Book> bookList=new ArrayList<>();
    public List<BookShelf> bookShelfList=new ArrayList<>();
    List<String> labelList=new ArrayList<>();
    static int present_bookshelf_selection;
    static int present_sort_selection;
    static int present_label_selection;
    FloatingActionMenu fab;
    SearchView searchView;
    Spinner mTopSpinner;
    Toolbar toolbar;
    BookShelfManager bookShelfManager;
    AlertDialog alertDialog;
    int label_count;
    BookShelf bookShelf;
    CharSequence[] items={"扫描条形码","手动输入isbn码","手动添加书籍"};
    //String[] labels;
    //MenuItem menuItem;
    static boolean isShow_labelitem;
    static boolean isShow_bookshelfitem;
    NavigationView navigationView;
    //BookShelf bookShelf=new BookShelf("所有");
    //List<BookShelf> bookShelfList=new ArrayList<>();
    RecyclerView recyclerView_book;
    //String[] bookshelfList_=new String[]{"默认书架","书架1","书架2"};
    public BookAdapter bookAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //这里也要读入label   然后 label_count=labelList.size()

            bookShelfManager = new BookShelfManager();
            bookShelfManager.read(MainActivity.this);


        for(BookShelf temp:bookShelfManager.getBookShelfList())
            bookShelfList.add(temp);

        //读取labelList
        SharedPreferences sp=this.getSharedPreferences("labelList", Activity.MODE_PRIVATE);
        String labelList_json=sp.getString("label_List_json","");
        if(!labelList_json.equals("")){
            Gson gson=new Gson();
            Type listType=new TypeToken<List<String>>(){}.getType();
            labelList=gson.fromJson(labelList_json,listType);
            label_count=labelList.size();
        }

        Log.d("size",String.valueOf(labelList.size()));

            if (bookShelfList.size() == 0) {
                bookShelfList.add(new BookShelf("所有"));
                bookShelfList.add(new BookShelf("默认书架"));
                bookShelfList.add(new BookShelf("aaa"));
                bookShelfList.add(new BookShelf("添加书架"));
            }



            //加载主list
            if (bookShelfList.get(0).getBookList().size() == 0)
                initBooks();



        //加载上面的toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //加载右下角的添加fab
        fab=(FloatingActionMenu)findViewById(R.id.fab);
        fab.setClosedOnTouchOutside(true);

        com.github.clans.fab.FloatingActionButton add_single=(com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_add_single);
        com.github.clans.fab.FloatingActionButton add_batch=(com.github.clans.fab.FloatingActionButton)findViewById(R.id.fab_add_batch);

        add_single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle("选择图片");
                dialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                //扫描isbn

                                break;
                            case 1:
                                //手动输入isbn

                                break;
                            case 2:
                                //手动添加书籍
                                Intent intent=new Intent(MainActivity.this,BookEditActivity.class);
                                Book book=new Book("","","","","","");
                                intent.putExtra("book_item",book);
                                startActivity(intent);
                                break;
                        }
                    }
                });
                dialog.show();
                fab.close(true);

                //Toast.makeText(getApplicationContext(),"hhh",Toast.LENGTH_SHORT).show();
                //Intent intent=new Intent(MainActivity.this,BookEditActivity.class);
                //如果是手动添加，那就可以传一个空的book过去  避免在那边改
                //startActivity(intent);
                //这里看一下能不能 点击了按钮以后 取消蒙版 不行就算了   要看回github里面的一些使用说明
            }
        });

        add_batch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //isVisible=true;
                //invalidateOptionsMenu();


                fab.close(true);
            }
        });



        //加载上方书架选择的spinner
        int count=bookShelfList.size();
        String[] bookshelfList_names=new String[count-1];
        for(int i=0;i<count-1;i++){
            bookshelfList_names[i] = bookShelfList.get(i).getBookshelfName();
        }
        //SpinnerAdapter spinnerAdapter=ArrayAdapter.createFromResource(getApplicationContext(),R.array.category,R.layout.spinner_dropdown_item);
        mTopSpinner=new Spinner(getSupportActionBar().getThemedContext());
        ArrayAdapter<String> bookshelflist_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,bookshelfList_names);
        bookshelflist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        readingstate_spinner.setAdapter(bookshelflist_adapter);
        mTopSpinner.setAdapter(bookshelflist_adapter);
        toolbar.addView(mTopSpinner,0);
        mTopSpinner.setSelection(present_bookshelf_selection);


        mTopSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                present_bookshelf_selection=position;
                if(present_bookshelf_selection==0){
                    isShow_bookshelfitem=false;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                }else{
                    isShow_bookshelfitem=true;
                    toolbar.setBackgroundColor(getResources().getColor(R.color.LabelorBookshelf));

                }
                invalidateOptionsMenu();

                bookList.clear();
                for(Book temp:bookShelfList.get(present_bookshelf_selection).getBookList())
                    bookList.add(temp);
                    //更换书架的时候也是默认排序吖
                    sort(bookList,present_sort_selection);
                if(isShow_labelitem==true){
                    //如果现在也是选择了标签
                    String selected_label=labelList.get(present_label_selection);
                    toolbar.setBackgroundColor(getResources().getColor(R.color.LabelorBookshelf));
                    List<Book> temp_bookList=new ArrayList<>();
                    List<String> temp_labelList=new ArrayList<>();
                    for(int i=0;i<bookList.size();i++){
                        temp_labelList=bookList.get(i).getLabelList();
                        for(int j=0;j<temp_labelList.size();j++){
                            if(temp_labelList.get(j).equals(selected_label)){
                                temp_bookList.add(bookList.get(i));
                                break;
                            }
                        }
                    }
                    bookList.clear();
                    for(Book book:temp_bookList)
                        bookList.add(book);
                }

                bookAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //加载主列表
        recyclerView_book=(RecyclerView)findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        recyclerView_book.setLayoutManager(layoutManager);
        bookAdapter=new BookAdapter(bookList,MainActivity.this);
        recyclerView_book.setAdapter(bookAdapter);
        recyclerView_book.addOnScrollListener(new RecyclerView.OnScrollListener(){
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if(dy>0){
                    if(!fab.isMenuButtonHidden()){
                        fab.hideMenuButton(true);
                    }
                }else{
                    if(fab.isMenuButtonHidden()){
                        fab.showMenuButton(true);
                    }
                }

                //super.onScrolled(recyclerView, dx, dy);
            }
        });

        //bookshelf_spinner.getAdapter().getItem(present_bookshelfspinner_selection).toString()

        //加载左边的导航栏
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.getMenu().removeGroup(R.id.last_three);
        for(int i=0;i<labelList.size();i++){
            navigationView.getMenu().add(R.id.nav_add_label,i,1,labelList.get(i)).setIcon(R.drawable.ic_label).setCheckable(true);
        }
        navigationView.getMenu().add(R.id.last_three,2,2,"捐赠").setIcon(R.drawable.ic_donate).setCheckable(true);
        navigationView.getMenu().add(R.id.last_three,2,2,"设置").setIcon(R.drawable.ic_settings).setCheckable(true);
        navigationView.getMenu().add(R.id.last_three,2,2,"关于").setIcon(R.drawable.ic_about).setCheckable(true);
        if(isShow_labelitem){
            navigationView.getMenu().findItem(present_label_selection).setChecked(true);
        }

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        //加载SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
                searchView.setOnQueryTextListener(this);
//        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//            @Override
//            public boolean onClose() {
//                if(fab!=null){
//                    fab.setVisibility(View.VISIBLE);
//                    fab.showMenuButton(true);
//                }
//                return false;
//            }
//        });


        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem renameLabelItem=menu.findItem(R.id.rename_label);
        MenuItem deleteLabelItem=menu.findItem(R.id.delete_label);
        MenuItem renameBookshelfItem=menu.findItem(R.id.rename_bookshelf);
        MenuItem deleteBookshelfItem=menu.findItem(R.id.delete_bookshelf);

        renameLabelItem.setVisible(isShow_labelitem);
        deleteLabelItem.setVisible(isShow_labelitem);
        renameBookshelfItem.setVisible(isShow_bookshelfitem);
        deleteBookshelfItem.setVisible(isShow_bookshelfitem);

        if(isShow_labelitem){
            fab.setVisibility(View.GONE);
            fab.hideMenuButton(true);
        }else{
            fab.setVisibility(View.VISIBLE);
            fab.showMenuButton(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.searchable.
        int id = item.getItemId();

        switch (id){
            case R.id.sort:
                final String[] items = {"标题", "作者", "出版社", "出版时间"};
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("排序依据");

                alertBuilder.setSingleChoiceItems(items, present_sort_selection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
//                    Toast.makeText(MainActivity.this, items[i], Toast.LENGTH_SHORT).show();
                        present_sort_selection=position;
//                    if(items[i] == "标题"){
//                        sort_result.addAll(sort(bookList,1));
//                        count[0]++;
//                    }
//                    if (items[i] == "作者") {
//                        sort_result.addAll(sort(bookList,2));
//                        count[0]++;
//                    }
//                    if (items[i] == "出版社") {
//                        sort_result.addAll(sort(bookList,3));
//                        count[0]++;
//                    }
//                    if (items[i] == "出版时间") {
//                        sort_result.addAll(sort(bookList,4));
//                        count[0]++;
//                    }
                    }
                });

                alertBuilder.setPositiveButton("排序", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                    if(count[0] == 0){
//                        sort_result.addAll(sort(bookList,1));
//                    }
                        // +++东西！！
                        sort(bookList,present_sort_selection);
                        bookAdapter.notifyDataSetChanged();

                        alertDialog.dismiss();
                    }
                });



                alertDialog = alertBuilder.create();
                alertDialog.show();

                break;



            case R.id.rename_bookshelf:
                final EditText rename_bookshelf=new EditText(MainActivity.this);
                AlertDialog.Builder dialog_renamebookshelf=new AlertDialog.Builder(MainActivity.this);
                dialog_renamebookshelf.setTitle("更改书架名称");
                dialog_renamebookshelf.setCancelable(false);
                dialog_renamebookshelf.setView(rename_bookshelf);
                dialog_renamebookshelf.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newBookshelfName=rename_bookshelf.getText().toString();
                        //更改该书书架里面的书的书架的名称
                        refreshBookshelf(newBookshelfName);
                        //更新一下上面的spinner
                        refreshBookshelfSpinner(false);//传入参数是isdelete:boolean


                    }
                });
                dialog_renamebookshelf.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog_renamebookshelf.show();


                break;
            case R.id.delete_bookshelf:
                AlertDialog.Builder dialog_deletebookshelf=new AlertDialog.Builder(MainActivity.this);
                dialog_deletebookshelf.setTitle("删除书架");
                dialog_deletebookshelf.setMessage("确定删除该书架吗？");
                dialog_deletebookshelf.setCancelable(false);
                dialog_deletebookshelf.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //删除该书架
                        deleteBookshelf();
                        //更新上面的书架spinner
                        refreshBookshelfSpinner(true);//传入参数是isdelete:boolean
                    }
                });
                dialog_deletebookshelf.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                dialog_deletebookshelf.show();
                break;

            case R.id.rename_label:
                final EditText rename_label=new EditText(MainActivity.this);
                AlertDialog.Builder dialog_renameLabel=new AlertDialog.Builder(MainActivity.this);
                dialog_renameLabel.setTitle("更改标签名称");
                dialog_renameLabel.setView(rename_label);
                dialog_renameLabel.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newLabelName=rename_label.getText().toString();
                        refreshLabel(newLabelName);  //In BookShelfList
                        refreshLabelInNavigationView(newLabelName);
                    }
                });

                dialog_renameLabel.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });


                dialog_renameLabel.show();


                break;

            case R.id.delete_label:
                AlertDialog.Builder dialog_deleteLabel=new AlertDialog.Builder(MainActivity.this);
                dialog_deleteLabel.setTitle("删除标签");
                dialog_deleteLabel.setMessage("确定删除该标签吗？");
                dialog_deleteLabel.setCancelable(false);
                dialog_deleteLabel.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteLabel();
                        refreshLabelInNavigationView();

                    }
                });
                dialog_deleteLabel.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog_deleteLabel.show();


                break;
        }



        return true;
    }


    private void deleteLabel(){
        String oldLabel=labelList.get(present_label_selection);
        labelList.remove(present_label_selection);
        for(int i=0;i<bookShelfList.get(0).getBookList().size();i++){
            for(int j=0;j<bookShelfList.get(0).getBookList().get(i).getLabelList().size();j++){
                if(bookShelfList.get(0).getBookList().get(i).getLabelList().get(j).equals(oldLabel)){
                    bookShelfList.get(0).getBookList().get(i).getLabelList().remove(j);
                    break;
                }
            }
        }
    }

    private void refreshLabelInNavigationView(){
        navigationView.getMenu().findItem(R.id.nav_bookshelf).setChecked(true);
        navigationView.getMenu().removeItem(present_label_selection);
        if(isShow_labelitem){
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            isShow_labelitem=false;
            invalidateOptionsMenu();
            bookList.clear();
            for(Book temp:bookShelfList.get(present_bookshelf_selection).getBookList())
                bookList.add(temp);
            //更换书架的时候也是默认排序吖
            sort(bookList,present_sort_selection);
            bookAdapter.notifyDataSetChanged();
        }
    }

    private void refreshLabelInNavigationView(String newLabelName){
        navigationView.getMenu().findItem(present_label_selection).setTitle(newLabelName);
    }

    private void refreshLabel(String newLabelName){
        String oldLabelName=labelList.get(present_label_selection);
        labelList.set(present_label_selection,newLabelName);
        //在所有书架里面 改变书的那个labelList  其他书架会跟着改， 因为这里应用是一样的
        for(int i=0;i<bookShelfList.get(0).getBookList().size();i++){
            for(int j=0;j<bookShelfList.get(0).getBookList().get(i).getLabelList().size();j++){
                if(bookShelfList.get(0).getBookList().get(i).getLabelList().get(j).equals(oldLabelName)){
                    bookShelfList.get(0).getBookList().get(i).getLabelList().set(j,newLabelName);
                    break;
                }
            }
        }
    }



    private void deleteBookshelf(){
        String delete_bookshelfname=bookShelfList.get(present_bookshelf_selection).getBookshelfName();
        bookShelfList.remove(present_bookshelf_selection);
        for(int i=0;i<bookShelfList.get(0).getBookList().size();i++){
            if(bookShelfList.get(0).getBookList().get(i).getBookshelf().equals(delete_bookshelfname)){
                bookShelfList.get(0).getBookList().remove(i);
            }
        }
    }




    private void refreshBookshelf(String newBookshelfName){
        String oldbookshelfname=bookShelfList.get(present_bookshelf_selection).getBookshelfName();
        bookShelfList.get(present_bookshelf_selection).setBookshelfName(newBookshelfName);
        //在该书架内所有书籍的书架名称更改
        for(int i=0;i<bookShelfList.get(present_bookshelf_selection).getBookList().size();i++){
            bookShelfList.get(present_bookshelf_selection).getBookList().get(i).setBookshelf(newBookshelfName);
        }
        //在所有书架内
        for(int j=0;j<bookShelfList.get(0).getBookList().size();j++){
            if(bookShelfList.get(0).getBookList().get(j).getBookshelf().equals(oldbookshelfname)){
                bookShelfList.get(0).getBookList().get(j).setBookshelf(newBookshelfName);
            }
        }
    }

    private void refreshBookshelfSpinner(boolean isdelete){

            //刷新下拉框spinner内的内容

            int count=bookShelfList.size();
            String[] bookshelfList_names=new String[count-1];
            for(int i=0;i<count-1;i++){
                bookshelfList_names[i] = bookShelfList.get(i).getBookshelfName();
            }
            ArrayAdapter<String> bookshelflist_adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,bookshelfList_names);
            bookshelflist_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mTopSpinner.setAdapter(bookshelflist_adapter);
            //标记当前书架选择
            if(!isdelete)
            mTopSpinner.setSelection(present_bookshelf_selection,true);
            else
            mTopSpinner.setSelection(0);


    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_bookshelf) {
            if(isShow_labelitem){
                toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                isShow_labelitem=false;
                invalidateOptionsMenu();
                bookList.clear();
                for(Book temp:bookShelfList.get(present_bookshelf_selection).getBookList())
                    bookList.add(temp);
                //更换书架的时候也是默认排序吖
                sort(bookList,present_sort_selection);
                bookAdapter.notifyDataSetChanged();
            }



            // Handle the camera action
        } else if (id == R.id.nav_search) {

        } else if (id == R.id.nav_add_label) {
            final EditText label_name_edit=new EditText(MainActivity.this);
            label_name_edit.setHint("请输入标签名称");
            AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("添加标签");
            dialog.setCancelable(false);
            dialog.setView(label_name_edit);

            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    navigationView.getMenu().removeGroup(R.id.last_three);


                    navigationView.getMenu().add(R.id.nav_add_label,label_count,1,label_name_edit.getText().toString()).setIcon(R.drawable.ic_label).setCheckable(true);
                    label_count++;
                    labelList.add(label_name_edit.getText().toString());
                    navigationView.getMenu().add(R.id.last_three,R.id.nav_donate,2,"捐赠").setIcon(R.drawable.ic_donate).setCheckable(true);
                    navigationView.getMenu().add(R.id.last_three,R.id.nav_setting,2,"设置").setIcon(R.drawable.ic_settings).setCheckable(true);
                    navigationView.getMenu().add(R.id.last_three,R.id.nav_about,2,"关于").setIcon(R.drawable.ic_about).setCheckable(true);
//                    navigationView.getMenu().a
                }
            });
            dialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });

            dialog.show();
        } else if (id == R.id.nav_donate) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_about) {

        }else if(id>=0 && id< labelList.size()){
            present_label_selection=id;
            isShow_labelitem=true;
            invalidateOptionsMenu();
            toolbar.setBackgroundColor(getResources().getColor(R.color.LabelorBookshelf));
            //toolbar颜色 要不要变一下
            refreshBookListByLabel();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (id==R.id.nav_search) {
            searchView.setIconified(false);   //设置 searchview 打开，同时右侧有× 搜索框可以关闭
            ///searchView.onActionViewExpanded();  //这个也是打开，只是搜索框不能关闭
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void refreshBookListByLabel(){
        String selected_label=labelList.get(present_label_selection);

        bookList.clear();
        for(Book temp:bookShelfList.get(present_bookshelf_selection).getBookList())
            bookList.add(temp);
        //更换书架的时候也是默认排序吖
        sort(bookList,present_sort_selection);

        List<Book> temp_bookList=new ArrayList<>();
        List<String> temp_labelList=new ArrayList<>();
            for(int i=0;i<bookList.size();i++){
               temp_labelList=bookList.get(i).getLabelList();
                for(int j=0;j<temp_labelList.size();j++){
                    if(temp_labelList.get(j).equals(selected_label)){
                        temp_bookList.add(bookList.get(i));
                        break;
                    }
                }
            }
        bookList.clear();
        for(Book book:temp_bookList)
            bookList.add(book);
        bookAdapter.notifyDataSetChanged();

    }


    //对搜索内容进行匹配，返回一个匹配关键字成功 过滤后的一组书籍
    private List<Book> filter(List<Book>bookList,String text){
        List<Book>filter_bookList=new ArrayList<Book>();

        for (Book book:bookList){
            if (book.getBookName().contains(text) || book.getPressName().contains(text))
                //这里实现书名和出版社的搜索关键字
                filter_bookList.add(book);
        }
        return filter_bookList;
    }

    //排序按钮

    /**
     * type：
     * 0、标题
     * 1、作者
     * 2、出版社
     * 3、出版时间
     */
    public void sort(List<Book> bookList,int type){
        List<Book> sort_bookList = new ArrayList<Book>();
//        new AuthorNameComparable();
        sort_bookList=bookList;
        if(type==0) {
            Collections.sort(sort_bookList, new BookNameComparable());
        }
        if(type==1){
            Collections.sort(sort_bookList,new AuthorNameComparable());
        }
        if(type==2){
            Collections.sort(sort_bookList,new PressNameComparable());
        }
        if(type==3){
            Collections.sort(sort_bookList,new PressTimeComparable());
        }

       // return sort_bookList;
    }

    //初始化书列表
    private void initBooks(){
        for(int i=0;i<3;i++){
            Book book=new Book("六论自发性","9787520142625","詹姆斯·C·斯科特","社会科学文献出版社","2019-04","aaa");
            bookShelfList.get(0).getBookList().add(book);
            bookShelfList.get(2).getBookList().add(book);
            Bitmap bitmap=ImageManager.decodeSampledBitmapFromResource(getResources(),R.drawable.book_pic);
            ImageManager.SaveImage(getApplicationContext(),bitmap,book.getUuid());



            Book book_1=new Book("C Primer Plus","9787115390592","Stephen Prata","人民邮电出版社","2016-01","默认书架");
            bookShelfList.get(0).getBookList().add(book_1);
            bookShelfList.get(1).getBookList().add(book_1);
            Bitmap bitmap1=ImageManager.decodeSampledBitmapFromResource(getResources(),R.drawable.book1_pic);
            ImageManager.SaveImage(getApplicationContext(),bitmap1,book_1.getUuid());



            Book book_2=new Book("人工智能原理：一种现代的方法","9787302331094","罗素、诺维格","清华大学出版社","2013-11","aaa");
            bookShelfList.get(0).getBookList().add(book_2);
            bookShelfList.get(2).getBookList().add(book_2);
            Bitmap bitmap2=ImageManager.decodeSampledBitmapFromResource(getResources(),R.drawable.book2_pic);
            ImageManager.SaveImage(getApplicationContext(),bitmap2,book_2.getUuid());


            Book book_3=new Book("敏捷软件开发","9787302071976","Robert C·Martin","清华大学出版社","2003-09","默认书架");
            bookShelfList.get(0).getBookList().add(book_3);
            bookShelfList.get(1).getBookList().add(book_3);
            Bitmap bitmap3=ImageManager.decodeSampledBitmapFromResource(getResources(),R.drawable.book3_pic);
            ImageManager.SaveImage(getApplicationContext(),bitmap3,book_3.getUuid());


        }
//        bitmap.recycle();
//        bitmap=null;
//        bitmap1.recycle();
//        bitmap1=null;
//        bitmap2.recycle();
//        bitmap2=null;
//        bitmap3.recycle();
//        bitmap3=null;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    //监听搜索框内容变化
    @Override
    public boolean onQueryTextChange(String newText) {
        //得到过滤后的一组书籍

//        List<Book> temp_bookList=new ArrayList<>();
//        for(Book book:bookList)
//            temp_bookList.add(book);
            List<Book> filter_bookList;
            if(!isShow_labelitem) {
                filter_bookList = filter(bookShelfList.get(present_bookshelf_selection).getBookList(), newText);
                //将过滤后的结果set进adapter 显示
                //bookAdapter.setFilter(filter_bookList);
//                bookList.clear();
//                for (Book book : filter_bookList)
//                    bookList.add(book);
            }else{
                List<Book> temp_bookList=new ArrayList<>();
                for(int i=0;i<bookShelfList.get(present_bookshelf_selection).getBookList().size();i++){
                    for(int j=0;j<bookShelfList.get(present_bookshelf_selection).getBookList().get(i).getLabelList().size();j++){
                        if(bookShelfList.get(present_bookshelf_selection).getBookList().get(i).getLabelList().get(j).equals(labelList.get(present_label_selection))){
                            temp_bookList.add(bookShelfList.get(present_bookshelf_selection).getBookList().get(i));
                            break;
                        }
                    }
                }
                filter_bookList = filter(temp_bookList, newText);
            }
        bookList.clear();
        for (Book book : filter_bookList)
            bookList.add(book);
        //将过滤后的结果set进adapter 显示
        //搜索内容之后也要根据排列顺序去排列一次
        sort(bookList,present_sort_selection);
       bookAdapter.notifyDataSetChanged();
        return true;

    }



    @Override
    protected void onDestroy() {
//        bookShelfManager.getBookShelfList().clear();
//        for(BookShelf bookShelf_temp:bookShelfList)
//            bookShelfManager.getBookShelfList().add(bookShelf_temp);
//        bookShelfManager.save(getApplicationContext());
        super.onDestroy();
    }
}
