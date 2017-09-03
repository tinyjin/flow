package com.youjinui.flow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import java.util.LinkedList;

import static com.youjinui.flow.FlowCanvas.hScroll;
import static com.youjinui.flow.FlowCanvas.vScroll;
import static com.youjinui.flow.FlowCreater.FlowCreateById;

public class FlowStudio extends AppCompatActivity implements OnTouchListener{

    public static LinkedList<Tag> Flows;
    public static FlowCanvas canvas;

    //Flow Editor를 위한 레이아웃 -> 다이얼로그 변수
    public static LayoutInflater infl;
    public static AlertDialog.Builder builder;

    public static Context context;

    public static View SideTitle;

    public static boolean LineSwitch;

    void setSideMenuTouch(){ //Flow Code 항목 터치 이벤트 추가
        View[] SideBtns = {
                findViewById(R.id.side_start),
                findViewById(R.id.side_end),
                findViewById(R.id.side_ready),
                findViewById(R.id.side_cheory),
                findViewById(R.id.side_input),
                findViewById(R.id.side_output),
                findViewById(R.id.side_condition),
                findViewById(R.id.side_repeat),
                findViewById(R.id.side_func_start),
                findViewById(R.id.side_func_end),
                findViewById(R.id.side_func_use),
        };

        for (View btn : SideBtns) {
            btn.setOnTouchListener(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flow_studio);

        LineSwitch = false;

        SideTitle = findViewById(R.id.side_title);

        context = this;

        // Flow Editor 사용을 위한 객체 초기화
        infl = getLayoutInflater();
        builder = new AlertDialog.Builder(this);
        Editors.InitEditorAll();


        //스크롤 제어를 위한 스크롤 위젯 초기화
        vScroll = (ScrollView)findViewById(R.id.vscroll);
        hScroll = (HorizontalScrollView)findViewById(R.id.hscroll);

        //사이드 메뉴 이벤트 등록
        setSideMenuTouch();

        Flows = new LinkedList<Tag>(); //플로우 차트 동적 리스트 생성

        // 도화지 추가 메소드
        canvas = new FlowCanvas(this); //플로우 도화지 생성

        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        Point size = new Point();
        display.getSize(size);
        int ScreenHeight = size.y;
        int ScreenWidth = size.x;

        ViewGroup parent_frame = (ViewGroup) findViewById(R.id.parent_frame); //도화지의 부모 뷰
        parent_frame.addView(canvas, new DrawerLayout.LayoutParams(
                ScreenWidth, ScreenHeight)); //도화지 스튜디오에 추가
        //끝
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) { //

        Log.v("FlowStudio", "x : "+event.getX()+", y : "+event.getY());

        int Id = v.getId();

        LeftDrawerEventListener(v, Id, event, v.getWidth());

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.flow_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.draw_line :
                LineSwitch = !LineSwitch;
                break;

            case R.id.flow_translate :
                Intent intent = new Intent(FlowStudio.this, FlowTranslator.class);
                intent.putExtra("code", FlowTranslator.TranslateC());
                startActivity(intent);
                break;

            case R.id.flow_compile :
                Intent intent_compiler = new Intent(FlowStudio.this, FlowCompiler.class);
                startActivity(intent_compiler);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LeftDrawerEventListener(View v, int id, MotionEvent e, float MenuWidth){ //도형 생성 감지 및 관리
        float x = e.getX();
        float y = e.getY();

        if(x > MenuWidth) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    DrawerLayout drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer_layout.closeDrawer(Gravity.LEFT);

                    ScrollView left_scroll = (ScrollView) findViewById(R.id.left_scroll);
                    left_scroll.requestDisallowInterceptTouchEvent(true);

                    break;

                case MotionEvent.ACTION_UP:

                    FlowCreateById(id, x, y, v);

                    break;
            }
        }
    }


}
