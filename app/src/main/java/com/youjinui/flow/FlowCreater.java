package com.youjinui.flow;

import android.graphics.Path;
import android.util.Log;
import android.view.View;

import java.util.UUID;

import static com.youjinui.flow.FlowStudio.Flows;
import static com.youjinui.flow.FlowStudio.SideTitle;
import static com.youjinui.flow.FlowStudio.canvas;

/**
 * 플로우 도형 생성자
 */

public class FlowCreater {

    public static void FlowCreateById(int id, float x, float y, View v){
        FlowTag.findTagClassById(id);

        String Str = "";

        // X, Y, Width, Height 변수 초기화
        int index = Flows.size() - 1;
        Log.v("FlowCreater", "index: " + index);

        Tag tag = Flows.get(index);
        tag.SetX( getAbsoluteXforSideMenu(v, x) );
        tag.SetY( getAbsoluteYforSideMenu(v, y) );

        tag.SetText(Str);
        tag.SetKind(id);
        tag.SetUUID(GetFlowUUID());

        tag.SetNext(Str);
        tag.SetPrev(Str);
        tag.SetLinePath(new Path());

        tag.ConditionEndable = false;

        Log.v("FlowCreater","UUID : "+ GetFlowUUID());

        tag.SetPath(FlowDrawer.findDrawPathById(index) ); // Path 초기화 findDrawPathById

        canvas.invalidate(); //도화지 갱신
    }

    static String GetFlowUUID(){
        return UUID.randomUUID().toString();
    }

    static float getAbsoluteYforSideMenu(final View v, final float y){ //사이드 바 사용을 위한 Y절대 좌표 반환 함수
        float AbsoluteY = y;

        int titleBottom = SideTitle.getBottom(); //flow code 타이틀의 높이
        int thisTop = v.getTop(); //현재 메뉴와 타이틀 사이의 간격

        AbsoluteY+= titleBottom + thisTop;

        return AbsoluteY;
    }

    static float getAbsoluteXforSideMenu(final View v, final float x){ //사이드 바 사용을 위한 X절대 좌표 반환 함수
        float AbsoluteX = x;

        int thisRight = v.getRight();

        AbsoluteX -= thisRight;

        return AbsoluteX;
    }

}
