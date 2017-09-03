package com.youjinui.flow;

import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.youjinui.flow.FlowStudio.canvas;

/**
 * 플로우 선 그리기 도구
 */

public class FlowLiner {
    public static boolean LineFlag = false; //true 는 라인 그리는 중, false는 라인 안 그리는 중;
    static Tag tmpTag; //임시 태그 : 선을 그리기 시작한 도형객체
    static float tmpX, tmpY;

    static List<Float> xList;
    static List<Float> yList;

    static void StartFlowLineDraw(Tag tag){ //플로우 라인 시작
        tmpTag = tag; //선 그리기 시작한 태그 변경

        tmpX = tag.GetCenterHorizontal();
        tmpY = tag.GetCenterVertical();

        //x y 경로 배열 생성
        xList = new ArrayList();
        yList = new ArrayList();

        //연결 선 수정시 Next, Prev 해제
        tag.SetNext("");

        String findUUID = tag.GetNext();
        Iterator<Tag> itr = FlowStudio.Flows.iterator();

        while(itr.hasNext()){
            Tag findTag = itr.next();
            if(findTag.GetUUID().equals(findUUID)){
                findTag.SetPrev("");
            }
        }

        UpdateList();
        LineFlag = true; //선 그리는 중으로 변경
    }

    static void EndFlowLineDraw(Tag tag, float x, float y){ //플로우 라인 도착
        //Prev, Next 설정

        if(!tag.GetPrev().equals("")){
            tag.ConditionEndable = true;
        }

        tmpTag.SetNext(tag.GetUUID());
        tag.SetPrev(tmpTag.GetUUID());

        DrawEndLine(tag, x, y);

        LineFlag = false;
    }

    static void DrawLinePath(float x, float y){ // 경로 작성 // 직선 판정, x>y : X 직선, y>x : Y 직선
        float absX, absY;

        absX = Math.abs(tmpX - x);
        absY = Math.abs(tmpY - y);

        if(absX > absY) { //X
            tmpX = x;
            UpdateList();
        }
        else { //Y
            tmpY = y;
            UpdateList();
        }
    }

    //해당 중앙 축보다 좀 더 긴 경우에는, 축보다 짧게 만들고 축의 중앙까지 이동
    //축의 중앙까지 이동 후 도형의 정중앙까지 Draw
    //x, y 배열의 수정에서
    // Left & Right에 가까운 경우 : Y를 CenterVertical 보다 작게
    // Top & Bottom에 가까운 경우 : X를 CenterHorizontal 보다 작게
    static void DrawEndLine(Tag tag,float x,float y) { //라인 끝 맺기

        float top = tag.GetTop();
        float bottom = tag.GetBottom();
        float left = tag.GetLeft();
        float right = tag.GetRight();

        if(top>tmpY  && top<y && x<right && x>left){ //Top에 연결되는 경우
            tmpY = top;
        }
        else if(bottom>y && bottom<tmpY && x<right && x>left){ //Bottom에 연결되는 경우
            tmpY = bottom;
        }
        else if(left>tmpX && left<x && y>top && y<bottom){ //Left에 연결되는 경우
            tmpX = left;
        }
        else if(right>x && right<tmpX && y>top && y<bottom){ //Right에 연결되는 경우
            tmpX = right;
        }

        UpdateList();
    }

    static void UpdateList(){ //좌표 리스트 갱신
        xList.add(tmpX);
        yList.add(tmpY);
        SaveList();
    }

    static void SaveList(){
        tmpTag.SetLinePathX(xList);
        tmpTag.SetLinePathY(yList);
    }

    static void CreateLinePath(){
        Iterator<Float> itrX = xList.iterator();
        Iterator<Float> itrY = yList.iterator();

        Path path = new Path();
        path.moveTo(xList.get(0), yList.get(0));

        while(itrX.hasNext() & itrY.hasNext()){
            float x = itrX.next();
            float y = itrY.next();

            path.lineTo(x, y);
        }

        tmpTag.SetLinePath(path);
        canvas.invalidate();
    }

    static void CreateLineArrow(Tag tag){
        float top = tag.GetTop();
        float bottom = tag.GetBottom();
        float left = tag.GetLeft();
        float right = tag.GetRight();

        float size = FlowResolution.Liner.arrow;

        Path path = tmpTag.GetLinePath();

        if(tmpX == left){
            path.moveTo(left, tmpY);
            path.rLineTo(-size, -size);
            path.moveTo(left, tmpY);
            path.rLineTo(-size, size);
        }
        else if(tmpX == right){
            path.moveTo(right, tmpY);
            path.rLineTo(size, -size);
            path.moveTo(right, tmpY);
            path.rLineTo(size, size);
        }
        else if(tmpY == top){
            path.moveTo(tmpX, top);
            path.rLineTo(-size, -size);
            path.moveTo(tmpX, top);
            path.rLineTo(size, -size);
        }
        else if(tmpY == bottom){
            path.moveTo(tmpX, bottom);
            path.rLineTo(-size, size);
            path.moveTo(tmpX, bottom);
            path.rLineTo(size, size);
        }

        tmpTag.SetLinePath(path);
    }

    static void CreateLineArrow(Tag startTag, Tag endTag, float x, float y){ //Condition 라인 무브에서 선이동 후 다음 도형 배치때문에 생기는 오류
        float top = endTag.GetTop();
        float bottom = endTag.GetBottom();
        float left = endTag.GetLeft();
        float right = endTag.GetRight();

        float size = FlowResolution.Liner.arrow;

        Path path = startTag.GetLinePath();

        if(x == left){
            path.moveTo(left, y);
            path.rLineTo(-size, -size);
            path.moveTo(left, y);
            path.rLineTo(-size, size);
        }
        else if(x == right){
            path.moveTo(right, y);
            path.rLineTo(size, -size);
            path.moveTo(right, y);
            path.rLineTo(size, size);
        }
        else if(y == top){
            path.moveTo(x, top);
            path.rLineTo(-size, -size);
            path.moveTo(x, top);
            path.rLineTo(size, -size);
        }
        else if(y == bottom){
            path.moveTo(x, bottom);
            path.rLineTo(-size, size);
            path.moveTo(x, bottom);
            path.rLineTo(size, size);
        }

        startTag.SetLinePath(path);
    }

    static void ConditionLineCheck(){
        Tag tag = tmpTag;

        int id = tag.GetKind();
        if(id != R.id.side_condition) return;

        Log.v("FlowLiner", "Condition Line Check");

        FlowTag.Condition condition = (FlowTag.Condition)tag;

        float x1, x2;

        x1 = condition.GetLinePathX().get(0);
        x2 = condition.GetLinePathX().get(1);

        if(x1 >  x2) { //YES 연결된 경우
            //바꿔줘야하는것 YesNext, YesLineX,Y , YesLine
            condition.YesNext = condition.GetNext();
            condition.YesXList = condition.GetLinePathX();
            condition.YesYList = condition.GetLinePathY();
            condition.YesLine = condition.GetLinePath();

            Log.v("FlowLiner", "Yes Line");
        }
        else { //No 연결된 경우
            condition.NoNext = condition.GetNext();
            condition.NoXList = condition.GetLinePathX();
            condition.NoYList = condition.GetLinePathY();
            condition.NoLine = condition.GetLinePath();
            Log.v("FlowLiner", "No Line");

        }
    }

}
