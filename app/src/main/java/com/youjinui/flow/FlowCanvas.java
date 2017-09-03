package com.youjinui.flow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.youjinui.flow.FlowDrawer.findDrawPathById;
import static com.youjinui.flow.FlowEditor.findEditorById;
import static com.youjinui.flow.FlowLiner.ConditionLineCheck;
import static com.youjinui.flow.FlowLiner.CreateLineArrow;
import static com.youjinui.flow.FlowLiner.CreateLinePath;
import static com.youjinui.flow.FlowLiner.DrawLinePath;
import static com.youjinui.flow.FlowLiner.EndFlowLineDraw;
import static com.youjinui.flow.FlowLiner.LineFlag;
import static com.youjinui.flow.FlowLiner.StartFlowLineDraw;
import static com.youjinui.flow.FlowMover.CheckDisConnectSpecialFlow;
import static com.youjinui.flow.FlowMover.CheckInSpecialFlow;
import static com.youjinui.flow.FlowMover.FlowMove;
import static com.youjinui.flow.FlowResolution.PaintResolution.strokeWidthP;
import static com.youjinui.flow.FlowResolution.PaintResolution.textSizeP;
import static com.youjinui.flow.FlowStudio.Flows;
import static com.youjinui.flow.FlowStudio.LineSwitch;
import static com.youjinui.flow.FlowStudio.canvas;

/**
 * 플로우 도화지(작업 공간)
 */

public class FlowCanvas extends View {

    public static Paint paint;
    public static Paint TextPaint;
    public static Paint LinePaint;

    //CanvasEventListener를 위한 Flag 변수
    private static boolean TouchFlag; //빈공간 터치를 막기 위한 변수
    private static boolean MoveFlag;
    public static int TagFlag;

    //스크롤 제어를 위한 수평/수직 스크롤
    public static ScrollView vScroll;
    public static HorizontalScrollView hScroll;

    private float xGap, yGap; //매끄러운 이동을 위한 실제 터치 좌표와, 도형의 원점 간 차이

    public static float DownX, DownY; //터치 민감도 처리를 위한 ACTION_DOWN시 좌표

    public FlowCanvas(Context context){ //도화지 재료 설정
        super(context);

        TouchFlag = false;
        MoveFlag = false;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth( strokeWidthP );

        TextPaint = new Paint();
        TextPaint.setColor(Color.BLACK);
        TextPaint.setTextSize( textSizeP );
        TextPaint.setAntiAlias(true);

        LinePaint = new Paint();
        LinePaint.setColor(Color.BLACK);
        LinePaint.setStrokeWidth(3);
        LinePaint.setStyle(Paint.Style.STROKE);
        LinePaint.setAntiAlias(true);
    }


    @Override
    protected void onDraw(Canvas canvas) { //그리기
        super.onDraw(canvas);

        Iterator<Tag> itr = Flows.iterator(); //순회 탐색 준비
        while(itr.hasNext()){ //리스트 순회 탐색
            Log.v("FlowCanvas","drawStart");

            Tag tag = itr.next();
            Path path = tag.GetPath(); //경로 가져오기
            int id = tag.GetKind();

            //선 그리기
            if(id == R.id.side_condition){
                FlowTag.Condition condition = (FlowTag.Condition)tag;
                if(!condition.GetYesNext().equals(""))
                    canvas.drawPath(condition.GetYesLinePath(), LinePaint);
                if(!condition.GetNoNext().equals(""))
                    canvas.drawPath(condition.GetNoLinePath(), LinePaint);
            }

            canvas.drawPath(tag.GetLinePath(), LinePaint);

            //도형 그리기
            paint.setColor(Color.WHITE); //배경색 설정
            paint.setStyle(Paint.Style.FILL); //그리기 모드 : 배경
            canvas.drawPath(path, paint);

            paint.setColor(Color.BLACK); //선색 설정
            paint.setStyle(Paint.Style.STROKE); //그리기 모드 : 테두리
            canvas.drawPath(path, paint);

            String text = tag.GetText();

            if(!text.isEmpty()){ //텍스트가 들어 있다면 drawText
                if(tag.GetKind() == R.id.side_repeat){ //반복 코드 따로 텍스트 지정
                    drawRepeatText(canvas, tag);
                }
                else {
                    float TextWidth = TextPaint.measureText(text);
                    float TextHeight = TextPaint.descent() + TextPaint.ascent();

                    float aliginX = tag.GetX() + (tag.GetWidth() - TextWidth) / 2; //X축 정렬
                    float aliginY = tag.GetY() + (tag.GetHeight() - TextHeight) / 2; //Y축 정렬
                    //Repeat 정렬 작성

                    canvas.drawText(text, aliginX, aliginY, TextPaint);
                }
            }

        }

    }

    private void drawRepeatText(Canvas canvas, Tag tag){
        String text = tag.GetText();
        float TextWidth = TextPaint.measureText(text);
        float TextHeight= TextPaint.descent() + TextPaint.ascent();

        float aliginX = tag.GetX() + (tag.GetWidth() - TextWidth)/2; //X축 정렬
        float aliginY = tag.GetY() + (FlowResolution.Repeat.titleGap - TextHeight)/2; //Y축 정렬
        //Repeat 정렬 작성

        canvas.drawText(text, aliginX, aliginY, TextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return CanvasEventListener(event);
    }

    private void FlagVarInit(){ //플래그 변수 초기화
        if(MoveFlag) MoveFlag = false;

        if(TouchFlag) TouchFlag = false;
    }

    private boolean CanvasEventListener(MotionEvent e){ //도형 움직이기 감지 및 관리
        float x = e.getX();
        float y = e.getY();

        switch ( e.getAction() ) {
            case MotionEvent.ACTION_DOWN: //플로우 도형 터치 여부 확인
                Log.v("FlowStudio", "canvas downed");

                FlagVarInit(); //플래그 변수 초기화

                for(int i = Flows.size() -1; i >= 0; i--) { //Flow Zorder를 위한 역 탐색
                    Tag findTag = Flows.get(i);

                    if ( TouchFlag = findTag.isValidAtTouch(x, y) ) { //터치한 공간에 플로우가 있다면
                        TagFlag = Flows.indexOf(findTag);

                        xGap = x - findTag.GetLeft();
                        yGap = y - findTag.GetTop();

                        //스크롤 터치 권한 회수
                        vScroll.requestDisallowInterceptTouchEvent(true);
                        hScroll.requestDisallowInterceptTouchEvent(true);

                        break;
                    }

                }

                DownX = x;
                DownY = y;

                if(LineSwitch){ //선 그리기 상태라면
                    if(!LineFlag && TouchFlag) { //라인을 그리기 시작했다면
                        StartFlowLineDraw(Flows.get(TagFlag));
                        CreateLinePath();
                    }
                    else if(LineFlag && !TouchFlag) { //선 경로를 작성중이라면
                        DrawLinePath(x, y);
                        CreateLinePath();
                    }
                    else if(LineFlag && TouchFlag) { //선 그리기가 끝났다면
                        EndFlowLineDraw(Flows.get(TagFlag), x, y);
                        CreateLinePath();
                        CreateLineArrow(Flows.get(TagFlag));
                        ConditionLineCheck();
                    }

                }

                return TouchFlag;

            case MotionEvent.ACTION_MOVE:

                if(TouchFlag && TouchSensitivityJudgment(x, y) && !LineSwitch){
                    MoveFlag = true;
                    FlowMove(TagFlag, x - xGap , y - yGap);
                }

                return true;

            case MotionEvent.ACTION_UP:

                if(TouchFlag && !MoveFlag && !LineSwitch){
                    StartFlowEditor(TagFlag);
                }

                if(MoveFlag){//움직이고 있었따면
                    CheckDisConnectSpecialFlow(Flows.get(TagFlag));
                    CheckInSpecialFlow(Flows.get(TagFlag), x, y); //스페셜 플로우 탐색

                    //스크롤 터치 권한 부여
                    vScroll.requestDisallowInterceptTouchEvent(false);
                    hScroll.requestDisallowInterceptTouchEvent(false);
                }

                if(LineSwitch && TouchFlag){
                    Log.v("FlowCanvas","id : "+Flows.get(TagFlag).GetUUID());
                    Log.v("FlowCanvas", "Next :"+Flows.get(TagFlag).GetNext());
                    Log.v("FlowCanvas", "Prev :"+Flows.get(TagFlag).GetPrev());

                }

                return true;
        }

        return false;
    }

    private void StartFlowEditor(int index){ //eTag = Editable Tag
        Log.v("FLowStudio", "Start Flow Editor index : "+index);
        findEditorById(index);
    }

    private boolean TouchSensitivityJudgment(float x, float y){ //터치 민감도 판정
        if(Math.abs(DownX - x) > 10 && Math.abs(DownY - y) > 10) return true;
        return false;
    }
}
