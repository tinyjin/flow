package com.youjinui.flow;

import android.graphics.Path;
import android.graphics.RectF;

import static com.youjinui.flow.FlowAutoSizer.RepeatAutoSizing;
import static com.youjinui.flow.FlowResolution.Cheory.heightC;
import static com.youjinui.flow.FlowResolution.Cheory.marginC;
import static com.youjinui.flow.FlowResolution.Condition.heightMagnificationCo;
import static com.youjinui.flow.FlowResolution.Condition.marginCo;
import static com.youjinui.flow.FlowResolution.EndResolution.heightE;
import static com.youjinui.flow.FlowResolution.EndResolution.marginE;
import static com.youjinui.flow.FlowResolution.EndResolution.roundE;
import static com.youjinui.flow.FlowResolution.Input.GradientI;
import static com.youjinui.flow.FlowResolution.Input.heightI;
import static com.youjinui.flow.FlowResolution.Input.marginI;
import static com.youjinui.flow.FlowResolution.Output.CurveI;
import static com.youjinui.flow.FlowResolution.Output.heightO;
import static com.youjinui.flow.FlowResolution.Output.marginO;
import static com.youjinui.flow.FlowResolution.Ready.RdipX;
import static com.youjinui.flow.FlowResolution.Ready.RdipY;
import static com.youjinui.flow.FlowResolution.Ready.marginR;
import static com.youjinui.flow.FlowResolution.StartResolution.heightS;
import static com.youjinui.flow.FlowResolution.StartResolution.marginS;
import static com.youjinui.flow.FlowResolution.StartResolution.roundS;
import static com.youjinui.flow.FlowResolution.funcEnd.heightfE;
import static com.youjinui.flow.FlowResolution.funcEnd.marginfE;
import static com.youjinui.flow.FlowResolution.funcEnd.roundfE;
import static com.youjinui.flow.FlowResolution.funcStart.heightfS;
import static com.youjinui.flow.FlowResolution.funcStart.marginfS;
import static com.youjinui.flow.FlowResolution.funcStart.roundfS;
import static com.youjinui.flow.FlowResolution.funcUse.heightfU;
import static com.youjinui.flow.FlowResolution.funcUse.marginfU;
import static com.youjinui.flow.FlowStudio.Flows;
import static com.youjinui.flow.FlowStudio.canvas;

/**
 * 플로우 도형 그리기
 */

public class FlowDrawer {

    public static Path findDrawPathById(int index){
        //id로 부터 Path그려주는 함수
        Path path;
        float x = Flows.get(index).GetX();
        float y = Flows.get(index).GetY();

        int id = Flows.get(index).GetKind();

        switch (id) {
            case R.id.side_start:
                path = DrawStart(index, x, y);
                break;

            case R.id.side_end:
                path = DrawEnd(index, x, y);
                break;

            case R.id.side_ready:
                path = DrawReady(index, x, y);
                break;

            case R.id.side_cheory:
                path = DrawCheory(index, x, y);
                break;

            case R.id.side_input:
                path = DrawInput(index, x, y);
                break;

            case R.id.side_output:
                path = DrawOutput(index, x, y);
                break;

            case R.id.side_condition:
                path = DrawCondition(index, x, y);
                break;

            case R.id.side_repeat:
                path = DrawRepeat(index, x, y);
                break;

            case R.id.side_func_start:
                path = DrawfuncStart(index, x, y);
                break;

            case R.id.side_func_end:
                path = DrawfuncEnd(index, x, y);
                break;

            case R.id.side_func_use:
                path = DrawfuncUse(index, x, y);
                break;
            default:
                path = new Path();
                break;
        }

        return path;
    }

    public static float getTextSize(String text){ //텍스트 사이즈 알아내는 함수
        float TextSize;
        if(text.isEmpty()){ //텍스트가 비어있다면 (Start, End 해당 없음)
            TextSize = 0; //도형별 기본 사이즈 반환 margin
        }
        else{
            //FlowCanvas로부터 measureText 알아내서 반환
            TextSize = canvas.TextPaint.measureText(text);
        }

        return TextSize;
    }

    public static Path DrawStart(int index, float x, float y) {
        Path path = new Path();
        String text = "Start"; //Start 도형은 Text값 고정
        Flows.get(index).SetText(text);

        float tx; //text X
        tx = getTextSize(text) + marginS;

        path.addRoundRect(new RectF(x, y, x + tx, y + heightS), roundS, roundS, Path.Direction.CCW);

        Flows.get(index).SetWidth(tx);
        Flows.get(index).SetHeight(heightS);

        return path;
    }

    public static Path DrawEnd(int index, float x, float y) {
        Path path = new Path();
        String text = "End"; //Start 도형은 Text값 고정
        Flows.get(index).SetText(text);

        float tx; //text X
        tx = getTextSize(text) + marginE;

        path.addRoundRect(new RectF(x, y, x + tx, y + heightE), roundE, roundE, Path.Direction.CCW);

        Flows.get(index).SetWidth(tx);
        Flows.get(index).SetHeight(heightE);

        return path;
    }

    public static Path DrawReady(int index, float x, float y) {
        Path path = new Path();
        Tag tag = Flows.get(index);

        String text = tag.GetText();

        path.moveTo(x + RdipX, y);

        float tx;
        tx = getTextSize(text) + marginR;

        path.rLineTo(tx, 0);
        path.rLineTo(RdipX, RdipY);
        path.rLineTo(-RdipX, RdipY);
        path.rLineTo(-tx, 0);
        path.rLineTo(-RdipX, -RdipY);
        path.rLineTo(RdipX, -RdipY);

        tag.SetWidth(tx + RdipX * 2);
        tag.SetHeight(RdipY * 2);

        return path;
    }

    public static Path DrawCheory(int index, float x, float y) {
        Path path = new Path();
        Tag tag = Flows.get(index);

        float tx;
        tx = getTextSize(tag.GetText()) + marginC;

        path.moveTo(x, y);
        path.addRect(x, y, x + tx, y + heightC, Path.Direction.CW);

        tag.SetWidth(tx);
        tag.SetHeight(heightC);

        return path;
    }

    public static Path DrawInput(int index, float x, float y) {
        Path path = new Path();
        Tag tag = Flows.get(index);
        path.moveTo(x + GradientI, y);

        float tx;
        tx = getTextSize(tag.GetText()) + marginI;

        path.rLineTo(tx, 0);
        path.rLineTo(-GradientI, heightI);
        path.rLineTo(-tx, 0);
        path.lineTo(x + GradientI, y);

        tag.SetWidth(tx + GradientI);
        tag.SetHeight(heightI);

        return path;
    }

    public static Path DrawOutput(int index, float x, float y) {
        Path path = new Path();
        Tag tag = Flows.get(index);

        path.moveTo(x, y);

        float tx;
        tx = getTextSize(tag.GetText()) + marginO;

        path.rLineTo(tx, 0);
        path.rLineTo(0, heightO);
        path.rCubicTo(0, 0, -(tx / 4), -CurveI, -(tx / 2), 0);
        path.rCubicTo(0, 0, -(tx / 4), CurveI, -(tx / 2), 0);
        path.lineTo(x, y);

        tag.SetWidth(tx);
        tag.SetHeight(heightO);

        return path;
    }

    public static Path DrawCondition(int index, float x, float y) {
        Path path = new Path();
        Tag tag = Flows.get(index);

        float tx, ty;
        tx = getTextSize(tag.GetText()) + marginCo;
        ty = tx / heightMagnificationCo;

        path.moveTo(x, y + ty / 2);
        path.rLineTo(tx/2, -ty / 2);
        path.rLineTo(tx / 2, ty / 2);
        path.rLineTo(-tx / 2, ty / 2);
        path.rLineTo(-tx / 2, -ty / 2);

        tag.SetWidth(tx);
        tag.SetHeight(ty);

        return path;
    }

    public static Path DrawRepeat(int index, float x, float y) { //width height 먼저 설정뒤, Get함수로 좌표그리기
        Path path = new Path();
        FlowTag.Repeat repeat = (FlowTag.Repeat)Flows.get(index);
        RepeatAutoSizing(repeat); //width height 설정

        path.moveTo(x, y);
        path.addRect(repeat.GetLeft(), repeat.GetTop(), repeat.GetRight(), repeat.GetBottom(), Path.Direction.CCW);
        path.moveTo(repeat.GetLeft(), repeat.GetTop() + FlowResolution.Repeat.titleGap);
        path.lineTo(repeat.GetRight(), repeat.GetTop() + FlowResolution.Repeat.titleGap);

        return path;
    }

    public static Path DrawfuncStart(int index, float x, float y) {
        Path path = new Path();
        Tag tag = Flows.get(index);
        String text = tag.GetText(); //Start 도형은 Text값 고정

        float tx; //text X
        tx = getTextSize(text) + marginfS;

        path.addRoundRect(new RectF(x, y, x + tx, y + heightfS), roundfS, roundfS, Path.Direction.CCW);

        tag.SetWidth(tx);
        tag.SetHeight(heightfS);

        return path;
    }

    public static Path DrawfuncEnd(int index, float x, float y) {
        Path path = new Path();
        Tag tag = Flows.get(index);
        String text = tag.GetText();

        float tx;
        tx = getTextSize(text) + marginfE;

        path.addRoundRect(new RectF(x, y, x + tx, y + heightfE), roundfE, roundfE, Path.Direction.CCW);

        tag.SetWidth(tx);
        tag.SetHeight(heightfE);

        return path;
    }

    public static Path DrawfuncUse(int index, float x, float y) {
        Path path = new Path();
        Tag tag = Flows.get(index);

        float tx;
        tx = getTextSize(tag.GetText()) + marginfU;

        path.moveTo(x, y);
        path.addRect(x, y, x + tx, y + heightfU, Path.Direction.CW);

        tag.SetWidth(tx);
        tag.SetHeight(heightfU);

        return path;
    }
}
