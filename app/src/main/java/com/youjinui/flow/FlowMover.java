package com.youjinui.flow;

import android.graphics.Path;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.youjinui.flow.FlowDrawer.findDrawPathById;
import static com.youjinui.flow.FlowLiner.CreateLineArrow;
import static com.youjinui.flow.FlowStudio.Flows;
import static com.youjinui.flow.FlowStudio.canvas;

/**
 * 플로우 이동 도구
 */

public class FlowMover {

    static boolean changeFlag = false;
    static Tag changeTagFlag;

    public static void EditFlowZOrder(Tag tag){
        if(!Flows.getLast().equals(tag)){
            Tag flagTag = Flows.get(FlowCanvas.TagFlag);
            Flows.remove(tag);
            Flows.add(tag);
            FlowCanvas.TagFlag = Flows.indexOf(flagTag);
        }
    }


    public static void FlowMove(int index, float x, float y){ //x & y : header가 올 위치, index : header
        Log.v("FlowStudio", "Flow Chart Moved");

        Tag header = Flows.get(index);

        //이동량 구하기
        float moveGapX, moveGapY;
        moveGapX = header.GetX() - x;
        moveGapY = header.GetY() - y;

        PrevDisconnect(header);

        header.SetX(x);
        header.SetY(y);

        EditFlowZOrder(header);
        CheckSpecialFlow(header, moveGapX, moveGapY); //header에 대한 special 도형 검사

        Tag tag = header; //선 기반 탐색을 위한 header 저장

        if(changeFlag){
            tag = changeTagFlag;
            changeFlag = false;
        }

        //Tag에 ListX ListY 선언후 리스트 좌표를 기반으로 선움직이기
        while(!tag.GetNext().equals("")){
            Tag rootTag = Tag.findTagByUUID(tag.GetNext()); //연결된 다음 도형 가져오기

            rootTag.SetX(rootTag.GetX() - moveGapX);
            rootTag.SetY(rootTag.GetY() - moveGapY);
            rootTag.SetPath(findDrawPathById(Flows.indexOf(rootTag)));

            EditFlowZOrder(rootTag);
            CheckSpecialFlow(rootTag, moveGapX, moveGapY); //rootTag에 대한 special 도형 검사

            //선 이동
            MoveLinePath(tag, rootTag, moveGapX, moveGapY);
            //

            tag = rootTag; //다음 순서로 넘어가기

            if(changeFlag){
                tag = changeTagFlag;
                changeFlag = false;
            }
        }

        header.SetPath(findDrawPathById(index));

        canvas.invalidate();
    }

    public static void ConditionMove(FlowTag.Condition condition, float moveGapX, float moveGapY){
        ConditionLineMove(condition, moveGapX, moveGapY);
        ConditionRootTagMove(condition, moveGapX, moveGapY);
    }

    public static void ConditionRootTagMove(FlowTag.Condition condition, float moveGapX, float moveGapY){
        boolean tmpChangeFlag = false;

        boolean YesFlag = false;
        boolean NoFlag = false;

        if(!condition.GetYesNext().equals("")) {
            Log.v("Flow Mover", "ConditionRootTagMove - YES");
            Tag YesTag = Tag.findTagByUUID(condition.GetYesNext());
            Tag tag = YesTag;

            YesTag.SetX(YesTag.GetX() - moveGapX);
            YesTag.SetY(YesTag.GetY() - moveGapY);
            YesTag.SetPath(findDrawPathById(Flows.indexOf(YesTag)));

            EditFlowZOrder(YesTag);
            CheckSpecialFlow(YesTag, moveGapX, moveGapY);

            if (changeFlag) {
                tag = changeTagFlag;
                changeFlag = false;
            }

            while (!tag.GetNext().equals("")) {
                Tag findTag = Tag.findTagByUUID(tag.GetNext());

                if (findTag.ConditionEndable) break;

                findTag.SetX(findTag.GetX() - moveGapX);
                findTag.SetY(findTag.GetY() - moveGapY);
                findTag.SetPath(findDrawPathById(Flows.indexOf(findTag)));

                EditFlowZOrder(findTag);
                CheckSpecialFlow(findTag, moveGapX, moveGapY);

                //선 이동
                MoveLinePath(tag, findTag, moveGapX, moveGapY);

                tag = findTag;

                if (changeFlag) {
                    tag = changeTagFlag;
                    changeFlag = false;
                }
            }

            changeTagFlag = tag;
            tmpChangeFlag = true;
            YesFlag = true;
        }

        if(!condition.GetNoNext().equals("")) {
            Log.v("Flow Mover", "ConditionRootTagMove - NO");
            Tag NoTag = Tag.findTagByUUID(condition.GetNoNext());
            Tag tag = NoTag;

            NoTag.SetX(NoTag.GetX() - moveGapX);
            NoTag.SetY(NoTag.GetY() - moveGapY);
            NoTag.SetPath(findDrawPathById(Flows.indexOf(NoTag)));

            EditFlowZOrder(NoTag);
            CheckSpecialFlow(NoTag, moveGapX, moveGapY);

            if (changeFlag) {
                tag = changeTagFlag;
                changeFlag = false;
            }

            while (!tag.GetNext().equals("")) {
                Tag findTag = Tag.findTagByUUID(tag.GetNext());

                if (findTag.ConditionEndable) break;

                findTag.SetX(findTag.GetX() - moveGapX);
                findTag.SetY(findTag.GetY() - moveGapY);
                findTag.SetPath(findDrawPathById(Flows.indexOf(findTag)));

                EditFlowZOrder(findTag);
                CheckSpecialFlow(findTag, moveGapX, moveGapY);

                //선 이동
                MoveLinePath(tag, findTag, moveGapX, moveGapY);

                tag = findTag;

                if (changeFlag) {
                    tag = changeTagFlag;
                    changeFlag = false;
                }
            }

            if(YesFlag){
                Tag yestag = changeTagFlag;
                if(!yestag.GetNext().equals("")){
                    Tag roottag = Tag.findTagByUUID(yestag.GetNext());
                    MoveLinePath(yestag, roottag, moveGapX, moveGapY);
                }
            }

            changeTagFlag = tag;
            tmpChangeFlag = true;
            NoFlag = true;

        }

        if(tmpChangeFlag){
            changeFlag = true;
        }
    }

    public static void ConditionLineMove(FlowTag.Condition condition, float moveGapX, float moveGapY){
        if(!condition.YesNext.equals("")){
            condition.linePathX = condition.YesXList;
            condition.linePathY = condition.YesYList;

            Tag YesNext = Tag.findTagByUUID(condition.GetYesNext());

            MoveLinePath(condition, YesNext, moveGapX, moveGapY);

            condition.YesXList = condition.linePathX;
            condition.YesYList = condition.linePathY;
            condition.YesLine = condition.GetLinePath();
        }

        if(!condition.NoNext.equals("")){
            condition.linePathX = condition.NoXList;
            condition.linePathY = condition.NoYList;

            Tag NoNext = Tag.findTagByUUID(condition.GetNoNext());

            MoveLinePath(condition, NoNext, moveGapX, moveGapY);

            condition.NoXList = condition.linePathX;
            condition.NoYList = condition.linePathY;
            condition.NoLine = condition.GetLinePath();
        }

    }


    public static void MoveToRepeat(Tag header, FlowTag.Repeat repeat){ //Repeat로 이동해 포함시켜야하는경우
        Log.v("FlowMover","MoveToRepeat");
        //해당 도형에 오면 안되는 도형 반환 함수, 클래스
        Tag originalTag = Tag.findTagByUUID(repeat.GetHeader());
        if(!repeat.GetHeader().equals("") && !header.equals(originalTag)){ //이미 헤더가 존재하는 경우 이동실패
            MoveFailed(header);
            return;
        }

        repeat.SetHeader(header.GetUUID()); //Repeat에 헤더를 포함시켜줌으로써/ Repeat에 순서도 추가
        //Header로부터 Move의 Gap을 구한뒤
        float startX = repeat.GetInternalCodeX();
        float startY = repeat.GetInternalCodeY();

        float moveGapX = header.GetX() - startX;
        float moveGapY = header.GetY() - startY;

        header.SetX(startX);
        header.SetY(startY);
        header.SetPath(findDrawPathById(Flows.indexOf(header)));

        EditFlowZOrder(header);
        CheckSpecialFlow(header, moveGapX, moveGapY); //header에 대한 special 도형 검사

        Tag tag = header; //탐색을 위한 초기값 설정

        if(changeFlag){
            tag = changeTagFlag;
            changeFlag = false;
        }

        while(!tag.GetNext().equals("")){
            Tag rootTag = Tag.findTagByUUID(tag.GetNext());
            rootTag.SetX(rootTag.GetX() - moveGapX);
            rootTag.SetY(rootTag.GetY() - moveGapY);
            rootTag.SetPath(findDrawPathById(Flows.indexOf(rootTag)));

            EditFlowZOrder(rootTag);
            CheckSpecialFlow(rootTag, moveGapX, moveGapY); //rootTag에 대한 special 도형 검사

            MoveLinePath(tag, rootTag, moveGapX, moveGapY);

            tag = rootTag;

            if(changeFlag){
                tag = changeTagFlag;
                changeFlag = false;
            }
        }

        repeat.SetPath(findDrawPathById(Flows.indexOf(repeat)));
        canvas.invalidate();
    }

    public static void MoveOfRepeat(FlowTag.Repeat repeat){
        //Rpeat가 이동하는 경우 : FlowMove & MoveToRepeat에서 사용됨

        if(!repeat.RepeatHeader.equals(""))
            MoveToRepeat(Tag.findTagByUUID(repeat.GetHeader()), repeat);

    }

    public static void DisconnectFromRepeat(Tag header, FlowTag.Repeat repeat){ //repeat 헤더 연결 끊기
        repeat.SetHeader("");
        repeat.SetPath(findDrawPathById(Flows.indexOf(repeat)));

        FlowStudio.canvas.invalidate();
    }

    public static void MoveFailed(Tag header){ //보류
        //이동에 실패한 순서도 제자리로 돌려놓기
        Tag tag = header;

        float moveGapX = header.GetX() - FlowCanvas.DownX;
        float moveGapY = header.GetY() - FlowCanvas.DownY;

        while(!tag.GetNext().equals("")){
            Tag rootTag = Tag.findTagByUUID(tag.GetNext());
            rootTag.SetX(tag.GetX() - moveGapX);
            rootTag.SetY(tag.GetY() - moveGapY);

            MoveLinePath(tag, rootTag, moveGapX, moveGapY);

            rootTag.SetPath(findDrawPathById(Flows.indexOf(rootTag)));

            tag = rootTag;
        }

        canvas.invalidate();
    }


    public static void MoveLinePath(Tag tag, Tag rootTag, float moveGapX, float moveGapY){
        List<Float> xList = new ArrayList<>();
        List<Float> yList = new ArrayList<>();

        Iterator<Float> itrX = tag.GetLinePathX().iterator();
        Iterator<Float> itrY = tag.GetLinePathY().iterator();

        Path linePath = new Path();
        if(itrX.hasNext() && itrY.hasNext())
            linePath.moveTo(tag.GetLinePathX().get(0) - moveGapX, tag.GetLinePathY().get(0) - moveGapY);

        while(itrX.hasNext() && itrY.hasNext()){
            float tmpX, tmpY;
            tmpX = itrX.next();
            tmpY = itrY.next();

            tmpX -= moveGapX;
            tmpY -= moveGapY;

            xList.add(tmpX);
            yList.add(tmpY);

            linePath.lineTo(tmpX, tmpY);
        }

        tag.SetLinePathX(xList);
        tag.SetLinePathY(yList);
        tag.SetLinePath(linePath);

        float nextX = xList.get(xList.size()-1);
        float nextY = yList.get(yList.size()-1);

        CreateLineArrow(tag, rootTag, nextX, nextY);
    }

    private static void CheckSpecialFlow(Tag tag, float moveGapX, float moveGapY){ //MoveOf를 쓰기 위함
        int id = tag.GetKind();

        switch (id){
            case R.id.side_repeat :
                MoveOfRepeat((FlowTag.Repeat)tag);
                break;
            case R.id.side_condition :
                ConditionMove((FlowTag.Condition)tag, moveGapX, moveGapY);
        }
    }

    public static void CheckInSpecialFlow(Tag tag, float x, float y){
        //해당좌표에 2개이상의 스페셜 플로우가 있다면 중첩으로 간주
        // 더 작은 크기의 플로우에 종속 -> 그럴필요 없이 Zorder값이 더 높은 쪽으로 하면되잖아!

        for(int i = Flows.size()-1; i >= 0; i--){ //Flow Zorder를 위한 역 탐색
            Tag findTag = Flows.get(i);

            if( !(findTag.GetLeft() < x && findTag.GetRight() > x && findTag.GetTop() < y && findTag.GetBottom() > y) ){
                continue; //해당 위치에 도형이 없다면 돌아가기
            }

            if(CheckAutoSizingFlow(findTag) && !tag.equals(findTag)){ //오토사이징 도형이라면
                int id = findTag.GetKind();
                switch (id){
                    case R.id.side_repeat :
                        MoveToRepeat(tag, (FlowTag.Repeat)Flows.get(i));
                        break;
                }
            }
        }
        //탐색끝
    }

    private static boolean CheckAutoSizingFlow(Tag tag){
        int id = tag.GetKind();
        switch (id){
            case R.id.side_repeat:
                return true;

            default:
                return false;
        }
    }

    public static void CheckDisConnectSpecialFlow(Tag header){
        Iterator<Tag> itr = Flows.iterator();
        while(itr.hasNext()){
            Tag findTag = itr.next();

            switch(findTag.GetKind()){
                case R.id.side_repeat :
                    FlowTag.Repeat repeat = (FlowTag.Repeat)findTag;
                    if(header.GetUUID().equals(repeat.GetHeader())){
                        DisconnectFromRepeat(header, repeat);
                    }

                    break;
            }

        }
    }

    private static void PrevDisconnect(Tag header){
        //Prev있으면 도형 간 연결해제 하는 기능 작성
        if(!header.GetPrev().equals("")){
            Tag prevTag = Tag.findTagByUUID(header.GetPrev());
            prevTag.SetLinePath(new Path());
            prevTag.SetNext("");

            header.SetPrev("");

            header.ConditionEndable = false;
        }
    }




}
